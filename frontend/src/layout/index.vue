<template>
  <div class="layout-shell">
    <aside class="layout-aside" :style="{ width: menuCollapsed ? '76px' : '248px' }">
      <div class="brand" @click="router.push('/dashboard')">
        <div class="brand-mark">物</div>
        <div v-show="!menuCollapsed" class="brand-copy">
          <strong>智慧物业</strong>
          <span>PROPERTY SERVICE</span>
        </div>
      </div>

      <el-scrollbar class="menu-scrollbar">
        <el-menu
          :default-active="activeMenu"
          :collapse="menuCollapsed"
          :collapse-transition="false"
          class="side-menu"
          router
        >
          <template v-for="menu in userMenus" :key="menu.id">
            <el-sub-menu v-if="menu.children && menu.children.length" :index="menu.path">
              <template #title>
                <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
                <span>{{ menu.menuName }}</span>
              </template>
              <el-menu-item v-for="child in menu.children" :key="child.id" :index="child.path">
                <el-icon v-if="child.icon"><component :is="child.icon" /></el-icon>
                <span>{{ child.menuName }}</span>
              </el-menu-item>
            </el-sub-menu>
            <el-menu-item v-else :index="menu.path">
              <el-icon v-if="menu.icon"><component :is="menu.icon" /></el-icon>
              <span>{{ menu.menuName }}</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-scrollbar>

      <div v-show="!menuCollapsed" class="aside-footer">
        <el-icon><Location /></el-icon>
        <span>社区运营管理平台</span>
      </div>
    </aside>

    <section class="layout-body">
      <header class="layout-header">
        <div class="header-left">
          <el-tooltip :content="isCollapse ? '展开菜单' : '收起菜单'" placement="bottom">
            <button class="icon-btn" type="button" aria-label="切换菜单" @click="isCollapse = !isCollapse">
              <el-icon :size="20"><Expand v-if="isCollapse" /><Fold v-else /></el-icon>
            </button>
          </el-tooltip>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/dashboard' }">
              <el-icon class="breadcrumb-home-icon"><HomeFilled /></el-icon>
              <span>首页</span>
            </el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">
              {{ item.meta.title }}
            </el-breadcrumb-item>
          </el-breadcrumb>
        </div>

        <div class="header-right">
          <div class="header-date">
            <el-icon><Calendar /></el-icon>
            <span>{{ dateText }}</span>
          </div>
          <el-dropdown trigger="click" @command="handleCommand">
            <div class="user-chip">
              <el-avatar :size="34" :src="userStore.avatar" class="user-avatar">{{ avatarText }}</el-avatar>
              <div class="user-copy">
                <strong>{{ userStore.realName }}</strong>
                <span>{{ roleText || '系统用户' }}</span>
              </div>
              <el-icon class="user-arrow"><ArrowDown /></el-icon>
            </div>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </header>

      <main class="layout-main" :class="{ 'layout-main--dashboard': route.path === '/dashboard' }">
        <div v-if="route.path !== '/dashboard'" class="page-heading">
          <div class="page-heading-icon">
            <el-icon :size="20"><component :is="pageIcon" /></el-icon>
          </div>
          <div>
            <h2>{{ pageTitle }}</h2>
            <p>{{ pageCaption }}</p>
          </div>
        </div>

        <router-view v-slot="{ Component }">
          <transition name="page-fade" mode="out-in">
            <component :is="Component" :key="route.path" />
          </transition>
        </router-view>
      </main>
    </section>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox, ElNotification } from 'element-plus'
import { getUserMenus } from '@/api/system/menu'
import { connectWebSocket, disconnectWebSocket } from '@/utils/ws'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const isMobile = ref(false)
const userMenus = ref([])

const menuCollapsed = computed(() => isCollapse.value || isMobile.value)
const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => route.matched.filter(r => r.meta.title && r.path !== '/'))

