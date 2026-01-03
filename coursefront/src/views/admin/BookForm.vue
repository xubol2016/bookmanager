<template>
  <div class="book-form-container">
    <el-page-header @back="cancel" :content="isEdit ? '编辑图书' : '新增图书'" class="mb-4" />
    
    <el-card>
        <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
            <el-form-item label="图书名称" prop="title">
                <el-input v-model="form.title" placeholder="请输入图书名称" />
            </el-form-item>
            
            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="作者" prop="author">
                        <el-input v-model="form.author" placeholder="作者" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                     <el-form-item label="出版社" prop="publisher">
                        <el-input v-model="form.publisher" placeholder="出版社" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                    <el-form-item label="ISBN" prop="isbn">
                        <el-input v-model="form.isbn" placeholder="ISBN" />
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                     <el-form-item label="分类ID" prop="categoryId">
                        <el-input-number v-model="form.categoryId" style="width: 100%"/>
                    </el-form-item>
                </el-col>
            </el-row>

            <el-row :gutter="20">
                <el-col :span="12">
                     <el-form-item label="库存" prop="stock">
                        <el-input-number v-model="form.stock" :min="0" style="width: 100%"/>
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                     <el-form-item label="位置" prop="location">
                        <el-input v-model="form.location" placeholder="存放位置" />
                    </el-form-item>
                </el-col>
            </el-row>

            <el-form-item label="状态" prop="status">
                <el-radio-group v-model="form.status">
                    <el-radio :value="1">上架</el-radio>
                    <el-radio :value="0">下架</el-radio>
                </el-radio-group>
            </el-form-item>

            <el-form-item label="封面" prop="coverUrl">
                <el-upload
                    class="avatar-uploader"
                    action="#"
                    :http-request="handleUpload"
                    :show-file-list="false"
                    :before-upload="beforeUpload"
                >
                    <img v-if="form.coverUrl" :src="getBookCover(form.coverUrl)" class="avatar" />
                    <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
                </el-upload>
                <div class="el-upload__tip">只能上传jpg/png文件，且不超过2MB</div>
            </el-form-item>

            <el-form-item label="简介" prop="description">
                <el-input v-model="form.description" type="textarea" :rows="4" />
            </el-form-item>

            <el-form-item>
                <el-button type="primary" @click="submitForm" :loading="loading">保存</el-button>
                <el-button @click="cancel">取消</el-button>
            </el-form-item>
        </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBookStore } from '@/stores/book'
import { ElMessage } from 'element-plus'
import { Plus } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()

const formRef = ref(null)
const loading = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
    id: undefined,
    title: '',
    author: '',
    publisher: '',
    isbn: '',
    categoryId: undefined,
    stock: 0,
    location: '',
    status: 1,
    description: '',
    coverUrl: ''
})

const rules = {
    title: [{ required: true, message: '请输入图书名称', trigger: 'blur' }],
    author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
    isbn: [{ required: true, message: '请输入ISBN', trigger: 'blur' }]
}

const getBookCover = (url) => {
    if (!url) return ''
    if (url.startsWith('http')) return url
    return `/api/${url}`
}

const beforeUpload = (file) => {
    const isImg = file.type === 'image/jpeg' || file.type === 'image/png'
    const isLt2M = file.size / 1024 / 1024 < 2

    if (!isImg) {
        ElMessage.error('上传封面图片只能是 JPG/PNG 格式!')
    }
    if (!isLt2M) {
        ElMessage.error('上传封面图片大小不能超过 2MB!')
    }
    return isImg && isLt2M
}

const handleUpload = async (options) => {
    try {
        const res = await bookStore.uploadBookCover(options.file)
        if (res) {
            form.coverUrl = res
            ElMessage.success('上传成功')
        } else {
            ElMessage.error('上传失败')
        }
    } catch (e) {
        ElMessage.error('上传出错')
    }
}

const submitForm = async () => {
    if (!formRef.value) return
    await formRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                if (isEdit.value) {
                    await bookStore.updateBook(form)
                    ElMessage.success('更新成功')
                } else {
                    await bookStore.addBook(form)
                    ElMessage.success('添加成功')
                }
                router.push('/admin/books')
            } catch (e) {
                // error handled by request interceptor usually
            } finally {
                loading.value = false
            }
        }
    })
}

const cancel = () => {
    router.back()
}

onMounted(async () => {
    if (isEdit.value) {
        const id = route.params.id
        const res = await bookStore.fetchBookDetail(id)
        if (res) {
            Object.assign(form, res)
        }
    }
})
</script>

<style scoped>
.book-form-container {
    padding: 20px;
    background: #fff;
    min-height: 100vh;
}
.mb-4 { margin-bottom: 20px; }
.avatar-uploader .avatar {
  width: 100px; /* Reduced specific size or keep as requested */
  height: 140px;
  display: block;
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 100px;
  height: 140px;
  text-align: center;
  line-height: 140px; /* Align vertically */
}
</style>
