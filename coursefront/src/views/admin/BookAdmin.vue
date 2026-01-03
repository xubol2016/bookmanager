<template>
  <div class="book-admin-container">
    <div class="header">
      <h2>图书管理</h2>
      <div class="actions">
        <el-button type="primary" @click="goAdd">新增图书</el-button>
        <el-button type="danger" :disabled="selection.length === 0" @click="handleBatchDelete">批量删除</el-button>
      </div>
    </div>
    
    <!-- Filter -->
    <div class="filter-bar mb-4">
        <el-input v-model="queryParams.title" placeholder="书名" style="width: 150px" class="mr-2" clearable @keyup.enter="loadBooks"/>
        <el-input v-model="queryParams.author" placeholder="作者" style="width: 150px" class="mr-2" clearable @keyup.enter="loadBooks"/>
        <el-button @click="loadBooks">搜索</el-button>
    </div>

    <!-- Table -->
    <el-table 
        v-loading="loading" 
        :data="bookList" 
        style="width: 100%" 
        @selection-change="handleSelectionChange"
        border
    >
      <el-table-column type="selection" width="55" />
      <el-table-column label="封面" width="80">
        <template #default="scope">
            <el-image 
                style="width: 50px; height: 70px"
                :src="getBookCover(scope.row.coverUrl)"
                :preview-src-list="[getBookCover(scope.row.coverUrl)]"
                fit="cover"
            />
        </template>
      </el-table-column>
      <el-table-column prop="title" label="书名" min-width="150" show-overflow-tooltip/>
      <el-table-column prop="author" label="作者" width="120" />
      <el-table-column prop="isbn" label="ISBN" width="130" />
      <el-table-column prop="stock" label="库存" width="80" align="center"/>
      <el-table-column label="状态" width="80" align="center">
          <template #default="scope">
              <el-tag :type="scope.row.status === 1 ? 'success' : 'info'">
                  {{ scope.row.status === 1 ? '上架' : '下架' }}
              </el-tag>
          </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="scope">
          <el-button size="small" @click="handleEdit(scope.row)">编辑</el-button>
          <el-button size="small" type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container mt-4">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        layout="total, sizes, prev, pager, next"
        @size-change="loadBooks"
        @current-change="loadBooks"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useBookStore } from '@/stores/book'
import { storeToRefs } from 'pinia'
import { ElMessage, ElMessageBox } from 'element-plus'

const router = useRouter()
const bookStore = useBookStore()
const { bookList, total, loading } = storeToRefs(bookStore)

const queryParams = reactive({
    page: 1,
    size: 10,
    title: '',
    author: ''
})

const selection = ref([])

const getBookCover = (url) => {
    if (!url) return ''
    if (url.startsWith('http')) return url
    return `/api/${url}`
}

const loadBooks = () => {
    bookStore.fetchBooks(queryParams)
}

const handleSelectionChange = (val) => {
    selection.value = val
}

const goAdd = () => {
    router.push('/admin/books/add')
}

const handleEdit = (row) => {
    router.push(`/admin/books/edit/${row.id}`)
}

const handleDelete = (row) => {
    ElMessageBox.confirm(`确定删除图书 "${row.title}" 吗?`, '警告', {
        type: 'warning'
    }).then(async () => {
        try {
            await bookStore.removeBook(row.id)
            ElMessage.success('删除成功')
            loadBooks()
        } catch (e) {
            // failed
        }
    })
}

const handleBatchDelete = () => {
    if (selection.value.length === 0) return
    ElMessageBox.confirm(`确定删除选中的 ${selection.value.length} 本图书吗?`, '警告', {
        type: 'warning'
    }).then(async () => {
        try {
            const ids = selection.value.map(item => item.id)
            await bookStore.removeBooks(ids)
            ElMessage.success('批量删除成功')
            loadBooks()
        } catch (e) {
            // failed
        }
    })
}

onMounted(() => {
    loadBooks()
})
</script>

<style scoped>
.book-admin-container {
    padding: 20px;
    background: #fff;
    min-height: 100vh;
}
.header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
}
.filter-bar {
    display: flex;
    align-items: center;
}
.mb-4 { margin-bottom: 20px; }
.mt-4 { margin-top: 20px; }
.mr-2 { margin-right: 10px; }
.pagination-container {
    display: flex;
    justify-content: flex-end;
}
</style>
