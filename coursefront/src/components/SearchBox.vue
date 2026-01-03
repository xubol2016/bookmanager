<template>
  <div class="search-box">
    <!-- 主搜索框 -->
    <div class="search-main">
      <el-autocomplete
        v-model="keyword"
        :fetch-suggestions="querySearch"
        placeholder="搜索图书（书名、作者、ISBN）"
        :trigger-on-focus="true"
        class="search-input"
        popper-class="search-suggestions"
        @select="handleSelect"
        @keyup.enter="handleSearch"
        @focus="showHistory = true"
        @blur="handleBlur"
      >
        <template #prefix>
          <el-icon><Search /></el-icon>
        </template>
        <template #suffix>
          <div class="suffix-actions">
            <el-icon v-if="keyword" class="clear-icon" @click.stop="clearKeyword">
              <Close />
            </el-icon>
            <el-divider direction="vertical" />
            <el-tooltip content="高级搜索" placement="bottom">
              <el-icon class="advanced-icon" @click.stop="showAdvanced = true">
                <Filter />
              </el-icon>
            </el-tooltip>
          </div>
        </template>
        <template #default="{ item }">
          <div class="suggestion-item">
            <el-icon v-if="item.type === 'history'" class="history-icon">
              <Clock />
            </el-icon>
            <el-icon v-else class="search-icon">
              <Search />
            </el-icon>
            <span class="suggestion-text">{{ item.value }}</span>
            <el-icon
              v-if="item.type === 'history'"
              class="delete-icon"
              @click.stop="removeHistory(item.value)"
            >
              <Close />
            </el-icon>
          </div>
        </template>
      </el-autocomplete>
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        搜索
      </el-button>
    </div>

    <!-- 搜索历史标签 -->
    <div v-if="searchHistory.length > 0 && !keyword" class="history-tags">
      <span class="history-label">搜索历史：</span>
      <el-tag
        v-for="item in searchHistory.slice(0, 5)"
        :key="item"
        size="small"
        class="history-tag"
        closable
        @click="quickSearch(item)"
        @close="removeHistory(item)"
      >
        {{ item }}
      </el-tag>
      <el-button
        v-if="searchHistory.length > 0"
        link
        type="danger"
        size="small"
        @click="clearHistory"
      >
        清空历史
      </el-button>
    </div>

    <!-- 高级搜索对话框 -->
    <el-dialog
      v-model="showAdvanced"
      title="高级搜索"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="advancedForm" label-width="100px" class="advanced-form">
        <el-form-item label="书名">
          <el-input v-model="advancedForm.title" placeholder="请输入书名关键词" clearable />
        </el-form-item>
        <el-form-item label="作者">
          <el-input v-model="advancedForm.author" placeholder="请输入作者名" clearable />
        </el-form-item>
        <el-form-item label="ISBN">
          <el-input v-model="advancedForm.isbn" placeholder="请输入ISBN号" clearable />
        </el-form-item>
        <el-form-item label="出版社">
          <el-input v-model="advancedForm.publisher" placeholder="请输入出版社" clearable />
        </el-form-item>
        <el-form-item label="分类">
          <el-cascader
            v-model="advancedForm.categoryId"
            :options="categoryTree"
            :props="{ value: 'id', label: 'name', children: 'children', checkStrictly: true, emitPath: false }"
            placeholder="请选择分类"
            clearable
            class="w-full"
          />
        </el-form-item>
        <el-form-item label="出版年份">
          <div class="year-range">
            <el-date-picker
              v-model="advancedForm.publishYearStart"
              type="year"
              placeholder="开始年份"
              format="YYYY"
              value-format="YYYY"
              class="year-picker"
            />
            <span class="year-separator">至</span>
            <el-date-picker
              v-model="advancedForm.publishYearEnd"
              type="year"
              placeholder="结束年份"
              format="YYYY"
              value-format="YYYY"
              class="year-picker"
            />
          </div>
        </el-form-item>
        <el-form-item label="库存状态">
          <el-radio-group v-model="advancedForm.hasStock">
            <el-radio :value="null">全部</el-radio>
            <el-radio :value="true">有库存</el-radio>
            <el-radio :value="false">无库存</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="resetAdvancedForm">重置</el-button>
        <el-button type="primary" @click="handleAdvancedSearch">搜索</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Search, Close, Filter, Clock } from '@element-plus/icons-vue'
