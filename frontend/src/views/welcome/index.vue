<template>
  <div class="welcome-container">
    <div class="welcome-banner">
      <div>
        <h2 class="welcome-title">{{ greeting }}，{{ userStore.realName || userStore.userInfo.username }}</h2>
        <p class="welcome-sub">欢迎使用物业管理系统，祝你工作顺利</p>
      </div>
      <div class="welcome-date">{{ nowText }}</div>
    </div>

    <el-card shadow="never" class="quick-card">
      <template #header>快捷入口</template>
      <div class="quick-grid">
        <div v-for="item in quickLinks" :key="item.path" class="quick-item" @click="router.push(item.path)">
          <el-icon :size="28"><component :is="item.icon" /></el-icon>
          <span>{{ item.title }}</span>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const now = ref(new Date())

const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const nowText = computed(() => now.value.toLocaleString('zh-CN', { hour12: false }))

const quickLinks = computed(() => [
  { title: '公告列表', path: '/announcement', icon: 'Notification', perm: 'announcement:list:list' },
  { title: '报修工单', path: '/repair/record', icon: 'Tools', perm: 'repair:record:list' },
  { title: '设备台账', path: '/equipment/equipments', icon: 'Monitor', perm: 'equipment:list:list' },
  { title: '投诉建议', path: '/complaint', icon: 'ChatLineSquare', perm: 'complaint:list:list' },
  { title: '收费管理', path: '/fee/items', icon: 'Money', perm: 'fee:item:list' },
  { title: '巡检计划', path: '/inspection/plans', icon: 'Search', perm: 'inspection:plan:list' }
].filter(item => userStore.hasPermission(item.perm)))

const timer = setInterval(() => { now.value = new Date() }, 30000)
onUnmounted(() => clearInterval(timer))
</script>

<style scoped>
.welcome-container { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 28px 32px;
  background: linear-gradient(135deg, #409eff, #36cfc9);
  border-radius: 8px;
  color: #fff;
}
.welcome-title { margin: 0 0 8px; font-size: 24px; font-weight: 600; }
.welcome-sub { margin: 0; opacity: 0.9; font-size: 14px; }
.welcome-date { font-size: 14px; opacity: 0.95; }
.stat-cards { margin-top: 4px; }
.stat-card { text-align: center; }
.stat-value { font-size: 26px; font-weight: 600; color: #303133; }
.stat-label { margin-top: 6px; font-size: 13px; color: #909399; }
.quick-card { border-radius: 8px; }
.quick-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 12px;
}
.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  padding: 20px 8px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.2s;
}
.quick-item:hover { border-color: #409eff; color: #409eff; box-shadow: 0 2px 8px rgba(64, 158, 255, 0.15); }
.quick-item span { font-size: 14px; }
</style>
