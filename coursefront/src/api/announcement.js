import request from '@/utils/request'

// 获取首页公告列表（已发布）
export function getTopAnnouncements(limit = 5) {
    return request({
        url: '/announcements/top',
        method: 'get',
        params: { limit }
    })
}

// 分页查询已发布的公告（普通用户）
export function getPublishedAnnouncements(params) {
    return request({
        url: '/announcements',
        method: 'get',
        params
    })
}

// 获取公告详情
export function getAnnouncementDetail(id) {
    return request({
        url: `/announcements/${id}`,
        method: 'get'
    })
}

// 管理员：分页查询所有公告
export function getAnnouncementPage(params) {
    return request({
        url: '/admin/announcements',
        method: 'get',
        params
    })
}

// 管理员：获取待发布的公告
export function getPendingAnnouncements() {
    return request({
        url: '/admin/announcements/pending',
        method: 'get'
    })
}

// 管理员：获取公告统计
export function getAnnouncementStats() {
    return request({
        url: '/admin/announcements/stats',
        method: 'get'
    })
}

// 管理员：发布公告
export function addAnnouncement(data) {
    return request({
        url: '/admin/announcements',
        method: 'post',
        data
    })
}

// 管理员：定时发布公告
export function scheduleAnnouncement(data) {
    return request({
        url: '/admin/announcements/schedule',
        method: 'post',
        data
    })
}

// 管理员：更新公告
export function updateAnnouncement(data) {
    return request({
        url: '/admin/announcements',
        method: 'put',
        data
    })
}

// 管理员：设置/取消置顶
export function toggleTop(id, isTop) {
    return request({
        url: `/admin/announcements/${id}/top`,
        method: 'put',
        params: { isTop }
    })
}

// 管理员：删除公告
export function deleteAnnouncement(id) {
    return request({
        url: `/admin/announcements/${id}`,
        method: 'delete'
    })
}
