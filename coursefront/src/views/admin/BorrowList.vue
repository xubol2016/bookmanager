<template>
  <div class="borrow-list-container p-4">
    <el-card>
      <template #header>
        <div class="flex justify-between items-center">
          <span class="text-lg font-bold">借阅管理</span>
        </div>
      </template>

      <!-- Filter -->
      <div class="mb-4 flex gap-4">
          <el-input v-model="queryParams.userId" placeholder="用户ID" style="width: 200px" clearable @clear="fetchData" />
          <el-input v-model="queryParams.bookId" placeholder="图书ID" style="width: 200px" clearable @clear="fetchData" />
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 120px" @change="fetchData">
              <el-option label="借阅中" :value="1" />
              <el-option label="已归还" :value="2" />
              <el-option label="已超期" :value="3" />
          </el-select>
          <el-button type="primary" @click="fetchData">查询</el-button>
      </div>

      <el-table :data="tableData" v-loading="loading" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userId" label="用户ID" width="80" />
        <el-table-column prop="bookId" label="图书ID" width="80" />
        <el-table-column label="借阅时间" min-width="160">
            <template #default="scope">{{ formatDate(scope.row.borrowTime) }}</template>
        </el-table-column>
        <el-table-column label="应还时间" min-width="160">
             <template #default="scope">{{ formatDate(scope.row.dueTime) }}</template>
        </el-table-column>
        <el-table-column label="归还时间" min-width="160">
             <template #default="scope">{{ scope.row.returnTime ? formatDate(scope.row.returnTime) : '-' }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
             <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ getStatusText(scope.row.status) }}</el-tag>
            </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" />
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
    size: 10,
    userId: '',
    bookId: '',
    status: undefined
})

const getStatusType = (status) => {
    const map = { 1: 'primary', 2: 'success', 3: 'danger' }
    return map[status] || 'info'
}

const getStatusText = (status) => {
    const map = { 1: '借阅中', 2: '已归还', 3: '已超期' }
    return map[status] || '未知'
}

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    return new Date(dateStr).toLocaleString()
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await borrowStore.fetchAdminList(queryParams)
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