import { getSuggestions, advancedSearch } from '@/api/search'
import { getCategoryTree } from '@/api/category'

const props = defineProps({
  // 是否显示历史标签
  showHistoryTags: {
    type: Boolean,
    default: true
  },
  // 初始关键词
  initialKeyword: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['search', 'advancedSearch'])

const router = useRouter()
const route = useRoute()

const keyword = ref(props.initialKeyword)
const showHistory = ref(false)
const showAdvanced = ref(false)
const categoryTree = ref([])

// 搜索历史
const HISTORY_KEY = 'book_search_history'
const MAX_HISTORY = 10
const searchHistory = ref([])

// 高级搜索表单
const advancedForm = reactive({
  title: '',
  author: '',
  isbn: '',
  publisher: '',
  categoryId: null,
  publishYearStart: null,
  publishYearEnd: null,
  hasStock: null
})

// 加载搜索历史
const loadHistory = () => {
  try {
    const history = localStorage.getItem(HISTORY_KEY)
    searchHistory.value = history ? JSON.parse(history) : []
  } catch (e) {
    searchHistory.value = []
  }
}

// 保存搜索历史
const saveHistory = (keyword) => {
  if (!keyword || !keyword.trim()) return
  const trimmed = keyword.trim()

  // 移除重复项
  const filtered = searchHistory.value.filter(item => item !== trimmed)
  // 添加到开头
  filtered.unshift(trimmed)
  // 限制数量
  searchHistory.value = filtered.slice(0, MAX_HISTORY)

  localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
}

// 移除单条历史
const removeHistory = (item) => {
  searchHistory.value = searchHistory.value.filter(h => h !== item)
  localStorage.setItem(HISTORY_KEY, JSON.stringify(searchHistory.value))
}

// 清空历史
const clearHistory = () => {
  searchHistory.value = []
  localStorage.removeItem(HISTORY_KEY)
}

// 获取搜索建议
const querySearch = async (queryString, cb) => {
  const suggestions = []

  // 如果有输入，先搜索API建议
  if (queryString && queryString.trim()) {
    try {
      const res = await getSuggestions(queryString.trim())
      if (res && res.length > 0) {
        res.forEach(item => {
          suggestions.push({ value: item, type: 'suggestion' })
        })
      }
    } catch (e) {
      console.error('获取建议失败', e)
    }
  }

  // 添加匹配的历史记录
  const matchedHistory = searchHistory.value
    .filter(item => !queryString || item.toLowerCase().includes(queryString.toLowerCase()))
    .slice(0, 5)
    .map(item => ({ value: item, type: 'history' }))

  // 历史记录放在前面
  const result = [...matchedHistory]

  // 添加API建议（去重）
  suggestions.forEach(s => {
    if (!result.find(r => r.value === s.value)) {
      result.push(s)
    }
  })

  cb(result.slice(0, 10))
}

// 选择建议项
const handleSelect = (item) => {
  keyword.value = item.value
  handleSearch()
}

// 执行搜索
const handleSearch = () => {
  if (!keyword.value || !keyword.value.trim()) return

  // 保存历史
  saveHistory(keyword.value)

  // 触发搜索事件
  emit('search', keyword.value.trim())

  // 跳转到图书列表页
  router.push({
    path: '/books',
    query: { search: keyword.value.trim() }
  })
}

// 快速搜索（点击历史标签）
const quickSearch = (item) => {
  keyword.value = item
  handleSearch()
}

// 清空关键词
const clearKeyword = () => {
  keyword.value = ''
}

// 失焦处理
const handleBlur = () => {
  setTimeout(() => {
    showHistory.value = false
  }, 200)
}

// 加载分类树
const loadCategoryTree = async () => {
  try {
    const res = await getCategoryTree()
    if (res) {
      categoryTree.value = res
    }
  } catch (e) {
    console.error('加载分类失败', e)
  }
}

// 重置高级搜索表单
const resetAdvancedForm = () => {
  advancedForm.title = ''
  advancedForm.author = ''
  advancedForm.isbn = ''
  advancedForm.publisher = ''
  advancedForm.categoryId = null
  advancedForm.publishYearStart = null
  advancedForm.publishYearEnd = null
  advancedForm.hasStock = null
}

// 执行高级搜索
const handleAdvancedSearch = async () => {
  // 构建搜索条件
  const searchConditions = {}

  if (advancedForm.title) searchConditions.title = advancedForm.title
  if (advancedForm.author) searchConditions.author = advancedForm.author
  if (advancedForm.isbn) searchConditions.isbn = advancedForm.isbn
  if (advancedForm.publisher) searchConditions.publisher = advancedForm.publisher
  if (advancedForm.categoryId) searchConditions.categoryId = advancedForm.categoryId
  if (advancedForm.publishYearStart) searchConditions.publishYearStart = parseInt(advancedForm.publishYearStart)
  if (advancedForm.publishYearEnd) searchConditions.publishYearEnd = parseInt(advancedForm.publishYearEnd)
  if (advancedForm.hasStock !== null) searchConditions.hasStock = advancedForm.hasStock

  // 检查是否有任何搜索条件
  if (Object.keys(searchConditions).length === 0) {
    return
  }

  showAdvanced.value = false

  // 触发高级搜索事件
  emit('advancedSearch', searchConditions)

  // 构建查询参数跳转
  const query = {}
  if (searchConditions.title) query.title = searchConditions.title
  if (searchConditions.author) query.author = searchConditions.author
  if (searchConditions.isbn) query.isbn = searchConditions.isbn
  if (searchConditions.publisher) query.publisher = searchConditions.publisher
  if (searchConditions.categoryId) query.categoryId = searchConditions.categoryId
  if (searchConditions.publishYearStart) query.yearStart = searchConditions.publishYearStart
  if (searchConditions.publishYearEnd) query.yearEnd = searchConditions.publishYearEnd
  if (searchConditions.hasStock !== null) query.hasStock = searchConditions.hasStock
  query.advanced = 1

  router.push({
    path: '/books',
    query
  })
}

onMounted(() => {
  loadHistory()
  loadCategoryTree()

  // 从URL获取初始搜索词
  if (route.query.search) {
    keyword.value = route.query.search
  }
})
</script>

<style scoped>
.search-box {
  width: 100%;
}

.search-main {
  display: flex;
  gap: 10px;
}

.search-input {
  flex: 1;
}

.search-input :deep(.el-input__wrapper) {
  padding-right: 8px;
}

.suffix-actions {
  display: flex;
  align-items: center;
  gap: 4px;
}

.clear-icon,
.advanced-icon {
  cursor: pointer;
  color: #909399;
  transition: color 0.2s;
}

.clear-icon:hover,
.advanced-icon:hover {
  color: #409eff;
}

.suggestion-item {
  display: flex;
  align-items: center;
  gap: 8px;
  width: 100%;
}

.history-icon {
  color: #909399;
}

.search-icon {
  color: #409eff;
}

.suggestion-text {
  flex: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.delete-icon {
  color: #c0c4cc;
  cursor: pointer;
  opacity: 0;
  transition: opacity 0.2s;
}

.suggestion-item:hover .delete-icon {
  opacity: 1;
}

.delete-icon:hover {
  color: #f56c6c;
}

.history-tags {
  margin-top: 10px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 8px;
}

.history-label {
  font-size: 12px;
  color: #909399;
}

.history-tag {
  cursor: pointer;
  transition: all 0.2s;
}

.history-tag:hover {
  background-color: #409eff;
  color: white;
}

.advanced-form {
  padding: 20px 0;
}

.year-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.year-picker {
  width: 140px;
}

.year-separator {
  color: #909399;
}

.w-full {
  width: 100%;
}
</style>

<style>
/* 全局样式，用于popper */
.search-suggestions {
  max-height: 400px;
}

.search-suggestions .el-autocomplete-suggestion li {
  padding: 8px 15px;
}
</style>
