<template>
  <div class="dashboard-container">
    <!-- 欢迎区域 -->
    <div class="welcome-section">
      <div class="welcome-text">
        <h2>欢迎回来，{{ userStore.userInfo.realName || userStore.userInfo.username }}</h2>
        <p class="subtitle">{{ greeting }}，祝您工作愉快！</p>
      </div>
      <div class="date-info">
        <div class="date">{{ currentDate }}</div>
        <div class="weather">{{ weekday }}</div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-books">
          <div class="stat-icon">
            <el-icon :size="32"><Reading /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.bookCount }}</div>
            <div class="stat-label">藏书总量</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-users">
          <div class="stat-icon">
            <el-icon :size="32"><User /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-borrow">
          <div class="stat-icon">
            <el-icon :size="32"><Tickets /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.borrowCount }}</div>
            <div class="stat-label">当前借出</div>
          </div>
        </div>
      </el-col>
      <el-col :xs="12" :sm="6">
        <div class="stat-card stat-overdue" :class="{ 'has-overdue': stats.overdueCount > 0 }">
          <div class="stat-icon">
            <el-icon :size="32"><Warning /></el-icon>
          </div>
          <div class="stat-content">
            <div class="stat-value">{{ stats.overdueCount || 0 }}</div>
            <div class="stat-label">超期未还</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 今日数据（管理员可见） -->
    <el-row :gutter="20" class="today-stats" v-if="isAdmin">
      <el-col :xs="12" :sm="8">
        <el-card shadow="hover" class="today-card">
          <div class="today-content">
            <el-icon :size="24" class="today-icon success"><SuccessFilled /></el-icon>
            <div class="today-info">
              <span class="today-value">{{ stats.todayBorrowCount || 0 }}</span>
              <span class="today-label">今日借出</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="12" :sm="8">
        <el-card shadow="hover" class="today-card">
          <div class="today-content">
            <el-icon :size="24" class="today-icon primary"><CircleCheckFilled /></el-icon>
            <div class="today-info">
              <span class="today-value">{{ stats.todayReturnCount || 0 }}</span>
              <span class="today-label">今日归还</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card shadow="hover" class="today-card">
          <div class="today-content">
            <el-icon :size="24" class="today-icon warning"><TrendCharts /></el-icon>
            <div class="today-info">
              <span class="today-value">{{ borrowRate }}%</span>
              <span class="today-label">图书借阅率</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 图表区域 -->
    <el-row :gutter="20" class="charts-row">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">借阅趋势</span>
              <el-radio-group v-model="trendDays" size="small" @change="loadBorrowTrend">
                <el-radio-button :value="7">近7天</el-radio-button>
                <el-radio-button :value="14">近14天</el-radio-button>
                <el-radio-button :value="30">近30天</el-radio-button>
              </el-radio-group>
            </div>
          </template>
          <div ref="trendChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover" class="chart-card">
          <template #header>
            <div class="chart-header">
              <span class="chart-title">图书分类分布</span>
            </div>
          </template>
          <div ref="categoryChartRef" class="chart-container"></div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 公告和快捷入口 -->
    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :lg="14">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Bell /></el-icon> 最新公告
              </span>
              <el-button link type="primary" @click="$router.push('/announcements')">
                查看全部 <el-icon><ArrowRight /></el-icon>
              </el-button>
            </div>
          </template>
          <div v-if="announcements.length > 0" class="announcement-list">
            <div
              v-for="item in announcements"
              :key="item.id"
              class="announcement-item"
              @click="$router.push('/announcements')"
            >
              <div class="announcement-content">
                <el-tag v-if="item.isTop" size="small" type="danger" effect="dark">置顶</el-tag>
                <span class="announcement-title">{{ item.title }}</span>
              </div>
              <span class="announcement-date">{{ formatDate(item.publishTime) }}</span>
            </div>
          </div>
          <el-empty v-else description="暂无公告" :image-size="80" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="10">
        <el-card shadow="hover" class="info-card">
          <template #header>
            <div class="card-header">
              <span class="card-title">
                <el-icon><Grid /></el-icon> 快捷入口
              </span>
            </div>
          </template>
          <div class="quick-access">
            <div class="quick-item" @click="$router.push('/books')">
              <el-icon :size="28" class="quick-icon books"><Reading /></el-icon>
              <span>图书检索</span>
            </div>
            <div class="quick-item" @click="$router.push('/my-borrow')">
              <el-icon :size="28" class="quick-icon borrow"><Tickets /></el-icon>
              <span>我的借阅</span>
            </div>
            <div class="quick-item" @click="$router.push('/home')">
              <el-icon :size="28" class="quick-icon home"><HomeFilled /></el-icon>
              <span>门户首页</span>
            </div>
            <div class="quick-item" @click="$router.push('/profile')">
              <el-icon :size="28" class="quick-icon profile"><User /></el-icon>
              <span>个人中心</span>
            </div>
            <template v-if="isAdmin">
              <div class="quick-item" @click="$router.push('/admin/books')">
                <el-icon :size="28" class="quick-icon admin"><Setting /></el-icon>
                <span>图书管理</span>
              </div>
              <div class="quick-item" @click="$router.push('/admin/borrow')">
                <el-icon :size="28" class="quick-icon admin-borrow"><List /></el-icon>
                <span>借阅管理</span>
              </div>
            </template>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick } from 'vue'
