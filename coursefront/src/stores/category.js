import { defineStore } from 'pinia'
import { ref } from 'vue'
import { getCategoryTree, getCategoryList, addCategory, updateCategory, deleteCategory, getCategoryStats } from '@/api/category'

export const useCategoryStore = defineStore('category', () => {

    async function fetchTree() {
        return await getCategoryTree()
    }

    async function fetchList() {
        return await getCategoryList()
    }

    async function add(data) {
        return await addCategory(data)
    }

    async function update(id, data) {
        return await updateCategory(id, data)
    }

    async function remove(id) {
        return await deleteCategory(id)
    }

    async function fetchStats(id) {
        return await getCategoryStats(id)
    }

    return {
        fetchTree,
        fetchList,
        add,
        update,
        remove,
        fetchStats
    }
})