const pageTitle = computed(() => route.meta.title || '工作台')
const pageIcon = computed(() => route.meta.icon || 'Menu')
const pageCaption = computed(() => {
  const parent = breadcrumbs.value[0]
  if (parent && parent.meta.title !== pageTitle.value) return parent.meta.title
  return '工作台概览'
})

const dateText = computed(() => new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}))

const roleText = computed(() => {
  const roles = userStore.roles
  return Array.isArray(roles) ? (roles[0] || '') : (roles || '')
})

const avatarText = computed(() => (userStore.realName || userStore.userInfo.username || '用').slice(0, 1))

function syncViewport() {
  isMobile.value = window.innerWidth <= 768
}

onMounted(async () => {
  syncViewport()
  window.addEventListener('resize', syncViewport)
  connectWebSocket(handleWsMessage)
  if (!userStore.permissions || userStore.permissions.length === 0) {
    try { await userStore.getUserInfo() } catch (e) {}
  }
  try {
    const res = await getUserMenus()
    userMenus.value = res.data || []
  } catch (e) { userMenus.value = [] }
})

onUnmounted(() => {
  window.removeEventListener('resize', syncViewport)
})

function handleWsMessage(msg) {
  if (!msg || !msg.type) return
  const routeMap = {
    repair: '/repair/record',
    complaint: '/complaint',
    fee: '/fee/records',
    inspection: '/inspection/records',
    announcement: '/announcement',
    equipment: '/equipment/equipments'
  }
  ElNotification({
    title: msg.title || '新消息',
    message: msg.content || '',
    type: 'info',
    duration: 6000,
    onClick: () => {
      const target = routeMap[msg.type]
      if (target) router.push(target)
    }
  })
}

function handleCommand(command) {
  if (command === 'profile') {
    ElMessage.info('个人信息功能开发中，请稍候')
    return
  }
  if (command === 'password') {
    ElMessageBox.prompt('请输入新密码', '修改密码', {
      inputType: 'password',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    }).then(({ value }) => {
      if (value && value.length >= 6) {
        import('@/api/system/user').then(({ resetPassword }) => {
          return resetPassword({ id: userStore.userInfo.id, newPassword: value })
        }).then(() => {
          ElMessage.success('密码修改成功，请重新登录')
          router.push('/login')
        }).catch(() => {})
      } else {
        ElMessage.warning('密码至少6位')
      }
    }).catch(() => {})
    return
  }
  if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', { type: 'warning' }).then(() => {
      disconnectWebSocket()
      userStore.logout().then(() => router.push('/login'))
    })
  }
}
</script>

<style scoped>
.layout-shell {
  height: 100vh;
  display: flex;
  overflow: hidden;
  background: var(--pms-bg);
}

