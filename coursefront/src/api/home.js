import request from '@/utils/request'

// 获取首页基础统计数据
export function getHomeStats() {
    return request({
        url: '/home/stats',
        method: 'get'
    })
}

// 获取完整首页统计数据（含分类统计、借阅趋势）
export function getFullHomeStats() {
    return request({
        url: '/home/stats/full',
        method: 'get'
    })
}

// 获取推荐图书（最新上架）
export function getRecommendations() {
    return request({
        url: '/home/recommend',
        method: 'get'
    })
}

// 获取热门图书（按借阅量排序）
export function getHotBooks(limit = 10) {
    return request({
        url: '/home/hot',
        method: 'get',
        params: { limit }
    })
}

// 获取分类统计数据
export function getCategoryStats() {
    return request({
        url: '/home/category-stats',
        method: 'get'
    })
}

// 获取借阅趋势数据
export function getBorrowTrend(days = 7) {
    return request({
        url: '/home/borrow-trend',
        method: 'get',
        params: { days }
    })
}
