<template>
  <el-container class="layout-container">
    <el-aside :width="isCollapse ? '64px' : '220px'" class="layout-aside">
      <div class="logo" @click="$router.push('/')">
        <img src="/favicon.svg" alt="" />
        <span v-show="!isCollapse">物业管理系统</span>
      </div>
      <el-menu
        :default-active="activeMenu"
        :collapse="isCollapse"
        :collapse-transition="false"
        background-color="#304156"
        text-color="#bfcbd9"
        active-text-color="#409EFF"
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
    </el-aside>

    <el-container>
      <el-header class="layout-header">
        <div class="header-left">
          <el-icon class="collapse-btn" @click="isCollapse = !isCollapse">
            <Fold v-if="!isCollapse" /><Expand v-else />
          </el-icon>
          <el-breadcrumb separator="/">
            <el-breadcrumb-item :to="{ path: '/' }">首页</el-breadcrumb-item>
            <el-breadcrumb-item v-for="item in breadcrumbs" :key="item.path">{{ item.meta.title }}</el-breadcrumb-item>
          </el-breadcrumb>
        </div>
        <div class="header-right">
          <el-dropdown @command="handleCommand">
            <span class="user-info">
              <el-avatar :size="32" :src="userStore.avatar" />
              <span class="username">{{ userStore.realName }}</span>
              <el-icon><ArrowDown /></el-icon>
            </span>
            <template #dropdown>
              <el-dropdown-menu>
                <el-dropdown-item command="profile">个人信息</el-dropdown-item>
                <el-dropdown-item command="password">修改密码</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </template>
          </el-dropdown>
        </div>
      </el-header>

      <el-main class="layout-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUserMenus } from '@/api/system/menu'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()
const isCollapse = ref(false)
const userMenus = ref([])

// 确保用户权限已加载
onMounted(async () => {
  if (!userStore.permissions || userStore.permissions.length === 0) {
    try { await userStore.getUserInfo() } catch (e) {}
  }
  try {
    const res = await getUserMenus()
    userMenus.value = res.data || []
  } catch (e) { userMenus.value = [] }
})

const activeMenu = computed(() => route.path)

const breadcrumbs = computed(() => route.matched.filter(r => r.meta.title && r.path !== '/'))

function handleCommand(command) {
  if (command === 'profile') {
    ElMessage.info('个人信息功能开发中，请稍候')
    return
  }
  if (command === 'password') {
    ElMessageBox.prompt('请输入新密码', '修改密码', { inputType: 'password', confirmButtonText: '确定', cancelButtonText: '取消' }).then(({ value }) => {
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
      userStore.logout().then(() => router.push('/login'))
    })
  }
}
</script>

<style scoped>
.layout-container { height: 100vh; }
.layout-aside { background-color: #304156; overflow-x: hidden; transition: width 0.3s; }
.logo { height: 60px; display: flex; align-items: center; justify-content: center; color: #fff; font-size: 18px; font-weight: bold; cursor: pointer; border-bottom: 1px solid rgba(255,255,255,0.1); }
.logo img { width: 32px; height: 32px; margin-right: 8px; }
.layout-header { display: flex; align-items: center; justify-content: space-between; background: #fff; border-bottom: 1px solid #e6e6e6; height: 60px; padding: 0 20px; }
.header-left { display: flex; align-items: center; gap: 16px; }
.collapse-btn { font-size: 20px; cursor: pointer; }
.header-right { display: flex; align-items: center; }
.user-info { display: flex; align-items: center; gap: 8px; cursor: pointer; }
.username { font-size: 14px; }
.layout-main { background: #f0f2f5; padding: 20px; overflow-y: auto; }
</style>
