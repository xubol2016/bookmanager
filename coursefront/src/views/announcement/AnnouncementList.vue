<template>
  <div class="announcement-container">
    <!-- 页面标题 -->
    <div class="page-header">
      <h1>系统公告</h1>
      <p class="subtitle">了解最新系统动态和重要通知</p>
    </div>

    <!-- 筛选区域 -->
    <div class="filter-bar">
      <el-input
        v-model="keyword"
        placeholder="搜索公告标题或内容"
        :prefix-icon="Search"
        clearable
        class="search-input"
        @input="handleSearch"
      />
      <el-radio-group v-model="filterType" @change="handleFilterChange">
        <el-radio-button value="all">全部</el-radio-button>
        <el-radio-button value="top">置顶公告</el-radio-button>
        <el-radio-button value="recent">最近一周</el-radio-button>
      </el-radio-group>
    </div>

    <!-- 公告列表 -->
    <div class="announcement-list" v-loading="loading">
      <template v-if="filteredList.length > 0">
        <!-- 置顶公告 -->
        <div v-if="topList.length > 0 && filterType !== 'recent'" class="top-section">
          <div class="section-title">
            <el-icon><Top /></el-icon> 置顶公告
          </div>
          <div
            v-for="item in topList"
            :key="item.id"
            class="announcement-card top"
            @click="showDetail(item)"
          >
            <div class="card-header">
              <div class="card-title">
                <el-tag type="danger" size="small" effect="dark">置顶</el-tag>
                <h3>{{ item.title }}</h3>
              </div>
              <span class="card-date">{{ formatDate(item.publishTime) }}</span>
            </div>
            <p class="card-summary">{{ getSummary(item.content) }}</p>
            <div class="card-footer">
              <span class="read-more">点击查看详情 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </div>

        <!-- 普通公告 -->
        <div class="normal-section">
          <div class="section-title" v-if="topList.length > 0 && filterType !== 'recent' && filterType !== 'top'">
            <el-icon><Document /></el-icon> 全部公告
          </div>
          <div
            v-for="item in normalList"
            :key="item.id"
            class="announcement-card"
            :class="{ 'new': isNew(item.publishTime) }"
            @click="showDetail(item)"
          >
            <div class="card-header">
              <div class="card-title">
                <el-tag v-if="isNew(item.publishTime)" type="success" size="small">新</el-tag>
                <h3>{{ item.title }}</h3>
              </div>
              <span class="card-date">{{ formatDate(item.publishTime) }}</span>
            </div>
            <p class="card-summary">{{ getSummary(item.content) }}</p>
            <div class="card-footer">
              <span class="read-more">点击查看详情 <el-icon><ArrowRight /></el-icon></span>
            </div>
          </div>
        </div>
      </template>

      <el-empty v-else description="暂无公告" :image-size="120">
        <template #description>
          <p>{{ keyword ? '未找到匹配的公告' : '暂无系统公告' }}</p>
        </template>
      </el-empty>
    </div>

    <!-- 公告详情对话框 -->
    <el-dialog
      v-model="detailVisible"
      :title="currentAnnouncement?.title"
      width="700px"
      class="detail-dialog"
      :close-on-click-modal="true"
    >
      <div class="detail-content" v-if="currentAnnouncement">
        <div class="detail-meta">
          <el-tag v-if="currentAnnouncement.isTop" type="danger" effect="dark" size="small">
            置顶
          </el-tag>
          <span class="publish-time">
            <el-icon><Clock /></el-icon>
            发布于 {{ formatFullDate(currentAnnouncement.publishTime) }}
          </span>
        </div>
        <el-divider />
        <div class="detail-body" v-html="formatContent(currentAnnouncement.content)"></div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button v-if="prevAnnouncement" @click="showDetail(prevAnnouncement)">
            <el-icon><ArrowLeft /></el-icon> 上一篇
          </el-button>
          <el-button v-if="nextAnnouncement" @click="showDetail(nextAnnouncement)">
            下一篇 <el-icon><ArrowRight /></el-icon>
          </el-button>
          <el-button type="primary" @click="detailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getPublishedAnnouncements } from '@/api/announcement'
import {
  Search,
  Top,
  Document,
  ArrowRight,
  ArrowLeft,
  Clock
} from '@element-plus/icons-vue'

// 数据状态
const loading = ref(false)
const list = ref([])
const keyword = ref('')
const filterType = ref('all')
const detailVisible = ref(false)
const currentAnnouncement = ref(null)

// 计算属性 - 过滤后的列表
const filteredList = computed(() => {
  let result = [...list.value]

  // 关键词过滤
  if (keyword.value) {
    const kw = keyword.value.toLowerCase()
    result = result.filter(item =>
      item.title.toLowerCase().includes(kw) ||
      item.content.toLowerCase().includes(kw)
    )
  }

  // 类型过滤
  if (filterType.value === 'top') {
    result = result.filter(item => item.isTop)
  } else if (filterType.value === 'recent') {
    const weekAgo = new Date()
    weekAgo.setDate(weekAgo.getDate() - 7)
    result = result.filter(item => new Date(item.publishTime) >= weekAgo)
  }

  return result
})

// 置顶公告
const topList = computed(() => {
  if (filterType.value === 'recent') return []
  return filteredList.value.filter(item => item.isTop)
})

