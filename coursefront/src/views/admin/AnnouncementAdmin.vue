<template>
  <div class="announcement-admin p-4">
      <el-card>
          <template #header>
              <div class="flex justify-between items-center">
                  <span class="font-bold text-lg">公告管理</span>
                  <el-button type="primary" @click="handleAdd">发布公告</el-button>
              </div>
          </template>

          <el-table :data="tableData" border stripe v-loading="loading">
              <el-table-column prop="id" label="ID" width="80" />
              <el-table-column prop="title" label="标题" min-width="200" show-overflow-tooltip />
              <el-table-column prop="isTop" label="置顶" width="100">
                  <template #default="{ row }">
                      <el-tag :type="row.isTop ? 'danger' : 'info'">{{ row.isTop ? '是' : '否' }}</el-tag>
                  </template>
              </el-table-column>
              <el-table-column prop="publishTime" label="发布时间" width="180">
                  <template #default="{ row }">
                      {{ formatDate(row.publishTime) }}
                  </template>
              </el-table-column>
              <el-table-column label="操作" width="180" fixed="right">
                  <template #default="{ row }">
                      <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
                      <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
                  </template>
              </el-table-column>
          </el-table>
          
          <div class="mt-4 flex justify-end">
              <el-pagination
                  background
                  layout="prev, pager, next"
                  :total="total"
                  :page-size="pageSize"
                  @current-change="handlePageChange"
              />
          </div>
      </el-card>

      <!-- Dialog -->
      <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑公告' : '发布公告'" width="600px">
          <el-form :model="form" label-width="80px">
              <el-form-item label="标题" required>
                  <el-input v-model="form.title" placeholder="请输入公告标题" />
              </el-form-item>
              <el-form-item label="内容" required>
                  <el-input type="textarea" v-model="form.content" rows="6" placeholder="请输入公告内容" />
              </el-form-item>
              <el-form-item label="置顶">
                  <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
              </el-form-item>
          </el-form>
          <template #footer>
              <el-button @click="dialogVisible = false">取消</el-button>
              <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
          </template>
      </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAnnouncementPage, addAnnouncement, updateAnnouncement, deleteAnnouncement } from '@/api/announcement'
import { ElMessage, ElMessageBox } from 'element-plus'

const tableData = ref([])
const loading = ref(false)
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const submitLoading = ref(false)
const form = reactive({
    id: null,
    title: '',
    content: '',
    isTop: 0
})

const formatDate = (dateStr) => {
    if (!dateStr) return '-'
    const d = new Date(dateStr)
    return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,0)}-${String(d.getDate()).padStart(2,0)}`
}

const fetchData = async () => {
    loading.value = true
    try {
        const res = await getAnnouncementPage({
            page: currentPage.value,
            size: pageSize.value
        })
        if (res) {
            tableData.value = res.records
            total.value = res.total
        }
    } finally {
        loading.value = false
    }
}

const handlePageChange = (val) => {
    currentPage.value = val
    fetchData()
}

const handleAdd = () => {
    isEdit.value = false
    form.id = null
    form.title = ''
    form.content = ''
    form.isTop = 0
    dialogVisible.value = true
}

const handleEdit = (row) => {
    isEdit.value = true
    form.id = row.id
    form.title = row.title
    form.content = row.content
    form.isTop = row.isTop
    dialogVisible.value = true
}

const handleDelete = (row) => {
    ElMessageBox.confirm('确定要删除该公告吗?', '提示', { type: 'warning' })
        .then(async () => {
            const res = await deleteAnnouncement(row.id)
            if (res) {
                ElMessage.success('删除成功')
                fetchData()
            }
        })
}

const submitForm = async () => {
    if (!form.title || !form.content) {
        ElMessage.warning('请填写完整信息')
        return
    }

    submitLoading.value = true
    try {
        let res
        if (isEdit.value) {
            res = await updateAnnouncement(form)
        } else {
            res = await addAnnouncement(form)
        }

        if (res) {
            ElMessage.success(isEdit.value ? '更新成功' : '发布成功')
            dialogVisible.value = false
            fetchData()
        }
    } finally {
        submitLoading.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>
