<template>
  <div class="home-container">
    <!-- 顶部轮播区域 -->
    <section class="hero-section">
      <el-carousel height="320px" :interval="5000" arrow="hover">
        <el-carousel-item v-for="(item, index) in carouselItems" :key="index">
          <div class="carousel-content" :style="{ background: item.bg }">
            <div class="carousel-text">
              <h2>{{ item.title }}</h2>
              <p>{{ item.desc }}</p>
              <el-button type="primary" size="large" @click="handleCarouselClick(item)">
                {{ item.btnText }}
              </el-button>
            </div>
            <div class="carousel-icon">
              <el-icon :size="120"><component :is="item.icon" /></el-icon>
            </div>
          </div>
        </el-carousel-item>
      </el-carousel>
    </section>

    <!-- 快速搜索区域 -->
    <section class="search-section">
      <div class="search-wrapper">
        <h3><el-icon><Search /></el-icon> 快速搜索图书</h3>
        <div class="search-box-container">
          <el-input
            v-model="searchKeyword"
            placeholder="输入书名、作者或ISBN进行搜索..."
            size="large"
            clearable
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button type="primary" @click="handleSearch">
                <el-icon><Search /></el-icon> 搜索
              </el-button>
            </template>
          </el-input>
          <!-- 搜索历史 -->
          <div v-if="searchHistory.length > 0" class="search-history">
            <span class="history-label">搜索历史：</span>
            <el-tag
              v-for="item in searchHistory.slice(0, 5)"
              :key="item"
              size="small"
              class="history-tag"
              closable
              @click="searchKeyword = item; handleSearch()"
              @close="removeHistory(item)"
            >
              {{ item }}
            </el-tag>
            <el-button link type="danger" size="small" @click="clearHistory">清空</el-button>
          </div>
        </div>
      </div>
    </section>

    <!-- 统计数据展示 -->
    <section class="stats-section">
      <el-row :gutter="20">
        <el-col :xs="12" :sm="6" v-for="stat in statsData" :key="stat.key">
          <div class="stat-card" :style="{ borderTopColor: stat.color }">
            <div class="stat-icon" :style="{ color: stat.color }">
              <el-icon :size="32"><component :is="stat.icon" /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stat.value }}</div>
              <div class="stat-label">{{ stat.label }}</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 推荐图书和热门图书 -->
    <section class="books-section">
      <el-row :gutter="24">
        <!-- 推荐图书（最新上架） -->
        <el-col :xs="24" :lg="12">
          <el-card class="book-card">
            <template #header>
              <div class="card-header">
                <span><el-icon><Star /></el-icon> 新书推荐</span>
                <el-button link type="primary" @click="$router.push('/books')">
                  查看更多 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </template>
            <div class="book-grid">
              <div
                v-for="book in recommendBooks"
                :key="book.id"
                class="book-item"
                @click="$router.push(`/books/${book.id}`)"
              >
                <div class="book-cover">
                  <img v-if="book.coverUrl" :src="getBookCover(book.coverUrl)" :alt="book.title" />
                  <div v-else class="cover-placeholder">
                    <el-icon :size="40"><Reading /></el-icon>
                  </div>
                </div>
                <div class="book-info">
                  <div class="book-title">{{ book.title }}</div>
                  <div class="book-author">{{ book.author }}</div>
                </div>
              </div>
            </div>
            <el-empty v-if="recommendBooks.length === 0" description="暂无推荐图书" />
          </el-card>
        </el-col>

        <!-- 热门图书 -->
        <el-col :xs="24" :lg="12">
          <el-card class="book-card">
            <template #header>
              <div class="card-header">
                <span><el-icon><TrendCharts /></el-icon> 热门借阅</span>
                <el-button link type="primary" @click="$router.push('/books')">
                  查看更多 <el-icon><ArrowRight /></el-icon>
                </el-button>
              </div>
            </template>
            <div class="hot-book-list">
              <div
                v-for="(book, index) in hotBooks"
                :key="book.id"
                class="hot-book-item"
                @click="$router.push(`/books/${book.id}`)"
              >
                <div class="rank" :class="{ 'top-three': index < 3 }">{{ index + 1 }}</div>
                <div class="hot-book-info">
                  <div class="book-title">{{ book.title }}</div>
                  <div class="book-meta">
                    <span>{{ book.author }}</span>
                    <el-tag size="small" type="warning">借阅 {{ book.borrowCount || 0 }} 次</el-tag>
                  </div>
                </div>
              </div>
            </div>
            <el-empty v-if="hotBooks.length === 0" description="暂无热门图书" />
          </el-card>
        </el-col>
      </el-row>
    </section>

    <!-- 公告区域 -->
    <section class="announcement-section">
      <el-card>
        <template #header>
          <div class="card-header">
            <span><el-icon><Bell /></el-icon> 系统公告</span>
            <el-button link type="primary" @click="$router.push('/announcements')">
              查看全部 <el-icon><ArrowRight /></el-icon>
            </el-button>
          </div>
        </template>
        <div class="announcement-list">
          <div
            v-for="item in announcements"
            :key="item.id"
            class="announcement-item"
            @click="viewAnnouncement(item)"
          >
            <div class="announcement-title">
              <el-tag v-if="item.isTop" size="small" type="danger" effect="dark">置顶</el-tag>
              <span>{{ item.title }}</span>
            </div>
            <div class="announcement-time">{{ formatDate(item.publishTime) }}</div>
          </div>
        </div>
        <el-empty v-if="announcements.length === 0" description="暂无公告" />
      </el-card>
    </section>

    <!-- 公告详情对话框 -->
    <el-dialog v-model="announcementVisible" :title="currentAnnouncement?.title" width="600px">
      <div class="announcement-detail">
        <div class="meta">
          <span>发布时间：{{ formatDateTime(currentAnnouncement?.publishTime) }}</span>
        </div>
        <div class="content" v-html="currentAnnouncement?.content"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import {
  Search, Star, TrendCharts, Bell, ArrowRight, Reading,
  Notebook, User, Document, DataAnalysis
} from '@element-plus/icons-vue'
import { getHomeStats, getRecommendations, getHotBooks } from '@/api/home'
import { getTopAnnouncements } from '@/api/announcement'

