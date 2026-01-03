<template>
  <el-container class="layout-container">
    <!-- 侧边栏 -->
    <el-aside :width="isCollapse ? '64px' : '220px'" class="aside" :class="{ 'aside-mobile': isMobile }">
      <div class="logo">
        <el-icon v-if="isCollapse" :size="24"><Reading /></el-icon>
        <span v-else>图书管理系统</span>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          class="el-menu-vertical"
          router
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
        >
          <!-- 门户首页 -->
          <el-menu-item index="/home">
            <el-icon><HomeFilled /></el-icon>
            <template #title>门户首页</template>
          </el-menu-item>

          <!-- 工作台 -->
          <el-menu-item index="/dashboard">
            <el-icon><Odometer /></el-icon>
            <template #title>工作台</template>
          </el-menu-item>

          <!-- 图书浏览 -->
          <el-menu-item index="/books">
            <el-icon><Reading /></el-icon>
            <template #title>图书浏览</template>
          </el-menu-item>

          <!-- 我的借阅 -->
          <el-menu-item index="/my-borrow">
            <el-icon><Tickets /></el-icon>
            <template #title>我的借阅</template>
          </el-menu-item>

          <!-- 系统公告 -->
          <el-menu-item index="/announcements">
            <el-icon><Bell /></el-icon>
            <template #title>系统公告</template>
          </el-menu-item>

          <!-- 个人中心 -->
          <el-menu-item index="/profile">
            <el-icon><User /></el-icon>
            <template #title>个人中心</template>
          </el-menu-item>

          <!-- 管理员菜单 -->
          <template v-if="isAdmin">
            <el-divider class="menu-divider" />

            <el-sub-menu index="admin-book">
              <template #title>
                <el-icon><Collection /></el-icon>
                <span>图书管理</span>
              </template>
              <el-menu-item index="/admin/books">
                <el-icon><Document /></el-icon>
                <template #title>图书列表</template>
              </el-menu-item>
              <el-menu-item index="/admin/categories">
                <el-icon><Folder /></el-icon>
                <template #title>分类管理</template>
              </el-menu-item>
              <el-menu-item index="/admin/category-stats">
                <el-icon><PieChart /></el-icon>
                <template #title>分类统计</template>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="admin-borrow">
              <template #title>
                <el-icon><Tickets /></el-icon>
                <span>借阅管理</span>
              </template>
              <el-menu-item index="/admin/borrow">
                <el-icon><List /></el-icon>
                <template #title>借阅记录</template>
              </el-menu-item>
              <el-menu-item index="/admin/overdue">
                <el-icon><Warning /></el-icon>
                <template #title>超期监控</template>
              </el-menu-item>
              <el-menu-item index="/admin/borrow-stats">
                <el-icon><TrendCharts /></el-icon>
                <template #title>借阅统计</template>
              </el-menu-item>
            </el-sub-menu>

            <el-sub-menu index="admin-system">
              <template #title>
                <el-icon><Setting /></el-icon>
                <span>系统管理</span>
              </template>
              <el-menu-item index="/users">
                <el-icon><UserFilled /></el-icon>
                <template #title>用户管理</template>
              </el-menu-item>
              <el-menu-item index="/admin/announcements">
                <el-icon><Notification /></el-icon>
                <template #title>公告管理</template>
              </el-menu-item>
            </el-sub-menu>
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <!-- 移动端遮罩 -->
    <div v-if="isMobile && !isCollapse" class="mobile-mask" @click="toggleCollapse"></div>

    <el-container class="main-container">
      <!-- 顶部导航 -->
      <el-header class="header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="toggleCollapse">
            <Fold v-if="!isCollapse" />
            <Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-if="currentRouteTitle">{{ currentRouteTitle }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <!-- 全局搜索 -->
          <div class="global-search" v-if="!isMobile">
            <el-input
              v-model="globalKeyword"
              placeholder="搜索图书..."
              :prefix-icon="Search"
              class="search-input"
              @keyup.enter="handleGlobalSearch"
            />
          </div>

          <!-- 通知 -->
          <el-badge :value="unreadCount" :hidden="unreadCount === 0" class="notification-badge">
            <el-icon class="header-icon" @click="goToAnnouncements">
              <Bell />
            </el-icon>
          </el-badge>

          <!-- 全屏 -->
          <el-icon class="header-icon" @click="toggleFullscreen" v-if="!isMobile">
            <FullScreen />
          </el-icon>

          <!-- 用户菜单 -->
          <el-dropdown @command="handleCommand">
            <div class="user-info">
              <el-avatar :size="32" :src="userAvatar" class="user-avatar">
                {{ userInitial }}
              </el-avatar>
              <span class="user-name" v-if="!isMobile">
                {{ userStore.userInfo.realName || userStore.userInfo.username }}
              </span>
              <el-icon class="el-icon--right"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <el-tag size="small" :type="isAdmin ? 'danger' : 'info'">
                    {{ isAdmin ? '管理员' : '普通用户' }}
                  </el-tag>
                </el-dropdown-item>
                <el-dropdown-item divided command="profile">
                  <el-icon><User /></el-icon> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <el-icon><Lock /></el-icon> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <el-icon><SwitchButton /></el-icon> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <!-- 标签页导航 -->
      <div class="tags-view" v-if="!isMobile">
        <el-tag
          v-for="tag in visitedViews"
          :key="tag.path"
          :closable="tag.path !== '/dashboard'"
          :type="isActiveTag(tag) ? '' : 'info'"
          class="view-tag"
          @click="goToTag(tag)"
          @close="closeTag(tag)"
        >
          {{ tag.title }}
        </el-tag>
      </div>

      <!-- 主内容区 -->
      <el-main class="main-content">
        <router-view v-slot="{ Component }">
          <transition name="fade-transform" mode="out-in">
            <keep-alive :include="cachedViews">
              <component :is="Component" />
            </keep-alive>
          </transition>
        </router-view>
      </el-main>

      <!-- 页脚 -->
      <el-footer class="footer" v-if="!isMobile">
        <span>图书管理系统 &copy; 2026</span>
      </el-footer>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import {
  HomeFilled,
  Odometer,
  Reading,
  Tickets,
  Bell,
  User,
  Collection,
  Document,
  Folder,
  PieChart,
  List,
  Warning,
  TrendCharts,
  Setting,
  UserFilled,
  Notification,
  Fold,
  Expand,
  Search,
  FullScreen,
  ArrowDown,
  Lock,
  SwitchButton
} from '@element-plus/icons-vue'
import { getTopAnnouncements } from '@/api/announcement'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

// 响应式状态
const isCollapse = ref(false)
const isMobile = ref(false)
const globalKeyword = ref('')
const unreadCount = ref(0)
const visitedViews = ref([{ path: '/dashboard', title: '工作台' }])
const cachedViews = ref(['Dashboard'])

// 计算属性
const activeMenu = computed(() => route.path)
const currentRouteTitle = computed(() => route.meta.title || '')
const isAdmin = computed(() => userStore.role === 1)
const userAvatar = computed(() => userStore.userInfo.avatar || '')
const userInitial = computed(() => {
  const name = userStore.userInfo.realName || userStore.userInfo.username || ''
  return name.charAt(0).toUpperCase()
})

// 检测屏幕尺寸
const checkMobile = () => {
  isMobile.value = window.innerWidth < 768
  if (isMobile.value) {
    isCollapse.value = true
  }
}

// 切换侧边栏折叠
const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

// 全局搜索
const handleGlobalSearch = () => {
  if (globalKeyword.value.trim()) {
    router.push({ path: '/books', query: { search: globalKeyword.value.trim() } })
    globalKeyword.value = ''
  }
}

// 跳转到公告页面
const goToAnnouncements = () => {
  router.push('/announcements')
}

// 全屏切换
const toggleFullscreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

// 处理用户菜单命令
const handleCommand = (command) => {
  if (command === 'logout') {
    userStore.logout()
    router.push('/login')
  } else if (command === 'profile') {
    router.push('/profile')
  } else if (command === 'password') {
    router.push('/profile')
  }
}

// 标签页相关
const isActiveTag = (tag) => tag.path === route.path

const goToTag = (tag) => {
  router.push(tag.path)
}

const closeTag = (tag) => {
  const index = visitedViews.value.findIndex(v => v.path === tag.path)
  if (index > -1) {
    visitedViews.value.splice(index, 1)
    // 如果关闭的是当前标签，跳转到最后一个
    if (tag.path === route.path && visitedViews.value.length > 0) {
      const lastTag = visitedViews.value[visitedViews.value.length - 1]
      router.push(lastTag.path)
    }
  }
}

// 添加访问标签
const addVisitedView = (r) => {
  if (!r.meta.title) return
  const exists = visitedViews.value.find(v => v.path === r.path)
  if (!exists) {
    visitedViews.value.push({
      path: r.path,
      title: r.meta.title
    })
  }
}

// 加载未读公告数量
const loadUnreadCount = async () => {
  try {
    const res = await getTopAnnouncements(100)
    if (res) {
      // 简单统计今天的公告数量作为"未读"
      const today = new Date().toDateString()
      unreadCount.value = res.filter(a =>
        new Date(a.publishTime).toDateString() === today
      ).length
    }
  } catch (e) {
    console.error('获取公告失败', e)
  }
}

// 监听路由变化
watch(route, (newRoute) => {
  addVisitedView(newRoute)
  // 移动端点击菜单后自动收起
  if (isMobile.value) {
    isCollapse.value = true
  }
}, { immediate: true })

onMounted(() => {
  checkMobile()
  window.addEventListener('resize', checkMobile)
  loadUnreadCount()
})

onUnmounted(() => {
  window.removeEventListener('resize', checkMobile)
})
</script>

<style scoped>
.layout-container {
  height: 100vh;
  overflow: hidden;
}

.aside {
  background-color: #304156;
  color: #fff;
  display: flex;
  flex-direction: column;
  transition: width 0.3s;
  overflow: hidden;
  z-index: 1001;
}

.aside-mobile {
  position: fixed;
  left: 0;
  top: 0;
  height: 100vh;
}

.mobile-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  z-index: 1000;
}

