<template>
  <div class="breadcrumb-container">
    <el-breadcrumb separator="/" class="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in breadcrumbs" :key="index">
        <router-link v-if="item.path && index < breadcrumbs.length - 1" :to="item.path">{{ item.name }}</router-link>
        <span v-else>{{ item.name }}</span>
      </el-breadcrumb-item>
    </el-breadcrumb>
  </div>
</template>

<script setup>
import { useRoute, useRouter } from 'vue-router'
import { computed } from 'vue'

const route = useRoute()
const router = useRouter()

const props = defineProps({
  routes: { type: Array, default: () => [] }
})

const breadcrumbs = computed(() => {
  if (props.routes.length > 0) return props.routes

  const matched = route.matched.filter(m => m.meta?.title)
  return matched.map(m => ({
    name: m.meta.title,
    path: m.path === '/' ? undefined : m.path
  }))
})
</script>

<style lang="scss" scoped>
.breadcrumb-container {
  .breadcrumb {
    font-size: 14px;

    :deep(.el-breadcrumb__inner) {
      color: #606266;
      font-weight: 500;

      &:hover {
        color: #409eff;
      }
    }

    :deep(.el-breadcrumb__inner.is-link) {
      color: #409eff;
      font-weight: 500;
    }

    :deep(.el-breadcrumb__separator) {
      color: #c0c4cc;
    }
  }
}
</style>