import { defineStore } from 'pinia'
import { ref } from 'vue'
import { applyBorrow, returnBook, getMyRecords, getBorrowList, getOverdueList } from '@/api/borrow'

export const useBorrowStore = defineStore('borrow', () => {

    async function apply(data) {
        return await applyBorrow(data)
    }

    async function returnBookAction(recordId) {
        return await returnBook(recordId)
    }

    async function fetchMyRecords(params) {
        return await getMyRecords(params)
    }

    async function fetchAdminList(params) {
        return await getBorrowList(params)
    }

    async function fetchOverdueList(params) {
        return await getOverdueList(params)
    }

    return {
        apply,
        returnBookAction,
        fetchMyRecords,
        fetchAdminList,
        fetchOverdueList
    }
})
