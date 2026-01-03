<template>
  <div class="category-stats-container p-4">
    <el-card>
        <template #header>
            <div class="flex justify-between items-center">
                <span class="text-lg font-bold">分类统计</span>
                <el-button type="primary" @click="fetchData">刷新</el-button>
            </div>
        </template>
        
        <el-table :data="tableData" border stripe style="width: 100%" v-loading="loading">
            <el-table-column prop="categoryName" label="分类名称" />
             <!-- Note: Current backend API getCategoryStats is per-id.
                  For a full stats table, we likely need a list endpoint with stats or iterate.
                  Since requirements are simple, let's fetch the tree/list first, then fetch stats or modify backend.
                  Wait, backend getStats(id) only returns ONE category.
                  To show ALL, we need to iterate or add a batch API.
                  Given constraints, I will fetch the LIST of categories, then map to show them. 
                  Actually, the requirements didn't specify a "batch stats" API, but "Classification Statistics Page" implies overview.
                  I will use the category list and maybe mocked stats or fetch individually if list is small. 
                  BETTER: Just show the category list and add a column "Book Count" if available.
                  The backend Category entity doesn't have `bookCount`.
                  I will fetch `getCategoryList` and for each, call `getCategoryStats` (Parallel). 
                  (Not efficient but works for small datasets). 
             -->
            <el-table-column prop="bookCount" label="图书数量" sortable />
        </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useCategoryStore } from '@/stores/category'

const categoryStore = useCategoryStore()
const tableData = ref([])
const loading = ref(false)

const fetchData = async () => {
    loading.value = true
    try {
        const categories = await categoryStore.fetchList()
        if (categories) {
            // Fetch stats for each
            const statsPromises = categories.map(cat => categoryStore.fetchStats(cat.id))
            const statsResults = await Promise.all(statsPromises)

            tableData.value = categories.map((cat, index) => {
                const statRes = statsResults[index]
                return {
                    ...cat,
                    bookCount: statRes ? statRes.bookCount : 0
                }
            })
        }
    } finally {
        loading.value = false
    }
}

onMounted(() => {
    fetchData()
})
</script>