.layout-aside {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  height: 100%;
  overflow: hidden;
  color: #fff;
  background: linear-gradient(180deg, #123f3d 0%, #0c3233 100%);
  transition: width 0.25s ease;
}

.brand {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  flex-shrink: 0;
  cursor: pointer;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.brand-mark {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 8px;
  background: #6ee7b7;
  color: #0b3b3c;
  font-size: 18px;
  font-weight: 800;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.2;
}

.brand-copy strong {
  font-size: 16px;
  letter-spacing: 0.02em;
}

.brand-copy span {
  margin-top: 3px;
  color: rgba(255, 255, 255, 0.52);
  font-size: 10px;
  letter-spacing: 0.08em;
}

.menu-scrollbar {
  flex: 1;
  padding: 12px 0;
}

.side-menu {
  --el-menu-bg-color: transparent;
  --el-menu-text-color: rgba(255, 255, 255, 0.72);
  --el-menu-active-color: #ffffff;
  --el-menu-hover-bg-color: rgba(255, 255, 255, 0.08);
  width: 100%;
  padding: 0 10px;
  background: transparent;
  border-right: none;
}

.side-menu :deep(.el-menu-item),
.side-menu :deep(.el-sub-menu__title) {
  height: 46px;
  line-height: 46px;
  margin: 2px 0;
  border-radius: 8px;
  color: rgba(255, 255, 255, 0.72);
}

.side-menu :deep(.el-menu-item:hover),
.side-menu :deep(.el-sub-menu__title:hover) {
  background: rgba(255, 255, 255, 0.08);
  color: #fff;
}

.side-menu :deep(.el-menu-item.is-active) {
  background: linear-gradient(90deg, rgba(110, 231, 183, 0.2), rgba(110, 231, 183, 0.06));
  color: #fff;
  box-shadow: inset 3px 0 0 #6ee7b7;
}

.side-menu :deep(.el-sub-menu .el-menu) {
  background: rgba(0, 0, 0, 0.12);
  border-radius: 8px;
}

.side-menu :deep(.el-icon) {
  color: inherit;
}

.aside-footer {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  height: 48px;
  flex-shrink: 0;
  color: rgba(255, 255, 255, 0.5);
  font-size: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}

.layout-body {
  display: flex;
  flex-direction: column;
  flex: 1;
  min-width: 0;
  height: 100%;
}

.layout-header {
  height: 72px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
  padding: 0 24px;
  background: var(--pms-surface);
  border-bottom: 1px solid var(--pms-border);
}

.header-left {
  display: flex;
  align-items: center;
  gap: 16px;
  min-width: 0;
}

.icon-btn {
  width: 38px;
  height: 38px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 8px;
  background: var(--pms-surface-soft);
  color: var(--pms-text-muted);
  cursor: pointer;
  transition: all 0.2s ease;
}

.icon-btn:hover {
  background: var(--pms-primary-soft);
  color: var(--pms-primary);
}

.breadcrumb-home-icon {
  margin-right: 4px;
  vertical-align: -2px;
}

.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}

.header-date {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--pms-text-muted);
  font-size: 13px;
  white-space: nowrap;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 6px 10px;
  border: 1px solid transparent;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s ease;
}

.user-chip:hover {
  background: var(--pms-surface-soft);
  border-color: var(--pms-border);
}

.user-avatar {
  background: var(--pms-primary);
  color: #fff;
  font-weight: 600;
}

.user-copy {
  display: flex;
  flex-direction: column;
  min-width: 0;
  line-height: 1.2;
}

.user-copy strong {
  color: var(--pms-text);
  font-size: 14px;
}

.user-copy span {
  margin-top: 3px;
  color: var(--pms-text-muted);
  font-size: 12px;
}

.user-arrow {
  color: var(--pms-text-muted);
}

.layout-main {
  flex: 1;
  overflow-y: auto;
  padding: 22px 24px 32px;
  background: var(--pms-bg);
}

.layout-main--dashboard {
  padding-top: 0;
  padding-bottom: 16px;
}

.page-heading {
  display: flex;
  align-items: center;
  gap: 12px;
  min-height: 44px;
  margin-bottom: 18px;
}

.page-heading-icon {
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 8px;
  background: var(--pms-primary-soft);
  color: var(--pms-primary);
}

.page-heading h2 {
  color: var(--pms-text);
  font-size: 20px;
  font-weight: 700;
  line-height: 1.25;
}

.page-heading p {
  margin-top: 3px;
  color: var(--pms-text-muted);
  font-size: 12px;
}

.page-fade-enter-active,
.page-fade-leave-active {
  transition: opacity 0.18s ease, transform 0.18s ease;
}

.page-fade-enter-from {
  opacity: 0;
  transform: translateY(6px);
}

.page-fade-leave-to {
  opacity: 0;
  transform: translateY(-4px);
}

@media (max-width: 900px) {
  .header-date {
    display: none;
  }
}

@media (max-width: 768px) {
  .layout-header {
    padding: 0 14px;
  }

  .icon-btn {
    display: none;
  }

  .layout-main {
    padding: 16px 14px 24px;
  }

  .user-copy {
    display: none;
  }

  .el-breadcrumb {
    display: none;
  }
}
</style>
