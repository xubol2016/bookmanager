import request from '@/utils/request'

export function getCategoryTree() {
    return request({
        url: '/categories/tree',
        method: 'get'
    })
}

export function getCategoryList() {
    return request({
        url: '/categories/list',
        method: 'get'
    })
}

export function addCategory(data) {
    return request({
        url: '/admin/categories',
        method: 'post',
        data
    })
}

export function updateCategory(id, data) {
    return request({
        url: `/admin/categories/${id}`,
        method: 'put',
        data
    })
}

export function deleteCategory(id) {
    return request({
        url: `/admin/categories/${id}`,
        method: 'delete'
    })
}

export function getCategoryStats(id) {
    return request({
        url: `/categories/${id}/stats`,
        method: 'get'
    })
}
