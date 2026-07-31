<template>
  <div class="welcome-container">
    <div class="welcome-banner">
      <div>
        <h2 class="welcome-title">{{ greeting }}，{{ userStore.realName || userStore.userInfo.username }}</h2>
        <p class="welcome-sub">欢迎使用社区物业系统，祝您生活愉快</p>
      </div>
      <div class="welcome-date">{{ nowText }}</div>
    </div>

    <el-row :gutter="16">
      <el-col :span="12" class="panel-col">
        <el-card shadow="never" class="panel-card">
          <template #header>工作待办</template>
          <div class="todo-body">
            <div v-if="todos.length" class="todo-list">
              <div v-for="item in todos" :key="item.key" class="todo-item" @click="router.push(item.path)">
                <el-icon :size="18" color="#409eff"><component :is="item.icon" /></el-icon>
                <span class="todo-name">{{ item.name }}</span>
                <el-badge :value="item.count" :hidden="!item.count" />
              </div>
            </div>
            <el-empty v-else description="暂无待办事项" :image-size="60" />
          </div>
        </el-card>
      </el-col>
      <el-col :span="12" class="panel-col">
        <el-card shadow="never" class="panel-card">
          <template #header>最新公告</template>
          <div v-if="latest" class="notice-body" @click="router.push('/announcement')">
            <div class="notice-title">{{ latest.title }}</div>
            <div class="notice-meta">
              <el-tag size="small">{{ typeText(latest.type) }}</el-tag>
              <span>{{ latest.publishTime }}</span>
            </div>
            <div class="notice-content">{{ latest.content }}</div>
          </div>
          <el-empty v-else description="暂无已发布公告" :image-size="60" />
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAnnouncementLatest } from '@/api/announcement'

const router = useRouter()
const userStore = useUserStore()
const now = ref(new Date())
const latest = ref(null)

// TODO: 待办数量后续接入后端接口（按角色返回真实数据）
const todos = ref([
  { key: 'repairAssign', name: '待派单报修', icon: 'Tickets', path: '/repair/record', count: 0 },
  { key: 'repairConfirm', name: '待确认工单', icon: 'Finished', path: '/repair/record', count: 0 },
  { key: 'complaint', name: '待处理投诉', icon: 'ChatDotSquare', path: '/complaint', count: 0 },
  { key: 'inspection', name: '待执行巡检', icon: 'Calendar', path: '/inspection/records', count: 0 }
])

const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const nowText = computed(() => now.value.toLocaleString('zh-CN', { hour12: false }))

const typeText = (t) => ({ 1: '通知', 2: '公告', 3: '活动' }[t] || '')

const timer = setInterval(() => { now.value = new Date() }, 30000)
onUnmounted(() => clearInterval(timer))

onMounted(async () => {
  try {
    const res = await getAnnouncementLatest()
    latest.value = res.data || null
  } catch (e) { latest.value = null }
})
</script>

<style scoped>
.welcome-container { display: flex; flex-direction: column; gap: 16px; }
.welcome-banner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 48px 48px;
  background: linear-gradient(135deg, #409eff, #36cfc9);
  border-radius: 8px;
  color: #fff;
}
.welcome-title { margin: 0 0 12px; font-size: 32px; font-weight: 600; }
.welcome-sub { margin: 0; opacity: 0.9; font-size: 16px; }
.welcome-date { font-size: 15px; opacity: 0.95; }
.panel-col { display: flex; }
.panel-card { border-radius: 8px; flex: 1; height: 320px; }
.panel-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  justify-content: center;
  height: calc(100% - 53px);
  padding: 16px 24px;
}
.todo-body { width: 100%; }
.todo-list { display: flex; flex-direction: column; gap: 12px; width: 100%; }
.todo-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 14px;
  border: 1px solid #ebeef5;
  border-radius: 6px;
  cursor: pointer;
  transition: background 0.2s;
}
.todo-item:hover { background: #f5f7fa; }
.todo-name { flex: 1; font-size: 14px; color: #303133; }
.notice-body { cursor: pointer; width: 100%; }
.notice-title { font-size: 20px; font-weight: 600; color: #303133; }
.notice-meta { display: flex; align-items: center; gap: 12px; margin: 12px 0; font-size: 14px; color: #909399; }
.notice-content {
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 15px;
  color: #606266;
  line-height: 1.8;
}
</style>
