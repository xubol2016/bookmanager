import { defineStore } from 'pinia'
import { ref } from 'vue'
import {
    getBookPage,
    getBookById,
    addBook as addBookApi,
    updateBook as updateBookApi,
    deleteBook as deleteBookApi,
    batchDeleteBooks as batchDeleteBooksApi,
    uploadCover as uploadCoverApi
} from '@/api/book'

export const useBookStore = defineStore('book', () => {

    // State
    const bookList = ref([])
    const total = ref(0)
    const currentBook = ref(null)
    const loading = ref(false)

    // Actions
    async function fetchBooks(params) {
        loading.value = true
        try {
            const res = await getBookPage(params)
            if (res) {
                bookList.value = res.records
                total.value = res.total
            }
            return res
        } finally {
            loading.value = false
        }
    }

    async function fetchBookDetail(id) {
        loading.value = true
        try {
            const res = await getBookById(id)
            if (res) {
                currentBook.value = res
            }
            return res
        } finally {
            loading.value = false
        }
    }

    async function addBook(data) {
        return await addBookApi(data)
    }

    async function updateBook(data) {
        return await updateBookApi(data)
    }

    async function removeBook(id) {
        return await deleteBookApi(id)
    }

    async function removeBooks(ids) {
        return await batchDeleteBooksApi(ids)
    }

    async function uploadBookCover(file) {
        const formData = new FormData()
        formData.append('file', file)
        return await uploadCoverApi(formData)
    }

    // 手动设置 loading 状态
    function setLoading(value) {
        loading.value = value
    }

    // 手动设置图书列表
    function setBooks(books, totalCount) {
        bookList.value = books
        total.value = totalCount
    }

    return {
        bookList,
        total,
        currentBook,
        loading,
        fetchBooks,
        fetchBookDetail,
        addBook,
        updateBook,
        removeBook,
        removeBooks,
        uploadBookCover,
        setLoading,
        setBooks
    }
})
