<template>
  <el-tree-select
    v-model="internalValue"
    :data="treeData"
    :props="{ label: 'name', value: 'id', children: 'children' }"
    check-strictly
    :render-after-expand="false"
    placeholder="请选择分类"
    filterable
    clearable
    class="w-full"
    @current-change="emit('change', $event)"
  />
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { useCategoryStore } from '@/stores/category'

const props = defineProps({
    modelValue: {
        type: [Number, String],
        default: null
    }
})

const emit = defineEmits(['update:modelValue', 'change'])
const categoryStore = useCategoryStore()
const treeData = ref([])
const internalValue = ref(props.modelValue)

watch(() => props.modelValue, (val) => {
    internalValue.value = val
})

watch(internalValue, (val) => {
    emit('update:modelValue', val)
})

onMounted(async () => {
    try {
        const res = await categoryStore.fetchTree()
        if (res) {
            treeData.value = res
        }
    } catch (e) {
        console.error('Failed to load categories', e)
    }
})
</script>
