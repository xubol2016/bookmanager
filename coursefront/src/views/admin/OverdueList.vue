<template>
  <div class="overdue-list-container p-4">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold text-red-500">超期监控</span>
          <el-button type="warning" @click="fetchData">刷新数据</el-button>
        </div>
      </template>

      <el-alert title="以下记录已超过归还期限，请及时联系用户归还。" type="warning" show-icon class="mb-4" />

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column label="借阅时间" width="180">
            <template #default="scope">{{ formatDate(scope.row.borrowTime) }}</template>
        </el-table-column>
        <el-table-column label="应还时间" width="180">
             <template #default="scope">
                 <span class="text-red-500 font-bold">{{ formatDate(scope.row.dueTime) }}</span>
             </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
             <template #default="scope">
                <el-tag type="danger">已超期</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
        <el-table-column label="操作" width="120" fixed="right">
            <template #default="scope">
                 <el-button type="primary" link size="small">通知用户</el-button>
                 <!-- Future feature: Send Email/SMS -->
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

const borrowStore = useBorrowStore()
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
    page: 1,
    size: 10
})

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    return new Date(dateStr).toLocaleString()
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await borrowStore.fetchOverdueList(queryParams)
        if (res && res.records) {
            tableData.value = res.records
            total.value = res.total
        }
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>
