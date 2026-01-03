import request from '@/utils/request'

// 基础图书搜索
export function searchBooks(params) {
    return request({
        url: '/search/books',
        method: 'get',
        params
    })
}

// 高级图书搜索
export function advancedSearch(data) {
    return request({
        url: '/search/books/advanced',
        method: 'post',
        data
    })
}

// 获取搜索建议
export function getSuggestions(keyword, limit = 10) {
    return request({
        url: '/search/suggest',
        method: 'get',
        params: { keyword, limit }
    })
}

// 全局搜索（图书+公告）
export function globalSearch(keyword, limit = 5) {
    return request({
        url: '/search/global',
        method: 'get',
        params: { keyword, limit }
    })
}
