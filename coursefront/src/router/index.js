import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/login',
            name: 'login',
            component: () => import('../views/auth/Login.vue')
        },
        {
            path: '/register',
            name: 'register',
            component: () => import('../views/auth/Register.vue')
        },
        {
            path: '/',
            name: 'root',
            component: () => import('../layout/MainLayout.vue'),
            redirect: '/dashboard',
            children: [
                {
                    path: 'dashboard',
                    name: 'dashboard',
                    component: () => import('../views/dashboard/Dashboard.vue'),
                    meta: { title: '首页', requiresAuth: true }
                },
                {
                    path: 'profile',
                    name: 'profile',
                    component: () => import('../views/profile/Profile.vue'),
                    meta: { title: '个人中心', requiresAuth: true }
                },
                {
                    path: 'users',
                    name: 'users',
                    component: () => import('../views/system/UserList.vue'),
                    meta: { title: '用户管理', requiresAuth: true, role: 1 } // Role 1 for admin
                },
                {
                    path: 'books',
                    name: 'books',
                    component: () => import('../views/book/BookList.vue'),
                    meta: { title: '图书列表', requiresAuth: true }
                },
                {
                    path: 'books/:id',
                    name: 'bookDetail',
                    component: () => import('../views/book/BookDetail.vue'),
                    meta: { title: '图书详情', requiresAuth: true }
                },
                {
                    path: 'admin/books',
                    name: 'bookAdmin',
                    component: () => import('../views/admin/BookAdmin.vue'),
                    meta: { title: '图书管理', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/books/add',
                    name: 'addBook',
                    component: () => import('../views/admin/BookForm.vue'),
                    meta: { title: '新增图书', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/books/edit/:id',
                    name: 'editBook',
                    component: () => import('../views/admin/BookForm.vue'),
                    meta: { title: '编辑图书', requiresAuth: true, role: 1 }
                },
                {
                    path: 'my-borrow',
                    name: 'myBorrow',
                    component: () => import('../views/borrow/MyBorrow.vue'),
                    meta: { title: '我的借阅', requiresAuth: true }
                },
                {
                    path: 'admin/borrow',
                    name: 'adminBorrow',
                    component: () => import('../views/admin/BorrowList.vue'),
                    meta: { title: '借阅管理', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/overdue',
                    name: 'adminOverdue',
                    component: () => import('../views/admin/OverdueList.vue'),
                    meta: { title: '超期监控', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/borrow-stats',
                    name: 'adminBorrowStats',
                    component: () => import('../views/admin/BorrowStats.vue'),
                    meta: { title: '借阅统计', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/categories',
                    name: 'categoryAdmin',
                    component: () => import('../views/admin/CategoryAdmin.vue'),
                    meta: { title: '分类管理', requiresAuth: true, role: 1 }
                },
                {
                    path: 'admin/category-stats',
                    name: 'categoryStats',
                    component: () => import('../views/admin/CategoryStats.vue'),
                    meta: { title: '分类统计', requiresAuth: true, role: 1 }
                },
                {
                    path: 'announcements',
                    name: 'announcements',
                    component: () => import('../views/announcement/AnnouncementList.vue'),
                    meta: { title: '系统公告', requiresAuth: true }
                },
                {
                    path: 'admin/announcements',
                    name: 'announcementAdmin',
                    component: () => import('../views/admin/AnnouncementAdmin.vue'),
                    meta: { title: '公告管理', requiresAuth: true, role: 1 }
                },
                {
                    path: 'home',
                    name: 'home',
                    component: () => import('../views/home/Home.vue'),
                    meta: { title: '门户首页', requiresAuth: true }
                }
            ]
        }
    ]
})

router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore()
    const token = userStore.token

    if (to.meta.requiresAuth && !token) {
        next('/login')
    } else if (token && (to.path === '/login' || to.path === '/register')) {
        next('/')
    } else {
        // Check role
        if (to.meta.role !== undefined) {
            // ensure user info is loaded
            if (!userStore.userInfo.id) {
                try {
                    await userStore.getProfile()
                } catch (e) {
                    userStore.logout()
                    next('/login')
                    return
                }
            }

            if (userStore.role !== to.meta.role) {
                if (userStore.role !== 1) { // Assuming only admin check needed for now
                    next('/dashboard') // or 403 page
                    return
                }
            }
        }
        next()
    }
})

export default router