.logo {
  height: 60px;
  line-height: 60px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  color: #fff;
  border-bottom: 1px solid #1f2d3d;
  white-space: nowrap;
  overflow: hidden;
}

.menu-scrollbar {
  flex: 1;
}

.el-menu-vertical {
  border-right: none;
}

.el-menu-vertical:not(.el-menu--collapse) {
  width: 220px;
}

.menu-divider {
  margin: 10px 15px;
  border-color: #1f2d3d;
}

.main-container {
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.header {
  background-color: #fff;
  border-bottom: 1px solid #dcdfe6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
  padding: 0 20px;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.collapse-btn:hover {
  color: #409eff;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 20px;
}

.global-search {
  width: 200px;
}

.search-input :deep(.el-input__wrapper) {
  border-radius: 20px;
}

.header-icon {
  font-size: 18px;
  cursor: pointer;
  color: #606266;
  transition: color 0.2s;
}

.header-icon:hover {
  color: #409eff;
}

.notification-badge {
  line-height: 1;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.user-avatar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.user-name {
  font-size: 14px;
  color: #303133;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.tags-view {
  background: #fff;
  border-bottom: 1px solid #d8dce5;
  padding: 5px 10px;
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.view-tag {
  cursor: pointer;
  border-radius: 2px;
}

.main-content {
  flex: 1;
  overflow: auto;
  background-color: #f0f2f5;
  padding: 20px;
}

.footer {
  height: 40px;
  line-height: 40px;
  text-align: center;
  background: #fff;
  border-top: 1px solid #dcdfe6;
  font-size: 12px;
  color: #909399;
}

/* 过渡动画 */
.fade-transform-enter-active,
.fade-transform-leave-active {
  transition: all 0.3s;
}

.fade-transform-enter-from {
  opacity: 0;
  transform: translateX(-30px);
}

.fade-transform-leave-to {
  opacity: 0;
  transform: translateX(30px);
}

/* 响应式样式 */
@media (max-width: 768px) {
  .header {
    padding: 0 10px;
  }

  .header-left {
    gap: 10px;
  }

  .header-right {
    gap: 15px;
  }

  .main-content {
    padding: 10px;
  }
}
</style>