// 普通公告
const normalList = computed(() => {
  if (filterType.value === 'top') return []
  if (filterType.value === 'recent') return filteredList.value
  return filteredList.value.filter(item => !item.isTop)
})

// 上一篇公告
const prevAnnouncement = computed(() => {
  if (!currentAnnouncement.value) return null
  const currentIndex = list.value.findIndex(a => a.id === currentAnnouncement.value.id)
  return currentIndex > 0 ? list.value[currentIndex - 1] : null
})

// 下一篇公告
const nextAnnouncement = computed(() => {
  if (!currentAnnouncement.value) return null
  const currentIndex = list.value.findIndex(a => a.id === currentAnnouncement.value.id)
  return currentIndex < list.value.length - 1 ? list.value[currentIndex + 1] : null
})

// 格式化日期（简短）
const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d

  // 今天
  if (diff < 24 * 60 * 60 * 1000 && d.getDate() === now.getDate()) {
    return `今天 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }
  // 昨天
  const yesterday = new Date(now)
  yesterday.setDate(yesterday.getDate() - 1)
  if (d.getDate() === yesterday.getDate() && d.getMonth() === yesterday.getMonth()) {
    return `昨天 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
  }
  // 本年
  if (d.getFullYear() === now.getFullYear()) {
    return `${d.getMonth() + 1}月${d.getDate()}日`
  }
  // 其他
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
}

// 格式化完整日期
const formatFullDate = (dateStr) => {
  if (!dateStr) return ''
  const d = new Date(dateStr)
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 获取摘要
const getSummary = (content, maxLength = 100) => {
  if (!content) return ''
  const text = content.replace(/<[^>]+>/g, '').replace(/\n+/g, ' ')
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

// 格式化内容（支持换行）
const formatContent = (content) => {
  if (!content) return ''
  return content.replace(/\n/g, '<br>')
}

// 判断是否为新公告（3天内）
const isNew = (dateStr) => {
  if (!dateStr) return false
  const d = new Date(dateStr)
  const now = new Date()
  const diff = now - d
  return diff < 3 * 24 * 60 * 60 * 1000
}

// 搜索处理
let searchTimeout = null
const handleSearch = () => {
  if (searchTimeout) clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    // 搜索已通过computed自动处理
  }, 300)
}

// 过滤类型变更
const handleFilterChange = () => {
  // 过滤已通过computed自动处理
}

// 显示详情
const showDetail = (item) => {
  currentAnnouncement.value = item
  detailVisible.value = true
}

// 加载公告列表
const loadAnnouncements = async () => {
  loading.value = true
  try {
    const res = await getPublishedAnnouncements({ page: 1, size: 100 })
    if (res && res.records) {
      list.value = res.records
    } else if (Array.isArray(res)) {
      list.value = res
    }
  } catch (e) {
    console.error('加载公告失败', e)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadAnnouncements()
})
</script>

<style scoped>
.announcement-container {
  max-width: 900px;
  margin: 0 auto;
}

/* 页面标题 */
.page-header {
  text-align: center;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 600;
  color: #303133;
  margin: 0 0 8px 0;
}

.page-header .subtitle {
  font-size: 14px;
  color: #909399;
  margin: 0;
}

/* 筛选区域 */
.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  flex-wrap: wrap;
  gap: 16px;
}

.search-input {
  width: 280px;
}

/* 公告列表 */
.announcement-list {
  min-height: 400px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: 600;
  color: #606266;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 2px solid #e4e7ed;
}

.top-section {
  margin-bottom: 32px;
}

/* 公告卡片 */
.announcement-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  cursor: pointer;
  transition: all 0.3s;
  border-left: 4px solid transparent;
}

.announcement-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.announcement-card.top {
  border-left-color: #f56c6c;
  background: linear-gradient(135deg, #fff 0%, #fef0f0 100%);
}

.announcement-card.new {
  border-left-color: #67c23a;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.card-title {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
}

.card-title h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  line-height: 1.4;
}

.card-date {
  font-size: 13px;
  color: #909399;
  flex-shrink: 0;
}

.card-summary {
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
  margin: 0 0 12px 0;
}

.card-footer {
  display: flex;
  justify-content: flex-end;
}

.read-more {
  font-size: 13px;
  color: #409eff;
  display: flex;
  align-items: center;
  gap: 4px;
}

/* 详情对话框 */
.detail-dialog :deep(.el-dialog__header) {
  padding-bottom: 0;
}

.detail-dialog :deep(.el-dialog__title) {
  font-size: 20px;
  font-weight: 600;
}

.detail-content {
  padding: 0 10px;
}

.detail-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #909399;
  font-size: 14px;
}

.publish-time {
  display: flex;
  align-items: center;
  gap: 4px;
}

.detail-body {
  font-size: 15px;
  line-height: 1.8;
  color: #303133;
  min-height: 150px;
}

.dialog-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
}

/* 响应式 */
@media (max-width: 768px) {
  .page-header h1 {
    font-size: 22px;
  }

  .filter-bar {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }

  .card-header {
    flex-direction: column;
    gap: 8px;
  }

  .card-date {
    align-self: flex-start;
  }
}
</style>
