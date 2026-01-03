<template>
  <div class="book-list-container">
    <div class="header">
      <h2>图书列表</h2>
      <div class="actions">
        <!-- 搜索区域 -->
        <SearchBox
          :show-history-tags="false"
          :initial-keyword="queryParams.search"
          @search="handleQuickSearch"
        />
      </div>
    </div>

    <!-- 当前搜索条件展示 -->
    <div v-if="hasActiveFilters" class="active-filters">
      <span class="filter-label">当前筛选：</span>
      <el-tag
        v-if="queryParams.search"
        closable
        @close="clearFilter('search')"
      >
        关键词: {{ queryParams.search }}
      </el-tag>
      <el-tag
        v-if="queryParams.title"
        closable
        @close="clearFilter('title')"
      >
        书名: {{ queryParams.title }}
      </el-tag>
      <el-tag
        v-if="queryParams.author"
        closable
        @close="clearFilter('author')"
      >
        作者: {{ queryParams.author }}
      </el-tag>
      <el-tag
        v-if="queryParams.isbn"
        closable
        @close="clearFilter('isbn')"
      >
        ISBN: {{ queryParams.isbn }}
      </el-tag>
      <el-tag
        v-if="queryParams.publisher"
        closable
        @close="clearFilter('publisher')"
      >
        出版社: {{ queryParams.publisher }}
      </el-tag>
      <el-tag
        v-if="queryParams.categoryId"
        closable
        @close="clearFilter('categoryId')"
      >
        分类ID: {{ queryParams.categoryId }}
      </el-tag>
      <el-tag
        v-if="queryParams.yearStart || queryParams.yearEnd"
        closable
        @close="clearFilter('year')"
      >
        出版年份: {{ queryParams.yearStart || '不限' }} - {{ queryParams.yearEnd || '不限' }}
      </el-tag>
      <el-tag
        v-if="queryParams.hasStock !== undefined"
        closable
        @close="clearFilter('hasStock')"
      >
        库存: {{ queryParams.hasStock ? '有库存' : '无库存' }}
      </el-tag>
      <el-button link type="primary" @click="clearAllFilters">清除全部</el-button>
    </div>

    <!-- 排序和筛选栏 -->
    <div class="filter-bar">
      <div class="sort-options">
        <span class="sort-label">排序：</span>
        <el-radio-group v-model="sortBy" size="small" @change="handleSortChange">
          <el-radio-button value="default">默认</el-radio-button>
          <el-radio-button value="newest">最新上架</el-radio-button>
          <el-radio-button value="popular">借阅最多</el-radio-button>
          <el-radio-button value="stock">库存最多</el-radio-button>
        </el-radio-group>
      </div>
      <div class="view-options">
        <el-radio-group v-model="viewMode" size="small">
          <el-radio-button value="grid">
            <el-icon><Grid /></el-icon>
          </el-radio-button>
          <el-radio-button value="list">
            <el-icon><List /></el-icon>
          </el-radio-button>
        </el-radio-group>
      </div>
    </div>

    <!-- 图书展示区域 - 网格视图 -->
    <el-row v-if="viewMode === 'grid'" :gutter="20" v-loading="loading">
      <el-col :xs="12" :sm="8" :md="6" :lg="4" :xl="4" v-for="book in bookList" :key="book.id" class="mb-4">
        <el-card shadow="hover" class="book-card" @click="goToDetail(book.id)">
          <div class="cover-wrapper">
            <img :src="getBookCover(book.coverUrl)" class="book-cover" alt="cover"/>
            <div v-if="book.stock === 0" class="no-stock-badge">
              暂无库存
            </div>
          </div>
          <div class="book-info">
            <h3 class="title" :title="book.title">{{ book.title }}</h3>
            <p class="author">{{ book.author }}</p>
            <div class="meta">
              <span class="stock">库存: {{ book.stock }}</span>
              <el-tag size="small" :type="book.status === 1 ? 'success' : 'info'">
                {{ book.status === 1 ? '上架' : '下架' }}
              </el-tag>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图书展示区域 - 列表视图 -->
    <div v-else class="book-list-view" v-loading="loading">
      <el-table :data="bookList" style="width: 100%" @row-click="goToDetailFromRow">
        <el-table-column label="封面" width="100">
          <template #default="{ row }">
            <img :src="getBookCover(row.coverUrl)" class="list-cover" alt="cover"/>
          </template>
        </el-table-column>
        <el-table-column prop="title" label="书名" min-width="200">
          <template #default="{ row }">
            <span class="book-title-link">{{ row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="author" label="作者" width="150" />
        <el-table-column prop="publisher" label="出版社" width="150" />
        <el-table-column prop="isbn" label="ISBN" width="150" />
        <el-table-column prop="stock" label="库存" width="80" align="center" />
        <el-table-column label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-tag size="small" :type="row.status === 1 ? 'success' : 'info'">
              {{ row.status === 1 ? '上架' : '下架' }}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-empty v-if="!loading && bookList.length === 0" description="暂无符合条件的图书">
      <el-button type="primary" @click="clearAllFilters">清除筛选条件</el-button>
    </el-empty>

    <!-- 分页 -->
    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.size"
        :total="total"
        :page-sizes="[12, 24, 48, 96]"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, reactive, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useBookStore } from '@/stores/book'
import { storeToRefs } from 'pinia'
import { Grid, List } from '@element-plus/icons-vue'
import SearchBox from '@/components/SearchBox.vue'
import { advancedSearch } from '@/api/search'

const router = useRouter()
const route = useRoute()
const bookStore = useBookStore()
const { bookList, total, loading } = storeToRefs(bookStore)

const viewMode = ref('grid')
const sortBy = ref('default')

const queryParams = reactive({
  page: 1,
  size: 12,
  search: '',
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  categoryId: null,
  yearStart: null,
  yearEnd: null,
  hasStock: undefined,
  status: 1 // 默认只显示上架的
})

// 是否有活动的筛选条件
const hasActiveFilters = computed(() => {
  return queryParams.search ||
         queryParams.title ||
         queryParams.author ||
         queryParams.isbn ||
         queryParams.publisher ||
         queryParams.categoryId ||
         queryParams.yearStart ||
         queryParams.yearEnd ||
         queryParams.hasStock !== undefined
})

// 监听路由变化
watch(
  () => route.query,
  (newQuery) => {
    parseQueryParams(newQuery)
    loadBooks()
  },
  { deep: true }
)

// 解析URL查询参数
const parseQueryParams = (query) => {
  queryParams.page = 1
  queryParams.search = query.search || ''
  queryParams.title = query.title || ''
  queryParams.author = query.author || ''
  queryParams.isbn = query.isbn || ''
  queryParams.publisher = query.publisher || ''
  queryParams.categoryId = query.categoryId ? parseInt(query.categoryId) : null
  queryParams.yearStart = query.yearStart ? parseInt(query.yearStart) : null
  queryParams.yearEnd = query.yearEnd ? parseInt(query.yearEnd) : null
  queryParams.hasStock = query.hasStock !== undefined ? query.hasStock === 'true' : undefined
}

const getBookCover = (url) => {
  if (!url) return 'https://placehold.co/150x200?text=No+Cover'
  if (url.startsWith('http')) return url
  return `/api/${url}`
}

const handleQuickSearch = (keyword) => {
  queryParams.search = keyword
  queryParams.page = 1
  loadBooks()
}

const handleSortChange = () => {
  queryParams.page = 1
  loadBooks()
}

const clearFilter = (filterName) => {
  if (filterName === 'year') {
    queryParams.yearStart = null
    queryParams.yearEnd = null
  } else {
    queryParams[filterName] = filterName === 'hasStock' ? undefined : (filterName === 'categoryId' ? null : '')
  }
  updateUrl()
  loadBooks()
}

const clearAllFilters = () => {
  queryParams.search = ''
  queryParams.title = ''
  queryParams.author = ''
  queryParams.isbn = ''
  queryParams.publisher = ''
  queryParams.categoryId = null
  queryParams.yearStart = null
  queryParams.yearEnd = null
  queryParams.hasStock = undefined
  queryParams.page = 1
  router.push({ path: '/books' })
  loadBooks()
}

const updateUrl = () => {
  const query = {}
  if (queryParams.search) query.search = queryParams.search
  if (queryParams.title) query.title = queryParams.title
  if (queryParams.author) query.author = queryParams.author
  if (queryParams.isbn) query.isbn = queryParams.isbn
  if (queryParams.publisher) query.publisher = queryParams.publisher
  if (queryParams.categoryId) query.categoryId = queryParams.categoryId
  if (queryParams.yearStart) query.yearStart = queryParams.yearStart
  if (queryParams.yearEnd) query.yearEnd = queryParams.yearEnd
  if (queryParams.hasStock !== undefined) query.hasStock = queryParams.hasStock

  router.replace({ path: '/books', query })
}

const loadBooks = async () => {
  // 构建请求参数
  const params = {
    page: queryParams.page,
    size: queryParams.size,
    status: queryParams.status
  }

  // 如果是高级搜索模式
  if (route.query.advanced === '1' || queryParams.title || queryParams.author ||
      queryParams.isbn || queryParams.publisher || queryParams.categoryId ||
      queryParams.yearStart || queryParams.yearEnd || queryParams.hasStock !== undefined) {
    // 使用高级搜索API
    const searchData = {
      page: queryParams.page,
      size: queryParams.size
    }
    if (queryParams.title) searchData.title = queryParams.title
    if (queryParams.author) searchData.author = queryParams.author
    if (queryParams.isbn) searchData.isbn = queryParams.isbn
    if (queryParams.publisher) searchData.publisher = queryParams.publisher
    if (queryParams.categoryId) searchData.categoryId = queryParams.categoryId
    if (queryParams.yearStart) searchData.publishYearStart = queryParams.yearStart
    if (queryParams.yearEnd) searchData.publishYearEnd = queryParams.yearEnd
    if (queryParams.hasStock !== undefined) searchData.hasStock = queryParams.hasStock

    try {
      bookStore.setLoading(true)
      const res = await advancedSearch(searchData)
      if (res) {
        bookStore.setBooks(res.records || [], res.total || 0)
      }
    } catch (e) {
      console.error('高级搜索失败', e)
    } finally {
      bookStore.setLoading(false)
    }
  } else if (queryParams.search) {
    // 简单关键词搜索
    params.keyword = queryParams.search
    bookStore.fetchBooks(params)
  } else {
    // 普通列表
    bookStore.fetchBooks(params)
  }
}

const handleSizeChange = () => {
  queryParams.page = 1
  loadBooks()
}

const handleCurrentChange = () => {
  loadBooks()
}

const goToDetail = (id) => {
  router.push(`/books/${id}`)
}

const goToDetailFromRow = (row) => {
  router.push(`/books/${row.id}`)
}

onMounted(() => {
  parseQueryParams(route.query)
  loadBooks()
})
</script>

<style scoped>
.book-list-container {
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 15px;
}

.header h2 {
  margin: 0;
}

.actions {
  flex: 1;
  max-width: 500px;
}

.active-filters {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 15px;
  padding: 10px 15px;
  background: #f5f7fa;
  border-radius: 4px;
}

.filter-label {
  font-size: 14px;
  color: #606266;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 10px 0;
  border-bottom: 1px solid #ebeef5;
}

.sort-options {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-label {
  font-size: 14px;
  color: #606266;
}

.mb-4 {
  margin-bottom: 20px;
}

.book-card {
  cursor: pointer;
  transition: transform 0.2s, box-shadow 0.2s;
  height: 100%;
}

.book-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.cover-wrapper {
  height: 180px;
  display: flex;
  justify-content: center;
  align-items: center;
  background: #f5f7fa;
  margin-bottom: 10px;
  overflow: hidden;
  position: relative;
  border-radius: 4px;
}

.book-cover {
  max-height: 100%;
  max-width: 100%;
  object-fit: cover;
}

.no-stock-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: rgba(245, 108, 108, 0.9);
  color: white;
  padding: 2px 8px;
  font-size: 12px;
  border-radius: 0 0 0 4px;
}

.book-info {
  text-align: left;
}

.title {
  font-size: 14px;
  font-weight: bold;
  margin: 0 0 5px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #303133;
}

.author {
  font-size: 12px;
  color: #909399;
  margin: 2px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.stock {
  font-size: 12px;
  color: #606266;
}

.book-list-view {
  margin-bottom: 20px;
}

.list-cover {
  width: 60px;
  height: 80px;
  object-fit: cover;
  border-radius: 4px;
}

.book-title-link {
  color: #409eff;
  cursor: pointer;
}

.book-title-link:hover {
  text-decoration: underline;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}

/* 响应式布局 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
  }

  .actions {
    width: 100%;
    max-width: none;
  }

  .filter-bar {
    flex-direction: column;
    gap: 10px;
    align-items: flex-start;
  }
}
</style>
