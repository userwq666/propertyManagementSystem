<template>
  <div class="statistics-container">
    <div v-if="overviewCards.length" class="overview-grid">
      <div v-for="c in overviewCards" :key="c.label" class="stat-tile overview-tile">
        <span class="stat-label">{{ c.label }}</span>
        <strong class="stat-value">{{ c.value }}</strong>
      </div>
    </div>

    <el-card v-if="can('statistics:repair:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><Tools /></el-icon>
          <span>维修统计</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item v-for="s in repairStats" :key="s.label" :label="s.label" :value="s.value" />
      </el-row>
      <el-row :gutter="16" class="chart-row">
        <el-col :xs="24" :lg="12">
          <v-chart class="chart" :option="repairTypeOption" autoresize />
        </el-col>
        <el-col :xs="24" :lg="12">
          <v-chart class="chart" :option="repairStatusOption" autoresize />
        </el-col>
      </el-row>
    </el-card>

    <el-card v-if="can('statistics:equipment:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><Monitor /></el-icon>
          <span>设备统计</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item label="设备总数" :value="equipment.total" />
        <stat-item label="分类数" :value="equipment.categoryTotal" />
        <stat-item label="正常" :value="equipment.normal" />
        <stat-item label="故障" :value="equipment.fault" />
        <stat-item label="维修中" :value="equipment.underRepair" />
        <stat-item label="停用" :value="equipment.disabled" />
      </el-row>
      <v-chart class="chart wide-chart" :option="equipmentOption" autoresize />
    </el-card>

    <el-card v-if="can('statistics:user:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><UserFilled /></el-icon>
          <span>人员统计</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item label="用户总数" :value="userInfo.total" />
        <stat-item label="管理员" :value="userInfo.propertyAdmin" />
        <stat-item label="业主账号" :value="userInfo.owner" />
        <stat-item label="维修工" :value="userInfo.repairWorker" />
        <stat-item label="巡检员" :value="userInfo.inspector" />
        <stat-item label="财务" :value="userInfo.finance" />
        <stat-item label="业主档案" :value="userInfo.ownerCount" />
        <stat-item label="房屋数" :value="userInfo.houseCount" />
        <stat-item label="车位数" :value="userInfo.parkingCount" />
      </el-row>
      <v-chart class="chart wide-chart" :option="userRoleOption" autoresize />
    </el-card>

    <el-card v-if="can('statistics:fee:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><Money /></el-icon>
          <span>物业费收支</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item label="应收金额" :value="fee.receivable" suffix="元" />
        <stat-item label="实收金额" :value="fee.income" suffix="元" />
        <stat-item label="待缴金额" :value="fee.unpaid" suffix="元" />
        <stat-item label="支出金额" :value="fee.expense" suffix="元" />
        <stat-item label="收支结余" :value="fee.balance" suffix="元" :type="fee.balance >= 0 ? 'income' : 'loss'" />
      </el-row>
      <v-chart class="chart wide-chart" :option="feeTrendOption" autoresize />
    </el-card>

    <el-card v-if="can('statistics:complaint:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><ChatDotSquare /></el-icon>
          <span>投诉统计</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item label="投诉总数" :value="complaint.total" />
        <stat-item label="待受理" :value="complaint.pending" />
        <stat-item label="处理中" :value="complaint.processing" />
        <stat-item label="已完成" :value="complaint.done" />
        <stat-item label="平均评分" :value="complaint.avgScore" suffix="分" />
      </el-row>
      <v-chart class="chart wide-chart" :option="complaintOption" autoresize />
    </el-card>

    <el-card v-if="can('statistics:inspection:list')" shadow="never" class="module-card">
      <template #header>
        <div class="module-header">
          <el-icon><Search /></el-icon>
          <span>巡检统计</span>
        </div>
      </template>
      <el-row :gutter="16">
        <stat-item label="计划数" :value="inspection.planTotal" />
        <stat-item label="记录数" :value="inspection.recordTotal" />
        <stat-item label="正常" :value="inspection.normal" />
        <stat-item label="异常" :value="inspection.abnormal" />
        <stat-item label="完成率" :value="inspection.completionRate" suffix="%" />
      </el-row>
      <v-chart class="chart wide-chart" :option="inspectionOption" autoresize />
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import {
  getStatisticsOverview,
  getRepairSummary,
  getEquipmentSummary,
  getUserSummary,
  getFeeSummary,
  getComplaintSummary,
  getInspectionSummary
} from '@/api/statistics'
import VChart from 'vue-echarts'
import { use } from 'echarts/core'
import { CanvasRenderer } from 'echarts/renderers'
import { PieChart, LineChart } from 'echarts/charts'
import { TooltipComponent, LegendComponent, GridComponent } from 'echarts/components'

