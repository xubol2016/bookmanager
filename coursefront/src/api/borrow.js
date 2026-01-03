import request from '@/utils/request'

export function applyBorrow(data) {
    return request({
        url: '/borrow/apply',
        method: 'post',
        data
    })
}

export function returnBook(recordId) {
    return request({
        url: '/borrow/return',
        method: 'post',
        params: { recordId }
    })
}

export function getMyRecords(params) {
    return request({
        url: '/borrow/my-records',
        method: 'get',
        params
    })
}

export function getBorrowList(params) {
    return request({
        url: '/admin/borrow/list',
        method: 'get',
        params
    })
}

export function getOverdueList(params) {
    return request({
        url: '/admin/borrow/overdue',
        method: 'get',
        params
    })
}
