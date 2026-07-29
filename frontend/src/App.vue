<template>
  <div id="app">
    <router-view v-slot="{ Component }" v-if="!hasError">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
    <div v-else class="error-boundary">
      <el-result icon="error" title="页面发生错误" sub-title="请刷新页面重试">
        <template #extra>
          <el-button type="primary" @click="handleReload">刷新页面</el-button>
        </template>
      </el-result>
    </div>
  </div>
</template>

<script setup>
import { ref, onErrorCaptured } from "vue"

const hasError = ref(false)

onErrorCaptured((err, instance, info) => {
  console.error("[ErrorBoundary]", err, info)
  hasError.value = true
  return false
})

const handleReload = () => {
  location.reload()
}
</script>

<style lang="scss">
#app {
  width: 100%;
  height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.error-boundary {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100vh;
}
</style>