use([CanvasRenderer, PieChart, LineChart, TooltipComponent, LegendComponent, GridComponent])

const StatItem = {
  props: { label: String, value: [Number, String], suffix: String, type: String },
  template: `
    <el-col :xs="12" :sm="8" :md="6" :lg="4">
      <div class="stat-tile" :class="type">
        <span class="stat-label">{{ label }}</span>
        <strong class="stat-value">{{ value }}<span v-if="suffix" class="stat-suffix">{{ suffix }}</span></strong>
      </div>
    </el-col>`
}

const userStore = useUserStore()
const can = (perm) => userStore.hasPermission(perm)

const overview = ref({})
const repair = ref({})
const equipment = ref({})
const userInfo = ref({})
const fee = ref({})
const complaint = ref({})
const inspection = ref({})

const overviewCards = computed(() => {
  const o = overview.value
  const cards = []
  if (o.repairTotal !== undefined) cards.push({ label: '维修总数', value: o.repairTotal })
  if (o.repairPending !== undefined) cards.push({ label: '待派单报修', value: o.repairPending })
  if (o.repairDone !== undefined) cards.push({ label: '已完成报修', value: o.repairDone })
  if (o.equipmentTotal !== undefined) cards.push({ label: '设备总数', value: o.equipmentTotal })
  if (o.equipmentFault !== undefined) cards.push({ label: '故障设备', value: o.equipmentFault })
  if (o.userTotal !== undefined) cards.push({ label: '用户总数', value: o.userTotal })
  if (o.ownerTotal !== undefined) cards.push({ label: '业主数', value: o.ownerTotal })
  if (o.feeIncome !== undefined) cards.push({ label: '实收(元)', value: o.feeIncome })
  if (o.feeUnpaid !== undefined) cards.push({ label: '待缴(元)', value: o.feeUnpaid })
  if (o.feeExpense !== undefined) cards.push({ label: '支出(元)', value: o.feeExpense })
  if (o.complaintTotal !== undefined) cards.push({ label: '投诉总数', value: o.complaintTotal })
  if (o.inspectionPlanTotal !== undefined) cards.push({ label: '巡检计划', value: o.inspectionPlanTotal })
  return cards
})

const repairStats = computed(() => [
  { label: '报修总数', value: repair.value.total },
  { label: '待派单', value: repair.value.pending },
  { label: '处理中', value: repair.value.processing },
  { label: '待确认', value: repair.value.evaluate },
  { label: '已完成', value: repair.value.done },
  { label: '维修支出(元)', value: repair.value.expense }
])

const palette = ['#0f766e', '#d99a2b', '#d64545', '#2e9e5b', '#64748b', '#0ea5a4']

function pieData(entries) {
  return entries
    .filter(([, v]) => v !== undefined && v !== null && Number(v) > 0)
    .map(([name, value]) => ({ name, value: Number(value) }))
}

