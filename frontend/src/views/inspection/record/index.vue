<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="巡检计划">
        <el-select v-model="searchForm.planId" placeholder="请选择计划" filterable @change="loadPlanDetail">
          <el-option v-for="p in plans.filter(i => i.id != null)" :key="p.id" :label="p.planName" :value="p.id" />
        </el-select>
      </el-form-item>
    </el-form>

    <div class="table-container" v-if="currentPlan">
      <div class="toolbar">
        <div class="toolbar-left"></div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="matrixRows" border stripe v-loading="loading" row-key="equipmentId">
        <el-table-column min-width="200" fixed>
          <template #header>
            <div class="plan-header">
              <div class="plan-header-name">{{ currentPlan.planName }}</div>
              <div class="plan-header-meta">
                <span>{{ currentPlan.startDate || '-' }} ~ {{ currentPlan.endDate || '-' }}</span>
                <span>{{ freqText(currentPlan.frequencyType) }}</span>
                <span>巡检员：{{ (currentPlan.inspectorNames || []).join('、') || '-' }}</span>
                <span>完成度：{{ finishedCount }}/{{ totalCells }}</span>
                <el-tag size="small" :type="isFinished ? 'success' : 'primary'">{{ isFinished ? '已完成' : '进行中' }}</el-tag>
              </div>
            </div>
          </template>
          <template #default="{ row }">
            <b>{{ row.equipmentName }}</b>
            <div><el-button link type="primary" size="small" @click="showPlanDetail">巡检计划详情</el-button></div>
          </template>
        </el-table-column>
        <el-table-column v-for="p in periods" :key="p.key" :label="p.label" min-width="96" align="center">
          <template #default="{ row }">
            <div class="matrix-cell" :class="cellClass(row, p)" @click="openCell(row, p)">
              <div>
                <div>{{ cellText(row, p) }}</div>
                <div v-if="cellOf(row, p) && cellOf(row, p).updateTime" class="cell-time">{{ timeShort(cellOf(row, p).updateTime) }}</div>
              </div>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>
    <el-empty v-else description="请选择巡检计划" />

    <!-- 打卡 -->
    <el-dialog title="巡检打卡" v-model="taskDialogVisible" width="500px">
      <template v-if="currentCell">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备">{{ currentCell.equipmentName }}</el-descriptions-item>
          <el-descriptions-item label="巡检周期">{{ currentCell.period.label }}</el-descriptions-item>
          <el-descriptions-item v-if="currentCell.record" label="巡检人">{{ currentCell.record.inspectorName || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="currentCell.record" label="填写人">{{ currentCell.record.fillerName || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-alert v-if="currentCell.record && !isManager" type="info" :closable="false" show-icon title="该周期已打卡，不能重复修改" style="margin-top:16px" />
        <el-alert v-if="currentCell.record && isManager" type="warning" :closable="false" show-icon title="高危操作：修改已打卡记录必须填写修改原因并留痕" style="margin-top:16px" />
        <el-form v-if="!currentCell.record || isManager" :model="fillForm" label-width="90px" style="margin-top:16px">
          <el-form-item label="巡检结果">
            <el-radio-group v-model="fillForm.status">
              <el-radio :value="1">正常</el-radio>
              <el-radio :value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="currentCell.record && isManager" label="修改原因" required>
            <el-input v-model="fillForm.reason" type="textarea" :rows="2" placeholder="管理员修改必须填写修改原因" />
          </el-form-item>
          <el-form-item v-if="fillForm.status === 2" label="异常描述">
            <el-input v-model="fillForm.abnormalDesc" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="fillForm.remark" type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
        <div v-if="currentCell.record && logs.length" style="margin-top:16px">
          <div class="log-title">修改记录</div>
          <el-table :data="logs" size="small" border>
            <el-table-column prop="createTime" label="时间" width="160" />
            <el-table-column prop="operatorName" label="操作人" width="90" />
            <el-table-column label="变更" width="110">
              <template #default="{ row }">{{ resultText(row.beforeStatus) }} → {{ resultText(row.afterStatus) }}</template>
            </el-table-column>
            <el-table-column prop="reason" label="原因" show-overflow-tooltip />
          </el-table>
        </div>
      </template>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button v-if="!currentCell.record || isManager" type="primary" @click="submitCell">{{ currentCell.record ? '保存修改' : '保存打卡' }}</el-button>
        <el-button v-if="currentCell && currentCell.record && currentCell.record.status === 2 && !currentCell.record.repairRecordId" type="danger" @click="handleRepair" v-permission="'inspection:record:edit'">生成报修单</el-button>
      </template>
    </el-dialog>

    <!-- 计划详情 -->
    <el-dialog title="巡检计划详情" v-model="planDetailVisible" width="600px">
      <el-descriptions :column="2" border v-if="currentPlan">
        <el-descriptions-item label="计划名称" :span="2">{{ currentPlan.planName }}</el-descriptions-item>
        <el-descriptions-item label="开始日期">{{ currentPlan.startDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="结束日期">{{ currentPlan.endDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="频率">{{ freqText(currentPlan.frequencyType) }}</el-descriptions-item>
        <el-descriptions-item label="巡检员">{{ (currentPlan.inspectorNames || []).join('、') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备" :span="2">{{ (currentPlan.equipmentNames || []).join('、') || '-' }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ currentPlan.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addRecord, updateRecord, getRecordPage, createRecordRepair, getRecordLogs } from '@/api/inspection/record'
import { getPlanPage } from '@/api/inspection/plan'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const plans = ref([])
const records = ref([])
const searchForm = reactive({ planId: '' })
const currentPlan = ref(null)
const currentCell = ref(null)
const taskDialogVisible = ref(false)
const planDetailVisible = ref(false)
const logs = ref([])
const fillForm = reactive({ status: 1, abnormalDesc: '', remark: '', reason: '' })
const userStore = useUserStore()

const freqText = (f) => ({ 1:'每天', 2:'每周', 3:'每月', 4:'每季度', 5:'每半年', 6:'每年', 7:'一次性' }[f] || '')
const isManager = computed(() => {
  const roles = userStore.roles || userStore.userInfo.roles || []
  return roles.includes('admin') || roles.includes('property_admin')
    || userStore.userInfo.roleName === '超级管理员' || userStore.userInfo.roleName === '物业管理员'
})
const resultText = (s) => ({ 1: '正常', 2: '异常', 3: '未巡检' }[s] || '-')

const periods = computed(() => currentPlan.value ? buildPeriods(currentPlan.value) : [])
const matrixRows = computed(() => {
  if (!currentPlan.value) return []
  const ids = currentPlan.value.equipmentIds || []
  const names = currentPlan.value.equipmentNames || []
  return ids.map((id, i) => ({ equipmentId: id, equipmentName: names[i] || '设备' + id }))
})
const totalCells = computed(() => matrixRows.value.length * periods.value.length)
const finishedCount = computed(() => matrixRows.value.reduce((sum, row) => sum + periods.value.filter(p => cellOf(row, p)).length, 0))
const isFinished = computed(() => totalCells.value > 0 && finishedCount.value >= totalCells.value)

const cellOf = (row, period) => records.value.find(r => r.planId === currentPlan.value.id && r.equipmentId === row.equipmentId && periodKeyOf(r) === period.key)
const cellText = (row, period) => {
  const r = cellOf(row, period)
  if (!r) return '待巡检'
  return { 1: '正常', 2: '异常', 3: '未巡检' }[r.status] || ''
}
const timeShort = (t) => (t || '').slice(5, 16).replace('T', ' ')
const cellClass = (row, period) => {
  const r = cellOf(row, period)
  if (!r) return 'cell-empty'
  return r.status === 2 ? 'cell-abnormal' : r.status === 1 ? 'cell-normal' : 'cell-missed'
}

onMounted(async () => {
  try {
    const p = await getPlanPage({ pageNum: 1, pageSize: 200 }, { silent: true })
    plans.value = (p.data.records || []).filter(x => x.status === 1)
  } catch (e) { /* 忽略 */ }
})

async function loadPlanDetail(id) {
  currentPlan.value = plans.value.find(p => p.id === id) || null
  records.value = []
  if (!currentPlan.value) return
  loading.value = true
  try {
    const res = await getRecordPage({ pageNum: 1, pageSize: 1000, planId: id })
    records.value = res.data.records
  } finally { loading.value = false }
}

async function fetchData() {
  if (searchForm.planId) loadPlanDetail(searchForm.planId)
}

function buildPeriods(plan) {
  const freq = plan.frequencyType || 1
  if (freq === 3) return monthPeriods(plan.startDate, plan.endDate)
  if (freq === 4) return quarterPeriods(plan.startDate, plan.endDate)
  if (freq === 5) return halfYearPeriods(plan.startDate, plan.endDate)
  if (freq === 6) return yearPeriods(plan.startDate, plan.endDate)
  if (freq === 2) return weekPeriods(plan.startDate, plan.endDate, 52)
  return dayPeriods(plan.startDate, plan.endDate, 90)
}

function dayPeriods(start, end, max) {
  const list = []
  const s = start ? new Date(start + 'T00:00:00') : new Date()
  const e = end ? new Date(end + 'T00:00:00') : new Date()
  for (let d = new Date(s); d <= e && list.length < max; d.setDate(d.getDate() + 1)) {
    const key = fmtDate(d)
    list.push({ key, label: key.slice(5), representative: key })
  }
  return list
}

function weekPeriods(start, end, max) {
  const list = []
  const s = start ? new Date(start + 'T00:00:00') : new Date()
  const e = end ? new Date(end + 'T00:00:00') : new Date()
  for (let d = new Date(s); d <= e && list.length < max; d.setDate(d.getDate() + 7)) {
    const key = fmtDate(d)
    const monday = new Date(d)
    list.push({ key, label: key.slice(5) + '起', representative: key })
  }
  return list
}

function monthPeriods(start, end) {
  const list = []
  const s = start ? new Date(start.slice(0, 7) + '-01T00:00:00') : new Date()
  const e = end ? new Date(end.slice(0, 7) + '-01T00:00:00') : new Date()
  for (let d = new Date(s); d <= e && list.length < 60; d.setMonth(d.getMonth() + 1)) {
    const key = `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}`
    list.push({ key, label: key, representative: key + '-01' })
  }
  return list
}

function quarterPeriods(start, end) {
  const list = []
  const s = start ? new Date(start.slice(0, 7) + '-01T00:00:00') : new Date()
  const e = end ? new Date(end.slice(0, 7) + '-01T00:00:00') : new Date()
  const sTotal = s.getFullYear() * 4 + Math.floor(s.getMonth() / 3)
  const eTotal = e.getFullYear() * 4 + Math.floor(e.getMonth() / 3)
  for (let total = sTotal; total <= eTotal && list.length < 40; total++) {
    const year = Math.floor(total / 4)
    const quarter = total % 4
    const month = quarter * 3 + 1
    const key = `${year}-Q${quarter + 1}`
    list.push({ key, label: key, representative: `${year}-${String(month).padStart(2, '0')}-01` })
  }
  return list
}

function halfYearPeriods(start, end) {
  const list = []
  const s = start ? new Date(start.slice(0, 7) + '-01T00:00:00') : new Date()
  const e = end ? new Date(end.slice(0, 7) + '-01T00:00:00') : new Date()
  const sTotal = s.getFullYear() * 2 + (s.getMonth() < 6 ? 0 : 1)
  const eTotal = e.getFullYear() * 2 + (e.getMonth() < 6 ? 0 : 1)
  for (let total = sTotal; total <= eTotal && list.length < 30; total++) {
    const year = Math.floor(total / 2)
    const half = total % 2
    const month = half === 0 ? 1 : 7
    const key = `${year}-H${half + 1}`
    list.push({ key, label: key, representative: `${year}-${String(month).padStart(2, '0')}-01` })
  }
  return list
}

function yearPeriods(start, end) {
  const list = []
  const s = start ? Number(start.slice(0, 4)) : new Date().getFullYear()
  const e = end ? Number(end.slice(0, 4)) : new Date().getFullYear()
  for (let y = s; y <= e && list.length < 30; y++) {
    list.push({ key: String(y), label: String(y), representative: `${y}-01-01` })
  }
  return list
}

function periodKeyOf(record) {
  const t = record.inspectionTime || ''
  const freq = currentPlan.value.frequencyType
  if (freq === 3) return t.slice(0, 7)
  if (freq === 4) {
    const m = Number(t.slice(5, 7))
    return t.slice(0, 4) + '-Q' + (Math.floor((m - 1) / 3) + 1)
  }
  if (freq === 5) return t.slice(0, 4) + '-H' + (Number(t.slice(5, 7)) <= 6 ? 1 : 2)
  if (freq === 6) return t.slice(0, 4)
  if (freq === 2) return t.slice(0, 10)
  return t.slice(0, 10)
}

function fmtDate(d) {
  return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`
}

function openCell(row, period) {
  const record = cellOf(row, period)
  currentCell.value = { equipmentId: row.equipmentId, equipmentName: row.equipmentName, period, record }
  fillForm.status = record ? (record.status === 2 ? 2 : 1) : 1
  fillForm.abnormalDesc = record?.abnormalDesc || ''
  fillForm.remark = record?.remark || ''
  fillForm.reason = ''
  logs.value = []
  if (record) {
    getRecordLogs(record.id).then(res => { logs.value = res.data || [] }).catch(() => {})
  }
  taskDialogVisible.value = true
}

function showPlanDetail() {
  planDetailVisible.value = true
}

async function submitCell() {
  if (currentCell.value.record && isManager.value && !fillForm.reason.trim()) {
    ElMessage.warning('管理员修改必须填写修改原因')
    return
  }
  if (fillForm.status === 2 && !fillForm.abnormalDesc) {
    ElMessage.warning('异常必须填写异常描述')
    return
  }
  const plan = currentPlan.value
  const inspectorId = plan.inspectorIds && plan.inspectorIds.length > 0 ? plan.inspectorIds[0] : (userStore.userInfo.id || userStore.userInfo.userId)
  const payload = {
    planId: plan.id,
    equipmentId: currentCell.value.equipmentId,
    inspectorUserId: inspectorId,
    inspectionTime: currentCell.value.period.representative + 'T00:00:00',
    status: fillForm.status,
    abnormalDesc: fillForm.abnormalDesc,
    remark: fillForm.remark,
    reason: fillForm.reason
  }
  try {
    if (currentCell.value.record) {
      await updateRecord({ id: currentCell.value.record.id, ...payload })
    } else {
      await addRecord(payload)
    }
    ElMessage.success('打卡成功')
    taskDialogVisible.value = false
    loadPlanDetail(plan.id)
  } catch (e) { /* handled */ }
}

async function handleRepair() {
  try {
    const res = await createRecordRepair(currentCell.value.record.id)
    ElMessage.success('已生成报修单，ID:' + res.data)
    taskDialogVisible.value = false
    loadPlanDetail(currentPlan.value.id)
  } catch (e) { /* handled */ }
}
</script>

<style scoped>
.plan-header-name { font-weight: 600; font-size: 15px; color: #303133; margin-bottom: 8px; }
.plan-header-meta { display: flex; flex-direction: column; gap: 4px; font-size: 13px; color: #909399; }
.matrix-cell { min-height: 44px; display: flex; align-items: center; justify-content: center; cursor: pointer; border-radius: 4px; font-size: 13px; }
.cell-time { margin-top: 2px; font-size: 11px; opacity: 0.75; }
.cell-empty { color: #909399; background: #f5f7fa; }
.cell-normal { color: #67c23a; background: #f0f9eb; }
.cell-abnormal { color: #f56c6c; background: #fef0f0; }
.cell-missed { color: #e6a23c; background: #fdf6ec; }
.log-title { font-weight: 600; font-size: 14px; color: #303133; margin-bottom: 8px; }
</style>
