<template>
  <div id="app">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { onBeforeRouteUpdate } from 'vue-router'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'

const router = useRouter()
const userStore = useUserStore()
const permissionStore = usePermissionStore()

const refreshRoutes = async () => {
  await permissionStore.generateRoutes()
  permissionStore.addRoutes.forEach(route => {
    router.addRoute(route)
  })
}

onBeforeRouteUpdate(async (to, from, next) => {
  if (to.meta.roles && to.meta.roles.length > 0) {
    if (!permissionStore.hasPermission(to.meta.roles)) {
      await refreshRoutes()
      return next({ ...to, replace: true })
    }
  }
  next()
})

if (userStore.token) {
  userStore.getInfo()
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
</style>