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
          background-color="#ffffff"
          text-color="#303133"
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

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-button
            type="text"
            class="toggle-button"
            @click="toggleSidebar"
            :aria-label="isCollapse ? '展开菜单' : '折叠菜单'"
          >
            <component :is="isCollapse ? Expand : Fold" />
          </el-button>
          <breadcrumb />
        </div>
        <div class="header-right">
          <el-dropdown trigger="click" @command="handleCommand">
            <span class="el-dropdown-link header-action">
              <bell />
              <el-badge :is-dot="true" class="notification-badge" />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item disabled>
                  <div class="dropdown-header">消息通知</div>
                </el-dropdown-item>
                <el-dropdown-item divided>
                  暂无新消息
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-dropdown trigger="click" @command="handleCommand">
            <span class="el-dropdown-link header-action header-avatar">
              <el-avatar :size="32" :src="avatarUrl" alt="用户头像">
                <user-filled />
              </el-avatar>
              <span v-show="!isCollapse" class="username">{{ name }}</span>
              <arrow-down />
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item :command="'profile'">
                  <user-filled /> 个人中心
                </el-dropdown-item>
                <el-dropdown-item :command="'password'">
                  <lock /> 修改密码
                </el-dropdown-item>
                <el-dropdown-item divided :command="'setting'">
                  <setting /> 设置
                </el-dropdown-item>
                <el-dropdown-item :command="'logout'">
                  <logout /> 退出登录
                </el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>

          <el-button
            type="text"
            class="toggle-button"
            @click="toggleSidebar"
            :aria-label="isCollapse ? '展开菜单' : '折叠菜单'"
          >
            <component :is="isCollapse ? Expand : Fold" />
          </el-button>
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
  HomeFilled, UserFilled, Lock, Setting, Logout, Bell, ArrowDown,
  Expand, Fold, User, Menu as MenuIcon, House, OfficeBuilding, Grid,
  Tickets, SwitchButton, StarFilled, ChatLineSquare, Document, Megaphone,
  MessageBox, DocumentAdd, Key, Parking, Truck, DocumentCopy, Money, Coin,
  DocumentChecked, ListCheck, ScaleToOriginal, WarningFilled, RefreshRight,
  Wrench, Monitor, Timer, Box, Cpu, Tools, Avatar
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
const avatarUrl = computed(() => avatar.value || 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif')

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
  if (!icon) return MenuIcon
  const icons = {
    HomeFilled, User, UserFilled, Avatar, MenuIcon, Setting, Lock, Logout, Bell, MessageBox,
    House, OfficeBuilding, Grid, Tickets, SwitchButton, StarFilled, ChatLineSquare,
    Document, Megaphone, MessageBox, DocumentAdd, Key, Parking, Truck, DocumentCopy,
    Money, Coin, DocumentChecked, ListCheck, ScaleToOriginal, WarningFilled, RefreshRight,
    Wrench, Monitor, Timer, Box, Cpu, Tools
  }
  return icons[icon] || MenuIcon
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
  background-color: #fff;
  border-right: 1px solid #ebeef5;
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
  padding: 0 10px;
  border-bottom: 1px solid #ebeef5;
  transition: all 0.3s ease;

  &.collapse {
    justify-content: center;
    padding: 0;
  }
}

.logo-image {
  width: 30px;
  height: 30px;
  margin-right: 10px;
  flex-shrink: 0;
}

.logo-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
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
  padding: 0 20px;
  background-color: #fff;
  border-bottom: 1px solid #ebeef5;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  position: sticky;
  top: 0;
  z-index: 50;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.toggle-button {
  padding: 8px;
  color: #606266;
  font-size: 18px;

  &:hover {
    color: #409eff;
    background-color: #f5f7fa;
  }
}

.header-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.header-action {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 10px;
  color: #606266;
  cursor: pointer;
  transition: color 0.3s;

  &:hover {
    color: #409eff;
  }
}

.notification-badge {
  margin-left: -8px;
}

.header-avatar {
  padding: 0 5px 0 10px;
  border-left: 1px solid #ebeef5;
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

/* 菜单样式覆盖 */
:deep(.el-menu--vertical) {
  border-right: none;
}

:deep(.el-menu-item) {
  height: auto;
  padding: 0 15px;
  line-height: normal;
  border-radius: 6px;
  margin: 4px 8px;

  &:hover {
    background-color: #f5f7fa;
  }

  &.is-active {
    background-color: rgba(64, 158, 255, 0.1);
    color: #409eff;
    font-weight: 500;

    .el-icon {
      color: #409eff;
    }
  }
}

:deep(.el-sub-menu__title) {
  height: auto;
  padding: 0 15px;
  line-height: normal;
  border-radius: 6px;
  margin: 4px 8px;

  &:hover {
    background-color: #f5f7fa;
  }
}

:deep(.el-menu--collapse .el-sub-menu__title) {
  padding: 0;
  margin: 4px 0;
  text-align: center;
}

:deep(.el-menu-item__content) {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 38px;
}

:deep(.el-sub-menu__title-content) {
  display: flex;
  align-items: center;
  gap: 12px;
  height: 38px;
}

:deep(.el-menu--collapse .el-menu-item__content,
  .el-menu--collapse .el-sub-menu__title-content) {
  justify-content: center;
  gap: 0;
}

/* 面包屑样式 */
:deep(.el-breadcrumb) {
  font-size: 14px;
}

:deep(.el-breadcrumb__inner) {
  color: #606266;

  &:hover {
    color: #409eff;
  }
}

:deep(.el-breadcrumb__inner.is-link) {
  color: #409eff;
}

:deep(.el-breadcrumb__separator) {
  color: #c0c4cc;
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

/* 滚动条样式 */
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
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    transform: translateX(-100%);
    transition: transform 0.3s ease;

    &.collapse {
      transform: translateX(-100%);
      width: 210px;
    }

    &.open {
      transform: translateX(0);
    }
  }

  .layout-main {
    padding: 15px;
  }
}
</style>