<template>
  <el-container class="layout-container">
    <el-aside :class="['layout-aside', { collapse: isCollapse }]" :width="isCollapse ? '64px' : '210px'">
      <div class="logo-container" :class="{ collapse: isCollapse }">
        <el-image class="logo-image" src="@/assets/logo.png" fit="contain" />
        <span v-show="!isCollapse" class="logo-title">物业管理系统</span>
      </div>
      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :unique-opened="true"
          :collapse="isCollapse"
          :collapse-transition="false"
          mode="vertical"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409eff"
          router
          @select="handleSelect"
          @openchange="handleOpenChange"
        >
          <el-menu-item index="/dashboard">
            <home-filled />
            <template #title>首页</template>
          </el-menu-item>
          <template v-for="route in sidebarRouters" :key="route.path">
            <component :is="renderMenuComponent(route)" :route="route" />
          </template>
        </el-menu>
      </el-scrollbar>
    </el-aside>

    <el-container class="main-container">
      <el-header class="layout-header" height="50px">
        <div class="header-left">
          <el-icon class="toggle-button" @click="toggleSidebar" :size="20">
            <component :is="isCollapse ? Expand : Fold" />
          </el-icon>
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-tooltip content="全屏" placement="bottom">
            <el-icon class="header-action" :size="18" @click="toggleFullScreen">
              <full-screen />
            </el-icon>
          </el-tooltip>
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="header-action header-avatar">
              <el-avatar :size="30" :src="avatarUrl">
                <user-filled />
              </el-avatar>
              <span class="username">{{ name }}</span>
              <el-icon><arrow-down /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">
                  <user-filled /> 个人中心
                </el-dropdown-item>
                <el-dropdown-item command="password">
                  <lock /> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided command="logout">
                  <switch-button /> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view v-slot="{ Component }">
          <transition name="fade" mode="out-in">
            <component :is="Component" />
          </transition>
        </router-view>
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter, useRoute } from 'vue-router'
import { storeToRefs } from 'pinia'
import { computed, ref, onMounted, watch } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { usePermissionStore } from '@/store/modules/permission'
import { useAppStore } from '@/store/modules/app'
import {
  HomeFilled, User, UserFilled, Avatar, Lock, Setting, SwitchButton, ArrowDown,
  Expand, Fold, FullScreen, Menu, House, OfficeBuilding,
  Tickets, ChatLineSquare, Notification, DocumentCopy, Money, Coin,
  DocumentChecked, WarningFilled, Tools, Monitor, Timer, DataAnalysis,
  DataBoard, List, BellFilled, Collection
} from '@element-plus/icons-vue'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import Breadcrumb from '@/components/Breadcrumb/index.vue'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const permissionStore = usePermissionStore()
const appStore = useAppStore()

const { sidebar } = storeToRefs(appStore)
const { name, avatar, roles } = storeToRefs(userStore)
const { sidebarRouters } = storeToRefs(permissionStore)

const isCollapse = computed(() => !sidebar.value.opened)
const activeMenu = ref(route.path)
const avatarUrl = computed(() => avatar.value || '')

const handleSelect = (key, keyPath) => {
  if (key === '/') return
  router.push(key).catch(() => {})
}

const handleOpenChange = (names) => {
  if (isCollapse.value) return
  activeMenu.value = names[names.length - 1]
}

const toggleSidebar = () => {
  appStore.toggleSidebar()
}

const toggleFullScreen = () => {
  if (!document.fullscreenElement) {
    document.documentElement.requestFullscreen()
  } else {
    document.exitFullscreen()
  }
}

const handleCommand = (command) => {
  switch (command) {
    case 'profile':
      router.push('/profile/index')
      break
    case 'password':
      router.push('/profile/password')
      break
    case 'setting':
      router.push('/profile/index')
      break
    case 'logout':
      userStore.logout()
      router.push('/login')
      break
  }
}

