<template>
  <div class="book-detail-container p-4">
    <el-button @click="$router.back()" class="mb-4">返回</el-button>
    <el-card v-if="book" v-loading="loading">
      <div class="flex gap-8">
        <div class="w-1/3 max-w-[300px]">
          <img :src="getBookCover(book.coverUrl)" alt="封面" class="w-full rounded shadow-lg" />
        </div>
        <div class="flex-1">
          <h1 class="text-2xl font-bold mb-4">{{ book.title }}</h1>
          <div class="space-y-2 text-gray-600">
            <p><strong>作者：</strong>{{ book.author }}</p>
            <p><strong>出版社：</strong>{{ book.publisher }}</p>
            <p><strong>ISBN：</strong>{{ book.isbn }}</p>
            <p><strong>分类：</strong>{{ book.categoryName || '未分类' }}</p>
            <p><strong>位置：</strong>{{ book.location }}</p>
            <p>
                <strong>状态：</strong>
                <el-tag :type="book.stock > 0 ? 'success' : 'danger'">
                    {{ book.stock > 0 ? `库存: ${book.stock}` : '暂时缺货' }}
                </el-tag>
            </p>
          </div>
          
          <div class="mt-6 border-t pt-4">
            <h3 class="font-bold mb-2">简介</h3>
            <p class="text-gray-500 leading-relaxed">{{ book.description || '暂无简介' }}</p>
          </div>

          <div class="mt-6">
              <el-button 
                type="primary" 
                size="large" 
                @click="handleBorrow" 
                :disabled="!book || book.stock <= 0"
              >
                  {{ book && book.stock > 0 ? '立即借阅' : '库存不足' }}
              </el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useBookStore } from '@/stores/book'
import { useBorrowStore } from '@/stores/borrow'
import { ElMessage, ElMessageBox } from 'element-plus'

const route = useRoute()
const router = useRouter()
const bookStore = useBookStore()
const borrowStore = useBorrowStore()

const loading = ref(false)
const book = computed(() => bookStore.currentBook)

const getBookCover = (url) => {
    if (!url) return 'https://placehold.co/300x400?text=No+Cover'
    if (url.startsWith('http')) return url
    return `/api/${url}`
}

const fetchBook = async () => {
    const id = route.params.id
    if (id) {
        loading.value = true
        try {
            await bookStore.fetchBookDetail(id)
        } finally {
            loading.value = false
        }
    }
}

const handleBorrow = () => {
    if (!book.value) return
    
    ElMessageBox.prompt('请输入借阅天数', '借阅确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputType: 'number',
        inputValue: '30',
        inputPattern: /^[1-9]\d*$/,
        inputErrorMessage: '请输入有效的天数'
    }).then(async ({ value }) => {
        try {
            const res = await borrowStore.apply({
                bookId: book.value.id,
                days: parseInt(value)
            })
            if (res) {
                ElMessage.success('借阅成功')
                fetchBook() // Refresh stock
            }
        } catch (e) {
            ElMessage.error('借阅请求失败')
        }
    }).catch(() => {
        // cancel
    })
}

onMounted(() => {
    fetchBook()
})
</script>
