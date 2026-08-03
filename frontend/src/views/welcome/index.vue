<template>
  <div class="welcome-container">
    <section class="hero">
      <div class="hero-copy">
        <div class="hero-kicker">
          <el-icon><Sunny /></el-icon>
          <span>{{ dateLabel }}</span>
        </div>
        <h1>{{ greeting }}，{{ userName }}</h1>
        <p>欢迎使用社区物业系统，祝您工作顺利。</p>
      </div>

      <div class="hero-side">
        <div class="hero-clock">
          <strong>{{ timeText }}</strong>
          <span>{{ dateText }}</span>
        </div>
        <div class="hero-stat">
          <span>当前待办</span>
          <strong>{{ todoCount }}</strong>
        </div>
      </div>
    </section>

    <div class="welcome-grid">
      <section class="panel-card">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon><Tickets /></el-icon>
            <span>待办事项</span>
          </div>
        </div>
        <div class="panel-body">
          <div v-if="todos.length" class="todo-list">
            <div v-for="item in todos" :key="item.key" class="todo-item" @click="router.push(item.path)">
              <div class="todo-icon">
                <el-icon :size="18"><component :is="item.icon" /></el-icon>
              </div>
              <span class="todo-name">{{ item.name }}</span>
              <el-badge :value="item.count" :hidden="!item.count" class="todo-badge" />
              <el-icon class="todo-arrow"><ArrowRight /></el-icon>
            </div>
          </div>
          <el-empty v-else description="暂无待办事项" :image-size="72" />
        </div>
      </section>

      <section class="panel-card">
        <div class="panel-header">
          <div class="panel-title">
            <el-icon><BellFilled /></el-icon>
            <span>最新公告</span>
          </div>
        </div>
        <div v-if="latest" class="notice-body" @click="router.push('/announcement')">
          <div class="notice-top">
            <el-tag :type="typeTag(latest.type)" size="small">{{ typeText(latest.type) }}</el-tag>
            <span>{{ latest.publishTime }}</span>
          </div>
          <div class="notice-title">{{ latest.title }}</div>
          <div class="notice-content">{{ latest.content }}</div>
        </div>
        <div v-else class="panel-body empty-body">
          <el-empty description="暂无已发布公告" :image-size="72" />
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { getAnnouncementLatest } from '@/api/announcement'
import { getTodos } from '@/api/statistics'
import { connectWebSocket } from '@/utils/ws'

const router = useRouter()
const userStore = useUserStore()
const now = ref(new Date())
const latest = ref(null)

const todos = ref([])

const todoMeta = {
  repairAssign: { icon: 'Tickets', path: '/repair/record' },
  repairConfirm: { icon: 'Finished', path: '/repair/record' },
  repairProcessing: { icon: 'Tools', path: '/repair/record' },
  complaint: { icon: 'ChatDotSquare', path: '/complaint' },
  complaintConfirm: { icon: 'ChatDotSquare', path: '/complaint' },
  inspectionTodo: { icon: 'Calendar', path: '/inspection/records' },
  inspectionAbnormal: { icon: 'Warning', path: '/inspection/records' },
  feePending: { icon: 'Money', path: '/fee/records' },
  expenseAudit: { icon: 'List', path: '/fee/expenses' }
}

const userName = computed(() => userStore.realName || userStore.userInfo.username || '朋友')

const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const dateText = computed(() => now.value.toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: '2-digit',
  day: '2-digit'
}))

const dateLabel = computed(() => now.value.toLocaleDateString('zh-CN', {
  month: 'long',
  day: 'numeric',
  weekday: 'long'
}))

const timeText = computed(() => now.value.toLocaleTimeString('zh-CN', {
  hour: '2-digit',
  minute: '2-digit',
  second: '2-digit',
  hour12: false
}))

const todoCount = computed(() => todos.value.reduce((sum, item) => sum + (Number(item.count) || 0), 0))

const typeText = (t) => ({ 1: '通知', 2: '公告', 3: '活动' }[t] || '')
const typeTag = (t) => ({ 1: 'primary', 2: 'success', 3: 'warning' }[t] || 'primary')

const timer = setInterval(() => { now.value = new Date() }, 1000)
let refreshTimer = null
let pollTimer = null
let wsUnsubscribe = null

async function loadTodos() {
  try {
    const res = await getTodos()
    todos.value = (res.data || [])
      .map(item => ({ ...item, ...todoMeta[item.key] }))
      .filter(item => item.icon && item.path)
  } catch (e) { /* 保留旧数据 */ }
}

function scheduleRefresh() {
  clearTimeout(refreshTimer)
  refreshTimer = setTimeout(loadTodos, 300)
}

