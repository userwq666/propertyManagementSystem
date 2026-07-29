<template>
  <div class="dashboard-container">
    <div class="page-header">
      <h1>数据概览</h1>
      <el-button type="primary" @click="refreshData" :loading="loading">
        <el-icon><Refresh /></el-icon> 刷新数据
      </el-button>
    </div>

    <el-row :gutter="16" class="stat-cards">
      <el-col :xs="12" :sm="6" v-for="card in statCards" :key="card.key">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-content">
            <div class="stat-info">
              <p class="stat-label">{{ card.label }}</p>
              <p class="stat-value">{{ card.value }}</p>
            </div>
            <div class="stat-icon" :style="{ backgroundColor: card.color }">
              <el-icon :size="20"><component :is="card.icon" /></el-icon>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16" class="charts-row">
      <el-col :xs="24" :lg="16">
        <el-card shadow="hover">
          <template #header><span>月度收费趋势 ({{ currentYear }})</span></template>
          <div ref="feeChartRef" style="height:300px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="8">
        <el-card shadow="hover">
          <template #header><span>费用结构</span></template>
          <div ref="feeStructRef" style="height:300px" />
        </el-card>
      </el-col>
    </el-row>

    <el-row :gutter="16">
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header><span>报修类型分布</span></template>
          <div ref="repairTypeRef" style="height:280px" />
        </el-card>
      </el-col>
      <el-col :xs="24" :lg="12">
        <el-card shadow="hover">
          <template #header><span>报修概况</span></template>
          <el-descriptions :column="2" border>
            <el-descriptions-item v-for="item in repairOverviewItems" :key="item.label" :label="item.label">{{ item.value }}</el-descriptions-item>
          </el-descriptions>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount, nextTick } from "vue"
import { Refresh, House, UserFilled, Money, Tools } from "@element-plus/icons-vue"
import * as echarts from "echarts"
import { getOverview, getMonthlyFeeStatistics, getFeeByItem, getRepairOverview, getRepairByType } from "@/api/statistics/dashboard"
import { useUserStore } from "@/store/modules/user"
import { useRouter } from "vue-router"

const router = useRouter()
const userStore = useUserStore()
const loading = ref(false)
const currentYear = new Date().getFullYear()

if (!userStore.roles || userStore.roles.length === 0) {
  router.replace("/login")
}

const overview = reactive({ houseCount: 0, ownerCount: 0, feeTotal: 0, repairCount: 0 })

const statCards = computed(() => [
  { key: "house", label: "房屋总数", value: overview.houseCount || "--", icon: House, color: "#409eff" },
  { key: "owner", label: "业主总数", value: overview.ownerCount || "--", icon: UserFilled, color: "#67c23a" },
  { key: "fee", label: "收费总额(元)", value: (Number(overview.feeTotal) || 0).toLocaleString(), icon: Money, color: "#e6a23c" },
  { key: "repair", label: "报修工单", value: overview.repairCount || "--", icon: Tools, color: "#f56c6c" }
])

const repairOverviewItems = ref([
  { label: "待处理", value: "--" }, { label: "处理中", value: "--" },
  { label: "已完成", value: "--" }, { label: "总工单", value: "--" }
])

let feeChart, feeStructChart, repairTypeChart
const feeChartRef = ref(null)
const feeStructRef = ref(null)
const repairTypeRef = ref(null)

const loadOverview = async () => {
  try { const res = await getOverview(); if (res.data) Object.assign(overview, res.data) } catch {}
}

const loadFeeMonthly = async () => {
  try {
    const res = await getMonthlyFeeStatistics(currentYear)
    const data = res.data || []
    if (!feeChartRef.value) return
    if (!feeChart) feeChart = echarts.init(feeChartRef.value)
    feeChart.setOption({
      tooltip: { trigger: "axis" },
      xAxis: { type: "category", data: data.map(d => (d.month || "") + "月") },
      yAxis: { type: "value" },
      series: [{ name: "收费金额", type: "bar", data: data.map(d => d.amount || 0), itemStyle: { color: "#409eff", borderRadius: [4, 4, 0, 0] } }],
      grid: { left: "3%", right: "4%", bottom: "3%", containLabel: true }
    })
  } catch {}
}

const loadFeeStruct = async () => {
  try {
    const res = await getFeeByItem()
    const data = res.data || []
    if (!feeStructRef.value) return
    if (!feeStructChart) feeStructChart = echarts.init(feeStructRef.value)
    feeStructChart.setOption({
      tooltip: { trigger: "item", formatter: "{b}: {c}元 ({d}%)" },
      series: [{
        type: "pie", radius: ["40%", "70%"],
        data: data.map(d => ({ name: d.itemName || d.name, value: d.amount || d.value })),
        label: { formatter: "{b}\n{d}%" }
      }],
      color: ["#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#909399"]
    })
  } catch {}
}

const loadRepairType = async () => {
  try {
    const res = await getRepairByType()
    const data = res.data || []
    if (!repairTypeRef.value) return
    if (!repairTypeChart) repairTypeChart = echarts.init(repairTypeRef.value)
    repairTypeChart.setOption({
      tooltip: { trigger: "item", formatter: "{b}: {c} ({d}%)" },
      series: [{
        type: "pie", radius: "60%",
        data: data.map(d => ({ name: d.typeName || d.name, value: d.count || d.value })),
        label: { formatter: "{b}: {c}" }
      }],
      color: ["#409eff", "#67c23a", "#e6a23c", "#f56c6c", "#909399", "#ff9f43"]
    })
  } catch {}
}

const loadRepairOverview = async () => {
  try {
    const res = await getRepairOverview()
    if (res.data) {
      repairOverviewItems.value = [
        { label: "待处理", value: res.data.pending || "--" },
        { label: "处理中", value: res.data.processing || "--" },
        { label: "已完成", value: res.data.completed || "--" },
        { label: "总工单", value: res.data.total || "--" }
      ]
    }
  } catch {}
}

const refreshData = async () => {
  loading.value = true
  await nextTick()
  await Promise.all([loadOverview(), loadFeeMonthly(), loadFeeStruct(), loadRepairType(), loadRepairOverview()])
  loading.value = false
}

const handleResize = () => { feeChart?.resize(); feeStructChart?.resize(); repairTypeChart?.resize() }

onMounted(async () => { await refreshData(); window.addEventListener("resize", handleResize) })
onBeforeUnmount(() => { feeChart?.dispose(); feeStructChart?.dispose(); repairTypeChart?.dispose(); window.removeEventListener("resize", handleResize) })
</script>

<style lang="scss" scoped>
.dashboard-container { padding: 0; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
  h1 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
}
.stat-cards { margin-bottom: 16px; }
.stat-card { transition: transform .3s, box-shadow .3s;
  &:hover { transform: translateY(-2px); box-shadow: 0 8px 24px rgba(0,0,0,.1); }
}
.stat-content { display: flex; justify-content: space-between; align-items: center; padding: 8px 0; }
.stat-info {
  .stat-label { margin: 0 0 4px; font-size: 14px; color: #909399; }
  .stat-value { margin: 0; font-size: 22px; font-weight: 600; color: #303133; line-height: 1.2; }
}
.stat-icon { width: 44px; height: 44px; border-radius: 10px; display: flex; align-items: center; justify-content: center; flex-shrink: 0;
  .el-icon { color: #fff; }
}
.charts-row { margin-bottom: 16px; }
</style>