const router = useRouter()

// 轮播数据
const carouselItems = ref([
  {
    title: '欢迎使用图书管理系统',
    desc: '海量图书，一键借阅，随时随地享受阅读',
    btnText: '开始浏览',
    icon: 'Reading',
    bg: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    action: '/books'
  },
  {
    title: '新书上架',
    desc: '精选最新图书，满足你的阅读需求',
    btnText: '查看新书',
    icon: 'Star',
    bg: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    action: '/books'
  },
  {
    title: '借阅管理',
    desc: '轻松管理你的借阅记录，查看归还日期',
    btnText: '我的借阅',
    icon: 'Document',
    bg: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    action: '/my-borrow'
  }
])

// 搜索
const searchKeyword = ref('')
const searchHistory = ref(JSON.parse(localStorage.getItem('searchHistory') || '[]'))

// 统计数据
const stats = ref({})
const statsData = computed(() => [
  { key: 'bookCount', label: '馆藏图书', value: stats.value.bookCount || 0, icon: 'Notebook', color: '#409EFF' },
  { key: 'userCount', label: '注册用户', value: stats.value.userCount || 0, icon: 'User', color: '#67C23A' },
  { key: 'borrowCount', label: '当前借出', value: stats.value.borrowCount || 0, icon: 'Document', color: '#E6A23C' },
  { key: 'todayBorrowCount', label: '今日借阅', value: stats.value.todayBorrowCount || 0, icon: 'DataAnalysis', color: '#F56C6C' }
])

// 图书数据
const recommendBooks = ref([])
const hotBooks = ref([])

// 公告数据
const announcements = ref([])
const announcementVisible = ref(false)
const currentAnnouncement = ref(null)

// 方法
const handleCarouselClick = (item) => {
  router.push(item.action)
}

const handleSearch = () => {
  if (!searchKeyword.value.trim()) return
  const history = searchHistory.value.filter(h => h !== searchKeyword.value)
  history.unshift(searchKeyword.value)
  searchHistory.value = history.slice(0, 10)
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
  router.push({ path: '/books', query: { search: searchKeyword.value } })
}

const removeHistory = (item) => {
  searchHistory.value = searchHistory.value.filter(h => h !== item)
  localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
}

const clearHistory = () => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
}

const viewAnnouncement = (item) => {
  currentAnnouncement.value = item
  announcementVisible.value = true
}