onUnmounted(() => {
  clearInterval(timer)
  clearInterval(pollTimer)
  clearTimeout(refreshTimer)
  if (wsUnsubscribe) wsUnsubscribe()
})

onMounted(async () => {
  try {
    const res = await getAnnouncementLatest()
    latest.value = res.data || null
  } catch (e) { latest.value = null }
  wsUnsubscribe = connectWebSocket(scheduleRefresh)
  loadTodos()
  pollTimer = setInterval(loadTodos, 60000)
})
</script>

<style scoped>
.welcome-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.hero {
  position: relative;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 20px;
  min-height: 180px;
  padding: 28px 32px;
  overflow: hidden;
  border-radius: 8px;
  color: #fff;
  background: linear-gradient(120deg, #0f766e 0%, #177f77 55%, #2f8d85 100%);
  box-shadow: 0 14px 34px rgba(15, 118, 110, 0.2);
}

.hero::after {
  content: '';
  position: absolute;
  right: -70px;
  top: -70px;
  width: 240px;
  height: 240px;
  border: 1px solid rgba(255, 255, 255, 0.18);
  border-radius: 8px;
  transform: rotate(45deg);
}

.hero-copy {
  position: relative;
  z-index: 1;
}

.hero-kicker {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 14px;
  color: rgba(255, 255, 255, 0.78);
  font-size: 13px;
}

.hero-kicker .el-icon {
  color: #6ee7b7;
}

.hero h1 {
  color: #fff;
  font-size: 28px;
  font-weight: 700;
  line-height: 1.25;
}

.hero p {
  margin-top: 9px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.hero-side {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: stretch;
  gap: 10px;
}

.hero-clock,
.hero-stat {
  min-width: 128px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px 18px;
  border: 1px solid rgba(255, 255, 255, 0.16);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
}

.hero-clock strong {
  color: #fff;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', monospace;
  font-size: 22px;
  letter-spacing: 0;
}

.hero-clock span,
.hero-stat span {
  margin-top: 6px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 12px;
}

.hero-stat strong {
  margin-top: 4px;
  color: #6ee7b7;
  font-size: 32px;
  line-height: 1;
}

.welcome-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.panel-card {
  display: flex;
  flex-direction: column;
  min-height: 255px;
  overflow: hidden;
  background: var(--pms-surface);
  border: 1px solid var(--pms-border);
  border-radius: 8px;
  box-shadow: var(--pms-shadow);
}

.panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  border-bottom: 1px solid var(--pms-border);
}

.panel-title {
  display: flex;
  align-items: center;
  gap: 9px;
  color: var(--pms-text);
  font-size: 15px;
  font-weight: 600;
}

.panel-title .el-icon {
  color: var(--pms-primary);
  font-size: 17px;
}

.panel-body {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 16px 18px;
}

.empty-body {
  min-height: 200px;
}

.todo-list {
  width: 100%;
  display: flex;
  flex-direction: column;
  gap: 9px;
}

.todo-item {
  display: flex;
  align-items: center;
  gap: 11px;
  padding: 10px 13px;
  border: 1px solid var(--pms-border);
  border-radius: 8px;
  background: var(--pms-surface-soft);
  cursor: pointer;
  transition: all 0.2s ease;
}

.todo-item:hover {
  border-color: #b5d6cf;
  background: #f1faf7;
  box-shadow: 0 8px 20px rgba(15, 118, 110, 0.08);
  transform: translateY(-1px);
}

.todo-icon {
  width: 34px;
  height: 34px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  border-radius: 8px;
  background: var(--pms-primary-soft);
  color: var(--pms-primary);
}

.todo-name {
  flex: 1;
  color: var(--pms-text);
  font-size: 14px;
  font-weight: 500;
}

.todo-arrow {
  color: #9db0aa;
  font-size: 14px;
}

.notice-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 16px 18px;
  cursor: pointer;
}

.notice-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  color: var(--pms-text-muted);
  font-size: 12px;
}

.notice-title {
  margin-top: 14px;
  color: var(--pms-text);
  font-size: 19px;
  font-weight: 700;
}

.notice-content {
  flex: 1;
  display: block;
  margin-top: 10px;
  overflow: hidden;
  color: #596964;
  font-size: 14px;
  line-height: 1.7;
  white-space: pre-wrap;
  word-break: break-word;
  max-height: 120px;
}

@media (max-width: 900px) {
  .hero {
    align-items: flex-start;
    flex-direction: column;
  }

  .hero-side {
    width: 100%;
  }

  .hero-clock,
  .hero-stat {
    flex: 1;
  }

  .welcome-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 560px) {
  .hero {
    padding: 20px 18px;
  }

  .hero h1 {
    font-size: 22px;
  }

  .hero-side {
    flex-direction: column;
  }
}
</style>