function pieOption(data) {
  return {
    color: palette,
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { bottom: 0, icon: 'circle', itemWidth: 8, itemHeight: 8, textStyle: { fontSize: 12 } },
    series: [{
      type: 'pie',
      radius: ['38%', '65%'],
      center: ['50%', '44%'],
      itemStyle: { borderRadius: 4, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      data
    }]
  }
}

const repairTypeOption = computed(() => pieOption(pieData(Object.entries(repair.value.typeRatio || {}))))
const repairStatusOption = computed(() => pieOption(pieData([
  ['待派单', repair.value.pending],
  ['处理中', repair.value.processing],
  ['待确认', repair.value.evaluate],
  ['已完成', repair.value.done],
  ['已取消', repair.value.cancelled]
])))
const equipmentOption = computed(() => pieOption(pieData([
  ['正常', equipment.value.normal],
  ['故障', equipment.value.fault],
  ['维修中', equipment.value.underRepair],
  ['停用', equipment.value.disabled],
  ['报废', equipment.value.scrapped]
])))
const userRoleOption = computed(() => pieOption(pieData([
  ['物业管理员', userInfo.value.propertyAdmin],
  ['业主', userInfo.value.owner],
  ['维修工', userInfo.value.repairWorker],
  ['巡检员', userInfo.value.inspector],
  ['财务', userInfo.value.finance]
])))
const complaintOption = computed(() => pieOption(pieData([
  ['待受理', complaint.value.pending],
  ['处理中', complaint.value.processing],
  ['已完成', complaint.value.done],
  ['已取消', complaint.value.cancelled]
])))
const inspectionOption = computed(() => {
  const total = Number(inspection.value.recordTotal) || 0
  const normal = Number(inspection.value.normal) || 0
  const abnormal = Number(inspection.value.abnormal) || 0
  return pieOption(pieData([['正常', normal], ['异常', abnormal], ['未巡检', Math.max(total - normal - abnormal, 0)]]))
})
const feeTrendOption = computed(() => {
  const trend = fee.value.monthlyTrend || []
  return {
    color: ['#0f766e'],
    tooltip: { trigger: 'axis' },
    grid: { left: 50, right: 20, top: 20, bottom: 30 },
    xAxis: { type: 'category', boundaryGap: false, data: trend.map(m => m.month) },
    yAxis: { type: 'value' },
    series: [{
      name: '实收',
      type: 'line',
      smooth: true,
      lineStyle: { width: 3 },
      areaStyle: { color: 'rgba(15, 118, 110, 0.12)' },
      data: trend.map(m => m.amount)
    }]
  }
})

onMounted(async () => {
  const tasks = []
  if (can('statistics:overview:list')) tasks.push(getStatisticsOverview().then(r => { overview.value = r.data || {} }).catch(() => {}))
  if (can('statistics:repair:list')) tasks.push(getRepairSummary().then(r => { repair.value = r.data || {} }).catch(() => {}))
  if (can('statistics:equipment:list')) tasks.push(getEquipmentSummary().then(r => { equipment.value = r.data || {} }).catch(() => {}))
  if (can('statistics:user:list')) tasks.push(getUserSummary().then(r => { userInfo.value = r.data || {} }).catch(() => {}))
  if (can('statistics:fee:list')) tasks.push(getFeeSummary().then(r => { fee.value = r.data || {} }).catch(() => {}))
  if (can('statistics:complaint:list')) tasks.push(getComplaintSummary().then(r => { complaint.value = r.data || {} }).catch(() => {}))
  if (can('statistics:inspection:list')) tasks.push(getInspectionSummary().then(r => { inspection.value = r.data || {} }).catch(() => {}))
  await Promise.all(tasks)
})
</script>

<style scoped>
.statistics-container {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.overview-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(160px, 1fr));
  gap: 12px;
}

.stat-tile {
  min-height: 96px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 16px 18px;
  border: 1px solid var(--pms-border);
  border-radius: 8px;
  background: var(--pms-surface);
  box-shadow: var(--pms-shadow);
  transition: all 0.2s ease;
}

.stat-tile:hover {
  border-color: #b5d6cf;
  box-shadow: 0 12px 28px rgba(15, 118, 110, 0.08);
  transform: translateY(-1px);
}

.stat-label {
  color: var(--pms-text-muted);
  font-size: 13px;
}

.stat-value {
  margin-top: 10px;
  color: var(--pms-primary);
  font-size: 24px;
  line-height: 1;
  font-weight: 700;
}

.stat-value.income {
  color: var(--el-color-success);
}

.stat-value.loss {
  color: var(--pms-danger);
}

.stat-suffix {
  margin-left: 3px;
  color: var(--pms-text-muted);
  font-size: 12px;
  font-weight: 500;
}

.overview-tile {
  min-height: 110px;
  background:
    linear-gradient(135deg, rgba(15, 118, 110, 0.05), transparent 55%),
    var(--pms-surface);
}

.module-card {
  border-radius: 8px;
}

.module-card :deep(.el-card__header) {
  padding: 14px 20px;
}

.module-header {
  display: flex;
  align-items: center;
  gap: 8px;
}

.module-header .el-icon {
  color: var(--pms-primary);
  font-size: 16px;
}

.chart-row {
  margin-top: 16px;
}

.chart {
  height: 280px;
}

.wide-chart {
  height: 300px;
  margin-top: 16px;
}

@media (max-width: 768px) {
  .overview-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .stat-tile {
    min-height: 84px;
    padding: 14px;
  }

  .chart,
  .wide-chart {
    height: 240px;
  }
}
</style>
