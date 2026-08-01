<template>
  <div class="statistics-container">
    <!-- 概览卡片 -->
    <el-row v-if="overviewCards.length" :gutter="16" class="overview-row">
      <el-col v-for="c in overviewCards" :key="c.label" :span="4">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-value">{{ c.value }}</div>
          <div class="stat-label">{{ c.label }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 维修统计 -->
    <el-card v-if="can('statistics:repair:list')" shadow="never" class="module-card">
      <template #header>维修统计</template>
      <el-row :gutter="16">
        <stat-item v-for="s in repairStats" :key="s.label" :label="s.label" :value="s.value" />
      </el-row>
      <div v-if="repair.typeRatio" class="ratio-box">
        <el-tag v-for="(v, k) in repair.typeRatio" :key="k" class="ratio-tag">{{ k }}：{{ v }} 次</el-tag>
      </div>
    </el-card>

    <!-- 设备统计 -->
    <el-card v-if="can('statistics:equipment:list')" shadow="never" class="module-card">
      <template #header>设备统计</template>
      <el-row :gutter="16">
        <stat-item label="设备总数" :value="equipment.total" />
        <stat-item label="分类数" :value="equipment.categoryTotal" />
        <stat-item label="正常" :value="equipment.normal" />
        <stat-item label="故障" :value="equipment.fault" />
        <stat-item label="维修中" :value="equipment.underRepair" />
        <stat-item label="停用" :value="equipment.disabled" />
      </el-row>
    </el-card>

    <!-- 人员统计 -->
    <el-card v-if="can('statistics:user:list')" shadow="never" class="module-card">
      <template #header>人员统计</template>
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
    </el-card>

    <!-- 费用统计 -->
    <el-card v-if="can('statistics:fee:list')" shadow="never" class="module-card">
      <template #header>物业费收支</template>
      <el-row :gutter="16">
        <stat-item label="应收金额" :value="fee.receivable" suffix="元" />
        <stat-item label="实收金额" :value="fee.income" suffix="元" />
        <stat-item label="待缴金额" :value="fee.unpaid" suffix="元" />
        <stat-item label="支出金额" :value="fee.expense" suffix="元" />
        <stat-item label="收支结余" :value="fee.balance" suffix="元" :type="fee.balance >= 0 ? 'income' : 'loss'" />
      </el-row>
      <div v-if="fee.monthlyTrend" class="trend-box">
        <div v-for="m in fee.monthlyTrend" :key="m.month" class="trend-item">
          <span class="trend-month">{{ m.month }}</span>
          <div class="trend-bar-wrap">
            <div class="trend-bar" :style="{ width: trendWidth(m.amount) }"></div>
          </div>
          <span class="trend-amount">{{ m.amount }}</span>
        </div>
      </div>
    </el-card>

    <!-- 投诉统计 -->
    <el-card v-if="can('statistics:complaint:list')" shadow="never" class="module-card">
      <template #header>投诉统计</template>
      <el-row :gutter="16">
        <stat-item label="投诉总数" :value="complaint.total" />
        <stat-item label="待受理" :value="complaint.pending" />
        <stat-item label="处理中" :value="complaint.processing" />
        <stat-item label="已完成" :value="complaint.done" />
        <stat-item label="平均评分" :value="complaint.avgScore" suffix="分" />
      </el-row>
    </el-card>

    <!-- 巡检统计 -->
    <el-card v-if="can('statistics:inspection:list')" shadow="never" class="module-card">
      <template #header>巡检统计</template>
      <el-row :gutter="16">
        <stat-item label="计划数" :value="inspection.planTotal" />
        <stat-item label="记录数" :value="inspection.recordTotal" />
        <stat-item label="正常" :value="inspection.normal" />
        <stat-item label="异常" :value="inspection.abnormal" />
        <stat-item label="完成率" :value="inspection.completionRate" suffix="%" />
      </el-row>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { getStatisticsOverview, getRepairSummary, getEquipmentSummary, getUserSummary, getFeeSummary, getComplaintSummary, getInspectionSummary } from '@/api/statistics'

const StatItem = {
  props: { label: String, value: [Number, String], suffix: String, type: String },
  template: `
    <el-col :span="4">
      <el-card shadow="hover" class="stat-card">
        <div class="stat-value" :class="type">{{ value }}<span v-if="suffix" class="stat-suffix">{{ suffix }}</span></div>
        <div class="stat-label">{{ label }}</div>
      </el-card>
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

const maxTrendAmount = computed(() => Math.max(...(fee.value.monthlyTrend || []).map(m => Number(m.amount) || 0), 1))
function trendWidth(amount) {
  return Math.max(2, Math.round((Number(amount) || 0) / maxTrendAmount.value * 100)) + '%'
}

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
.statistics-container { display: flex; flex-direction: column; gap: 16px; }
.overview-row { margin-bottom: 0; }
.stat-card { text-align: center; }
.stat-value { font-size: 22px; font-weight: 600; color: #409eff; }
.stat-value.income { color: #67c23a; }
.stat-value.loss { color: #f56c6c; }
.stat-suffix { font-size: 12px; color: #909399; margin-left: 2px; }
.stat-label { margin-top: 6px; font-size: 13px; color: #909399; }
.module-card { border-radius: 8px; }
.module-card :deep(.el-card__header) { font-weight: 600; }
.ratio-box { margin-top: 12px; display: flex; flex-wrap: wrap; gap: 8px; }
.ratio-tag { }
.trend-box { margin-top: 16px; display: flex; flex-direction: column; gap: 8px; }
.trend-item { display: flex; align-items: center; gap: 10px; font-size: 13px; }
.trend-month { width: 70px; color: #909399; }
.trend-bar-wrap { flex: 1; background: #f0f2f5; border-radius: 4px; height: 16px; }
.trend-bar { height: 16px; background: #67c23a; border-radius: 4px; min-width: 2px; }
.trend-amount { width: 80px; text-align: right; color: #606266; }
</style>
