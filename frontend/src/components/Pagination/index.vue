<template>
  <div class="pagination-container" :class="{ hidden: hidden }">
    <el-pagination
      v-model:current-page="currentPage"
      v-model:page-size="pageSize"
      :background="background"
      :layout="layout"
      :page-sizes="pageSizes"
      :pager-count="pagerCount"
      :total="total"
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
    />
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  total: { type: Number, required: true },
  page: { type: Number, default: 1 },
  limit: { type: Number, default: 20 },
  pageSizes: { type: Array, default: () => [10, 20, 30, 50] },
  pagerCount: { type: Number, default: window.innerWidth < 992 ? 5 : 7 },
  layout: { type: String, default: 'total, sizes, prev, pager, next, jumper' },
  background: { type: Boolean, default: true },
  autoScroll: { type: Boolean, default: true },
  hidden: { type: Boolean, default: false }
})

const emit = defineEmits(['update:page', 'update:limit', 'pagination'])

const currentPage = computed({
  get() { return props.page },
  set(val) { emit('update:page', val) }
})

const pageSize = computed({
  get() { return props.limit },
  set(val) { emit('update:limit', val) }
})

const handleSizeChange = (val) => {
  if (currentPage.value * val > props.total) {
    currentPage.value = 1
  }
  emit('pagination', { page: currentPage.value, limit: val })
}

const handleCurrentChange = (val) => {
  emit('pagination', { page: val, limit: pageSize.value })
}
</script>

<style lang="scss" scoped>
.pagination-container {
  padding: 16px 0;
  display: flex;
  justify-content: flex-end;
}

.hidden {
  display: none;
}
</style>
