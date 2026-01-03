<template>
  <div class="my-borrow-container p-4">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">我的借阅</span>
        </div>
      </template>

      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="全部" name="all"></el-tab-pane>
        <el-tab-pane label="借阅中" name="1"></el-tab-pane>
        <el-tab-pane label="已归还" name="2"></el-tab-pane>
        <el-tab-pane label="已超期" name="3"></el-tab-pane>
      </el-tabs>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="记录ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <!-- Note: Backend only returns Book ID currently. 
             Ideally should return Title. 
             If title is missing, we might show ID or fetch book detail.
             For now show ID. -->
        <el-table-column label="借阅时间" min-width="160">
            <template #default="scope">
                {{ formatDate(scope.row.borrowTime) }}
            </template>
        </el-table-column>
        <el-table-column label="应还时间" min-width="160">
             <template #default="scope">
                {{ formatDate(scope.row.dueTime) }}
            </template>
        </el-table-column>
        <el-table-column label="归还时间" min-width="160">
             <template #default="scope">
                {{ scope.row.returnTime ? formatDate(scope.row.returnTime) : '-' }}
            </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
            <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">
                    {{ getStatusText(scope.row.status) }}
                </el-tag>
            </template>
        </el-table-column>
        <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
                <el-button 
                    v-if="scope.row.status === 1 || scope.row.status === 3" 
                    type="primary" 
                    link 
                    @click="handleReturn(scope.row)"
                >
                    归还
                </el-button>
            </template>
        </el-table-column>
      </el-table>

      <div class="mt-4 flex justify-end">
        <el-pagination
            v-model:current-page="queryParams.page"
            v-model:page-size="queryParams.size"
            :total="total"
            layout="total, prev, pager, next"
            @current-change="fetchData"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive } from 'vue'
import { useBorrowStore } from '@/stores/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const borrowStore = useBorrowStore()
const activeTab = ref('all')
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
    page: 1,
    size: 10,
    status: undefined
})

const getStatusType = (status) => {
    const map = {
        1: 'primary',
        2: 'success',
        3: 'danger'
    }
    return map[status] || 'info'
}

const getStatusText = (status) => {
    const map = {
        1: '借阅中',
        2: '已归还',
        3: '已超期'
    }
    return map[status] || '未知'
}

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    return new Date(dateStr).toLocaleString()
}

const handleTabChange = (val) => {
    if (val === 'all') {
        queryParams.status = undefined
    } else {
        queryParams.status = parseInt(val)
    }
    queryParams.page = 1
    fetchData()
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await borrowStore.fetchMyRecords(queryParams)
        if (res && res.records) {
            tableData.value = res.records
            total.value = res.total
        }
    } catch (e) {
        // quiet
    } finally {
        loading.value = false
    }
}

const handleReturn = (row) => {
    ElMessageBox.confirm(
        '确定要归还这本图书吗?',
        '归还确认',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const res = await borrowStore.returnBookAction(row.id)
        if (res) {
            ElMessage.success('归还成功')
            fetchData()
        }
    })
}

onMounted(() => {
    fetchData()
})
</script>

<style scoped>
.my-borrow-container {
    max-width: 1200px;
    margin: 0 auto;
}
</style>