import { useUserStore } from '@/stores/user'
import { getFullHomeStats, getBorrowTrend, getCategoryStats } from '@/api/home'
import { getTopAnnouncements } from '@/api/announcement'
import * as echarts from 'echarts'
import {
  Reading,
  User,
  Tickets,
  Warning,
  SuccessFilled,
  CircleCheckFilled,
  TrendCharts,
  Bell,
  ArrowRight,
  Grid,
  HomeFilled,
  Setting,
  List
} from '@element-plus/icons-vue'

const userStore = useUserStore()

// 数据状态
const stats = ref({
  bookCount: 0,
  borrowCount: 0,
  userCount: 0,
  overdueCount: 0,
  todayBorrowCount: 0,
  todayReturnCount: 0
})
const announcements = ref([])
const trendDays = ref(7)
const borrowTrendData = ref([])
const categoryStatsData = ref([])

// 图表引用
const trendChartRef = ref(null)
const categoryChartRef = ref(null)
let trendChart = null
let categoryChart = null

// 计算属性
const isAdmin = computed(() => userStore.role === 1)

const greeting = computed(() => {
  const hour = new Date().getHours()
  if (hour < 6) return '凌晨好'
  if (hour < 9) return '早上好'
  if (hour < 12) return '上午好'
  if (hour < 14) return '中午好'
  if (hour < 17) return '下午好'
  if (hour < 19) return '傍晚好'
  return '晚上好'
})