const getMenuIcon = (icon) => {
  if (!icon) return Menu
  const icons = {
    HomeFilled, User, UserFilled, Avatar, Menu, Setting, Lock, SwitchButton, Bell, BellFilled,
    House, OfficeBuilding, Grid, Tickets, StarFilled, ChatLineSquare, EditPen, Star,
    Document, Notification, MessageBox, DocumentAdd, Key, Location, Van, DocumentCopy,
    Money, Coin, DocumentChecked, Finished, ScaleToOriginal, WarningFilled, RefreshRight,
    Tools, Monitor, Timer, Box, Cpu, DataAnalysis, DataBoard, List
  }
  return icons[icon] || Menu
}

const renderMenuComponent = (route) => {
  if (route.hidden) return () => null

  const hasChildren = route.children && route.children.length > 0

  if (hasChildren) {
    return {
      name: `SubMenu-${route.name}`,
      props: ['route'],
      template: `
        <el-sub-menu :index="route.path">
          <template #title>
            <component :is="getMenuIcon(route.meta?.icon)" />
            <span>{{ route.meta?.title }}</span>
          </template>
          <template v-for="child in route.children" :key="child.path">
            <component :is="renderMenuComponent(child)" :route="child" />
          </template>
        </el-sub-menu>
      `,
      methods: { getMenuIcon, renderMenuComponent }
    }
  }

  return {
    name: `MenuItem-${route.name}`,
    props: ['route'],
    template: `
      <el-menu-item :index="route.path">
        <component :is="getMenuIcon(route.meta?.icon)" />
        <span>{{ route.meta?.title }}</span>
      </el-menu-item>
    `,
    methods: { getMenuIcon }
  }
}

watch(() => route.path, (val) => {
  activeMenu.value = val
})

onMounted(() => {
  NProgress.configure({ showSpinner: false })
})
</script>

<style lang="scss" scoped>
.layout-container {
  height: 100%;
  width: 100%;
  overflow: hidden;
}

.layout-aside {
  height: 100%;
  background-color: #304156;
  transition: width 0.3s ease;
  overflow: hidden;
  z-index: 100;

  &.collapse {
    width: 64px;
  }
}

.logo-container {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 15px;
  background-color: #2b2f3a;
  transition: all 0.3s ease;

  &.collapse {
    justify-content: center;
    padding: 0;
  }
}

.logo-image {
  width: 32px;
  height: 32px;
  margin-right: 10px;
  flex-shrink: 0;
}

.logo-title {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
  white-space: nowrap;
  overflow: hidden;
}

.menu-scrollbar {
  height: calc(100% - 50px);
}

.layout-header {
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  background-color: #fff;
  border-bottom: 1px solid #d8dce5;
  box-shadow: 0 1px 3px 0 rgba(0, 0, 0, 0.12), 0 0 3px 0 rgba(0, 0, 0, 0.04);
  position: sticky;
  top: 0;
  z-index: 9;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-button {
  padding: 8px;
  cursor: pointer;
  color: #5a5e66;
  transition: color 0.3s;

  &:hover {
    color: #409eff;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 15px;
}

.header-action {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 0 5px;
  color: #5a5e66;
  cursor: pointer;
  font-size: 14px;
  transition: color 0.3s;

  &:hover {
    color: #409eff;
  }
}

.header-avatar {
  padding: 0 10px;
  border-left: 1px solid #d8dce5;
}

.username {
  font-size: 14px;
  color: #303133;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.layout-main {
  padding: 20px;
  background-color: #f0f2f5;
  min-height: calc(100vh - 50px);
  overflow: auto;
}

/* 菜单样式 */
:deep(.el-menu--vertical) {
  border-right: none;
}

:deep(.el-menu-item),
:deep(.el-sub-menu__title) {
  height: 50px;
  line-height: 50px;

  &:hover {
    background-color: #263445 !important;
  }
}

:deep(.el-menu-item.is-active) {
  background-color: #409eff !important;
  color: #fff !important;
}

:deep(.el-sub-menu .el-menu-item) {
  min-width: 0 !important;
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0;
  text-align: center;
}

/* 过渡动画 */
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

/* 滚动条 */
:deep(.el-scrollbar__wrap) {
  overflow-x: hidden !important;
}

/* 响应式 */
@media (max-width: 768px) {
  .layout-aside {
    position: fixed;
    left: 0;
    top: 0;
    height: 100vh;
    z-index: 1000;

    &.collapse {
      width: 210px !important;
    }
  }

  .layout-main {
    padding: 15px;
  }
}
</style>