const getBookCover = (url) => {
  if (!url) return ''
  if (url.startsWith('http')) return url
  return `/api/${url}`
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getFullYear()}-${String(d.getMonth()+1).padStart(2,'0')}-${String(d.getDate()).padStart(2,'0')} ${String(d.getHours()).padStart(2,'0')}:${String(d.getMinutes()).padStart(2,'0')}`
}

// 获取数据
const fetchData = async () => {
  try {
    const [statsRes, recommendRes, hotRes, annRes] = await Promise.all([
      getHomeStats(),
      getRecommendations(),
      getHotBooks(8),
      getTopAnnouncements(5)
    ])
    if (statsRes) stats.value = statsRes
    if (recommendRes) recommendBooks.value = recommendRes.slice(0, 8)
    if (hotRes) hotBooks.value = hotRes.slice(0, 8)
    if (annRes) announcements.value = annRes
  } catch (e) {
    console.error('Error fetching home data:', e)
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.home-container {
  min-height: 100%;
  background: #f5f7fa;
}

/* 轮播区域 */
.hero-section {
  margin-bottom: 24px;
}

.carousel-content {
  height: 100%;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 80px;
  color: #fff;
}

.carousel-text h2 {
  font-size: 32px;
  margin-bottom: 16px;
}

.carousel-text p {
  font-size: 16px;
  margin-bottom: 24px;
  opacity: 0.9;
}

.carousel-icon {
  opacity: 0.3;
}

/* 搜索区域 */
.search-section {
  background: #fff;
  padding: 32px;
  margin-bottom: 24px;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.search-wrapper h3 {
  margin: 0 0 16px 0;
  font-size: 18px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 8px;
}

.search-box-container {
  max-width: 800px;
}

.search-history {
  margin-top: 12px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.history-label {
  color: #909399;
  font-size: 13px;
}

.history-tag {
  cursor: pointer;
}

/* 统计区域 */
.stats-section {
  margin-bottom: 24px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  border-top: 3px solid #409EFF;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

/* 图书区域 */
.books-section {
  margin-bottom: 24px;
}

.book-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-header span {
  font-weight: bold;
  font-size: 16px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.book-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.book-item {
  cursor: pointer;
  transition: transform 0.2s;
}

.book-item:hover {
  transform: translateY(-4px);
}

.book-cover {
  width: 100%;
  aspect-ratio: 3/4;
  border-radius: 4px;
  overflow: hidden;
  background: #f5f7fa;
  display: flex;
  align-items: center;
  justify-content: center;
}

.book-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.cover-placeholder {
  color: #c0c4cc;
}

.book-info {
  margin-top: 8px;
}

.book-title {
  font-size: 14px;
  color: #303133;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.book-author {
  font-size: 12px;
  color: #909399;
  margin-top: 4px;
}

/* 热门图书列表 */
.hot-book-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-book-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: background 0.2s;
}

.hot-book-item:hover {
  background: #f5f7fa;
}

.rank {
  width: 24px;
  height: 24px;
  border-radius: 4px;
  background: #e4e7ed;
  color: #909399;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: bold;
  flex-shrink: 0;
}

.rank.top-three {
  background: linear-gradient(135deg, #f5af19 0%, #f12711 100%);
  color: #fff;
}

.hot-book-info {
  flex: 1;
  min-width: 0;
}

.hot-book-info .book-title {
  margin-bottom: 4px;
}

.book-meta {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 12px;
  color: #909399;
}

/* 公告区域 */
.announcement-section {
  margin-bottom: 24px;
}

.announcement-list {
  display: flex;
  flex-direction: column;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
  cursor: pointer;
  transition: background 0.2s;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-item:hover {
  background: #f5f7fa;
  margin: 0 -20px;
  padding: 12px 20px;
}

.announcement-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.announcement-time {
  color: #909399;
  font-size: 13px;
}

/* 公告详情 */
.announcement-detail .meta {
  color: #909399;
  font-size: 13px;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.announcement-detail .content {
  line-height: 1.8;
  color: #606266;
}

/* 响应式 */
@media (max-width: 768px) {
  .carousel-content {
    padding: 0 24px;
  }

  .carousel-text h2 {
    font-size: 24px;
  }

  .carousel-icon {
    display: none;
  }

  .book-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