const currentDate = computed(() => {
  const d = new Date()
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日`
})

const weekday = computed(() => {
  const days = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return days[new Date().getDay()]
})

const borrowRate = computed(() => {
  if (!stats.value.bookCount) return 0
  return Math.round((stats.value.borrowCount / stats.value.bookCount) * 100)
})

// 格式化日期
const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const d = new Date(dateStr)
  return `${d.getMonth() + 1}月${d.getDate()}日`
}

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await getFullHomeStats()
    if (res) {
      stats.value = res
    }
  } catch (e) {
    console.error('加载统计数据失败', e)
  }
}

// 加载公告
const loadAnnouncements = async () => {
  try {
    const res = await getTopAnnouncements(5)
    if (res) {
      announcements.value = res
    }
  } catch (e) {
    console.error('加载公告失败', e)
  }
}

// 加载借阅趋势
const loadBorrowTrend = async () => {
  try {
    const res = await getBorrowTrend(trendDays.value)
    if (res) {
      borrowTrendData.value = res
      renderTrendChart()
    }
  } catch (e) {
    console.error('加载借阅趋势失败', e)
  }
}

// 加载分类统计
const loadCategoryStats = async () => {
  try {
    const res = await getCategoryStats()
    if (res) {
      categoryStatsData.value = res
      renderCategoryChart()
    }
  } catch (e) {
    console.error('加载分类统计失败', e)
  }
}

// 渲染趋势图表
const renderTrendChart = () => {
  if (!trendChartRef.value) return

  if (!trendChart) {
    trendChart = echarts.init(trendChartRef.value)
  }

  const dates = borrowTrendData.value.map(item => item.date)
  const counts = borrowTrendData.value.map(item => item.count)

  const option = {
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'cross',
        label: {
          backgroundColor: '#6a7985'
        }
      }
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '3%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: dates,
      axisLabel: {
        formatter: (value) => {
          const d = new Date(value)
          return `${d.getMonth() + 1}/${d.getDate()}`
        }
      }
    },
    yAxis: {
      type: 'value',
      minInterval: 1
    },
    series: [
      {
        name: '借阅量',
        type: 'line',
        smooth: true,
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(64, 158, 255, 0.5)' },
            { offset: 1, color: 'rgba(64, 158, 255, 0.1)' }
          ])
        },
        lineStyle: {
          color: '#409eff',
          width: 2
        },
        itemStyle: {
          color: '#409eff'
        },
        data: counts
      }
    ]
  }

  trendChart.setOption(option)
}

// 渲染分类图表
const renderCategoryChart = () => {
  if (!categoryChartRef.value) return

  if (!categoryChart) {
    categoryChart = echarts.init(categoryChartRef.value)
  }

  const data = categoryStatsData.value.map(item => ({
    name: item.categoryName || item.name,
    value: item.count || item.bookCount
  }))

  const option = {
    tooltip: {
      trigger: 'item',
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      orient: 'vertical',
      right: '5%',
      top: 'center',
      itemWidth: 10,
      itemHeight: 10,
      textStyle: {
        fontSize: 12
      }
    },
    series: [
      {
        name: '图书分类',
        type: 'pie',
        radius: ['40%', '70%'],
        center: ['35%', '50%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: '#fff',
          borderWidth: 2
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 14,
            fontWeight: 'bold'
          }
        },
        labelLine: {
          show: false
        },
        data: data
      }
    ],
    color: ['#5470c6', '#91cc75', '#fac858', '#ee6666', '#73c0de', '#3ba272', '#fc8452', '#9a60b4']
  }

  categoryChart.setOption(option)
}

// 窗口大小变化时重绘图表
const handleResize = () => {
  trendChart?.resize()
  categoryChart?.resize()
}

onMounted(async () => {
  await Promise.all([
    loadStats(),
    loadAnnouncements()
  ])

  await nextTick()

  await Promise.all([
    loadBorrowTrend(),
    loadCategoryStats()
  ])

  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  trendChart?.dispose()
  categoryChart?.dispose()
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

/* 欢迎区域 */
.welcome-section {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 8px;
  padding: 24px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  color: #fff;
}

.welcome-text h2 {
  margin: 0 0 8px 0;
  font-size: 22px;
  font-weight: 600;
}

.welcome-text .subtitle {
  margin: 0;
  opacity: 0.9;
  font-size: 14px;
}

.date-info {
  text-align: right;
}

.date-info .date {
  font-size: 16px;
  font-weight: 500;
}

.date-info .weather {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}

/* 统计卡片 */
.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  transition: transform 0.2s, box-shadow 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.stat-books .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-users .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
}

.stat-borrow .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-overdue .stat-icon {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-overdue.has-overdue .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.stat-content {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

/* 今日数据 */
.today-stats {
  margin-bottom: 20px;
}

.today-card {
  height: 80px;
}

.today-card :deep(.el-card__body) {
  height: 100%;
  display: flex;
  align-items: center;
}

.today-content {
  display: flex;
  align-items: center;
  gap: 12px;
  width: 100%;
}

.today-icon {
  padding: 8px;
  border-radius: 8px;
}

.today-icon.success {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

.today-icon.primary {
  background: rgba(64, 158, 255, 0.1);
  color: #409eff;
}

.today-icon.warning {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.today-info {
  display: flex;
  flex-direction: column;
}

.today-value {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
}

.today-label {
  font-size: 12px;
  color: #909399;
}

/* 图表区域 */
.charts-row {
  margin-bottom: 20px;
}

.chart-card {
  height: 100%;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.chart-title {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
}

.chart-container {
  height: 300px;
}

/* 信息卡片 */
.info-row {
  margin-bottom: 20px;
}

.info-card {
  height: 100%;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-weight: 600;
  font-size: 16px;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}

/* 公告列表 */
.announcement-list {
  max-height: 280px;
  overflow-y: auto;
}

.announcement-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background 0.2s;
}

.announcement-item:hover {
  background: #f5f7fa;
  margin: 0 -20px;
  padding: 12px 20px;
}

.announcement-item:last-child {
  border-bottom: none;
}

.announcement-content {
  display: flex;
  align-items: center;
  gap: 8px;
  flex: 1;
  overflow: hidden;
}

.announcement-title {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #303133;
}

.announcement-date {
  font-size: 12px;
  color: #909399;
  flex-shrink: 0;
}

/* 快捷入口 */
.quick-access {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 16px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
  background: #f5f7fa;
}

.quick-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.quick-item span {
  font-size: 12px;
  color: #606266;
}

.quick-icon {
  padding: 10px;
  border-radius: 50%;
}

.quick-icon.books {
  background: rgba(103, 126, 234, 0.1);
  color: #667eea;
}

.quick-icon.borrow {
  background: rgba(245, 87, 108, 0.1);
  color: #f5576c;
}

.quick-icon.home {
  background: rgba(17, 153, 142, 0.1);
  color: #11998e;
}

.quick-icon.profile {
  background: rgba(230, 162, 60, 0.1);
  color: #e6a23c;
}

.quick-icon.admin {
  background: rgba(144, 147, 153, 0.1);
  color: #909399;
}

.quick-icon.admin-borrow {
  background: rgba(103, 194, 58, 0.1);
  color: #67c23a;
}

/* 响应式 */
@media (max-width: 768px) {
  .welcome-section {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }

  .date-info {
    text-align: center;
  }

  .stat-card {
    margin-bottom: 12px;
  }

  .quick-access {
    grid-template-columns: repeat(2, 1fr);
  }
}
</style>
