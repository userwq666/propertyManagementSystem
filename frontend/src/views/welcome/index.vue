<template>
  <div class="welcome-container">
    <div class="welcome-banner">
      <div>
        <h2 class="welcome-title">{{ greeting }}，{{ userStore.realName || userStore.userInfo.username }}</h2>
        <p class="welcome-sub">欢迎使用物业管理系统，祝你工作顺利</p>
      </div>
      <div class="welcome-date">{{ nowText }}</div>
    </div>

    <el-row :gutter="16">
      <el-col :span="10" class="panel-col">
        <el-card shadow="never" class="panel-card">
          <template #header>今日天气</template>
          <div v-if="weather" class="weather-body">
            <div class="weather-temp">{{ weather.temperature }}°C</div>
            <div class="weather-desc">{{ weather.desc }}</div>
            <div class="weather-meta">
              <span>{{ weather.city }}</span>
              <span>风速 {{ weather.windspeed }} km/h</span>
            </div>
            <div class="weather-meta">{{ weather.time }}</div>
          </div>
          <el-empty v-else description="天气加载失败" :image-size="60" />
        </el-card>
      </el-col>
      <el-col :span="14" class="panel-col">
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
const weather = ref(null)
const latest = ref(null)

const greeting = computed(() => {
  const hour = now.value.getHours()
  if (hour < 6) return '夜深了'
  if (hour < 12) return '早上好'
  if (hour < 18) return '下午好'
  return '晚上好'
})

const nowText = computed(() => now.value.toLocaleString('zh-CN', { hour12: false }))

const typeText = (t) => ({ 1: '通知', 2: '公告', 3: '活动' }[t] || '')
const weatherCodeText = (code) => ({
  0: '晴', 1: '大部晴朗', 2: '多云', 3: '阴',
  45: '雾', 48: '冻雾', 51: '小毛毛雨', 53: '毛毛雨', 55: '大毛毛雨',
  61: '小雨', 63: '中雨', 65: '大雨', 71: '小雪', 73: '中雪', 75: '大雪',
  80: '阵雨', 81: '强阵雨', 82: '暴雨', 95: '雷阵雨'
}[code] || '未知')

const timer = setInterval(() => { now.value = new Date() }, 30000)
onUnmounted(() => clearInterval(timer))

onMounted(async () => {
  loadWeather()
  try {
    const res = await getAnnouncementLatest()
    latest.value = res.data || null
  } catch (e) { latest.value = null }
})

async function loadWeather() {
  try {
    const res = await fetch('https://api.open-meteo.com/v1/forecast?latitude=31.2304&longitude=121.4737&current_weather=true&timezone=Asia%2FShanghai')
    const data = await res.json()
    const cw = data.current_weather
    if (cw) {
      weather.value = {
        city: '上海',
        temperature: Math.round(cw.temperature),
        desc: weatherCodeText(cw.weathercode),
        windspeed: Math.round(cw.windspeed),
        time: (cw.time || '').replace('T', ' ')
      }
    }
  } catch (e) { weather.value = null }
}
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
.panel-col { display: flex; }
.panel-card { border-radius: 8px; flex: 1; height: 240px; }
.panel-card :deep(.el-card__body) {
  display: flex;
  align-items: center;
  justify-content: center;
  height: calc(100% - 53px);
  padding: 16px 24px;
}
.weather-body { text-align: center; padding: 8px 0; }
.weather-temp { font-size: 44px; font-weight: 600; color: #409eff; }
.weather-desc { margin: 8px 0; font-size: 16px; color: #606266; }
.weather-meta { display: flex; justify-content: center; gap: 16px; margin-top: 6px; font-size: 13px; color: #909399; }
.notice-body { cursor: pointer; width: 100%; }
.notice-title { font-size: 18px; font-weight: 600; color: #303133; }
.notice-meta { display: flex; align-items: center; gap: 12px; margin: 10px 0; font-size: 13px; color: #909399; }
.notice-content {
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-size: 14px;
  color: #606266;
  line-height: 1.6;
}
</style>
