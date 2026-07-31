<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="巡检计划">
        <el-select v-model="searchForm.planId" placeholder="请选择计划" filterable style="width:220px" @change="loadPlanDetail">
          <el-option v-for="p in plans.filter(i => i.id != null)" :key="p.id" :label="p.planName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="fetchData">刷新</el-button>
      </el-form-item>
    </el-form>

    <el-card v-if="currentPlan" shadow="never" class="plan-info">
      <div class="plan-info-row">
        <span><b>{{ currentPlan.planName }}</b></span>
        <el-tag size="small" :type="isFinished ? 'success' : 'primary'">{{ isFinished ? '已完成' : '进行中' }}</el-tag>
      </div>
      <div class="plan-info-row">
        <span>开始：{{ currentPlan.startDate || '-' }}</span>
        <span>结束：{{ currentPlan.endDate || '-' }}</span>
        <span>频率：{{ freqText(currentPlan.frequencyType) }}</span>
        <span>巡检员：{{ (currentPlan.inspectorNames || []).join('、') || '-' }}</span>
        <span>完成度：{{ finishedCount }}/{{ totalCells }}</span>
      </div>
    </el-card>

    <div class="table-container" v-if="currentPlan">
      <el-table :data="matrixRows" border stripe v-loading="loading" row-key="equipmentId">
        <el-table-column prop="equipmentName" label="设备" min-width="150" fixed />
        <el-table-column v-for="d in dates" :key="d" :label="d.slice(5)" min-width="96">
          <template #default="{ row }">
            <div class="matrix-cell" :class="cellClass(row, d)" @click="openCell(row, d)">
              {{ cellText(row, d) }}
            </div>
          </template>
        </el-table-column>
      </el-table>
      <el-empty v-if="dates.length === 0" description="该计划暂无巡检日期" />
    </div>
    <el-empty v-else description="请选择巡检计划" />

    <!-- 打卡 -->
    <el-dialog title="巡检打卡" v-model="taskDialogVisible" width="500px">
      <template v-if="currentCell">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="设备">{{ currentCell.equipmentName }}</el-descriptions-item>
          <el-descriptions-item label="巡检日期">{{ currentCell.date }}</el-descriptions-item>
          <el-descriptions-item v-if="currentCell.record" label="巡检人">{{ currentCell.record.inspectorName || '-' }}</el-descriptions-item>
          <el-descriptions-item v-if="currentCell.record" label="填写人">{{ currentCell.record.fillerName || '-' }}</el-descriptions-item>
        </el-descriptions>
        <el-form :model="fillForm" label-width="90px" style="margin-top:16px">
          <el-form-item label="巡检结果">
            <el-radio-group v-model="fillForm.status">
              <el-radio :value="1">正常</el-radio>
              <el-radio :value="2">异常</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item v-if="fillForm.status === 2" label="异常描述">
            <el-input v-model="fillForm.abnormalDesc" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="备注">
            <el-input v-model="fillForm.remark" type="textarea" :rows="2" />
          </el-form-item>
        </el-form>
      </template>
      <template #footer>
        <el-button @click="taskDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitCell">保存打卡</el-button>
        <el-button v-if="currentCell && currentCell.record && currentCell.record.status === 2 && !currentCell.record.repairRecordId" type="danger" @click="handleRepair">生成报修单</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addRecord, updateRecord, getRecordPage, createRecordRepair } from '@/api/inspection/record'
import { getPlanPage } from '@/api/inspection/plan'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const plans = ref([])
const records = ref([])
const searchForm = reactive({ planId: '' })
const currentPlan = ref(null)
const currentCell = ref(null)
const taskDialogVisible = ref(false)
const fillForm = reactive({ status: 1, abnormalDesc: '', remark: '' })
const userStore = useUserStore()

const freqText = (f) => ({ 1:'每天', 2:'每周', 3:'每月', 4:'每季度', 5:'每半年', 6:'每年', 7:'一次性' }[f] || '')

const dates = computed(() => currentPlan.value ? buildPlanDates(currentPlan.value) : [])
const matrixRows = computed(() => {
  if (!currentPlan.value) return []
  return (currentPlan.value.equipmentIds || []).map(id => ({
    equipmentId: id,
    equipmentName: (currentPlan.value.equipmentNames || [])[currentPlan.value.equipmentIds.indexOf(id)] || '设备' + id
  }))
})
const cellOf = (row, date) => records.value.find(r => r.planId === currentPlan.value.id && r.equipmentId === row.equipmentId && (r.inspectionTime || '').startsWith(date))
const totalCells = computed(() => matrixRows.value.length * dates.value.length)
const finishedCount = computed(() => matrixRows.value.reduce((sum, row) => sum + dates.value.filter(d => cellOf(row, d)).length, 0))
const isFinished = computed(() => totalCells.value > 0 && finishedCount.value >= totalCells.value)

const cellText = (row, date) => {
  const r = cellOf(row, date)
  if (!r) return '待巡检'
  return { 1: '正常', 2: '异常', 3: '未巡检' }[r.status] || ''
}
const cellClass = (row, date) => {
  const r = cellOf(row, date)
  if (!r) return 'cell-empty'
  return r.status === 2 ? 'cell-abnormal' : r.status === 1 ? 'cell-normal' : 'cell-missed'
}

onMounted(async () => {
  try {
    const p = await getPlanPage({ pageNum: 1, pageSize: 200 }, { silent: true })
    plans.value = (p.data.records || []).filter(x => x.status === 1)
    if (plans.value.length > 0) {
      searchForm.planId = plans.value[0].id
      loadPlanDetail(plans.value[0].id)
    }
  } catch (e) { /* 忽略 */ }
})

async function loadPlanDetail(id) {
  currentPlan.value = plans.value.find(p => p.id === id) || null
  records.value = []
  if (!currentPlan.value) return
  loading.value = true
  try {
    const res = await getRecordPage({ pageNum: 1, pageSize: 500, planId: id })
    records.value = res.data.records
  } finally { loading.value = false }
}

async function fetchData() {
  if (searchForm.planId) loadPlanDetail(searchForm.planId)
}

function buildPlanDates(plan) {
  const start = plan.startDate ? new Date(plan.startDate + 'T00:00:00') : new Date()
  const end = plan.endDate ? new Date(plan.endDate + 'T00:00:00') : new Date()
  if (end < start) return []
  const freq = plan.frequencyType || 1
  const value = (plan.frequencyValue || '').toString()
  const dates = []
  for (let d = new Date(start); d <= end; d.setDate(d.getDate() + 1)) {
    if (dates.length >= 90) break
    const y = d.getFullYear(), m = String(d.getMonth() + 1).padStart(2, '0'), day = String(d.getDate()).padStart(2, '0')
    const fmt = `${y}-${m}-${day}`
    if (freq === 1) { dates.push(fmt); continue }
    if (freq === 2) {
      const week = d.getDay() === 0 ? 7 : d.getDay()
      if (value.split(',').map(Number).includes(week)) dates.push(fmt)
      continue
    }
    if (freq === 3) {
      if (Number(value) === d.getDate()) dates.push(fmt)
      continue
    }
    dates.push(fmt)
  }
  return dates
}

function openCell(row, date) {
  const record = cellOf(row, date)
  currentCell.value = { equipmentId: row.equipmentId, equipmentName: row.equipmentName, date, record }
  fillForm.status = record ? (record.status === 2 ? 2 : 1) : 1
  fillForm.abnormalDesc = record?.abnormalDesc || ''
  fillForm.remark = record?.remark || ''
  taskDialogVisible.value = true
}

async function submitCell() {
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
    inspectionTime: currentCell.value.date + 'T00:00:00',
    status: fillForm.status,
    abnormalDesc: fillForm.abnormalDesc,
    remark: fillForm.remark
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
.plan-info { margin-bottom: 16px; border-radius: 8px; }
.plan-info-row { display: flex; align-items: center; gap: 20px; margin: 6px 0; font-size: 14px; color: #606266; }
.matrix-cell { min-height: 44px; display: flex; align-items: center; justify-content: center; cursor: pointer; border-radius: 4px; font-size: 13px; }
.cell-empty { color: #909399; background: #f5f7fa; }
.cell-normal { color: #67c23a; background: #f0f9eb; }
.cell-abnormal { color: #f56c6c; background: #fef0f0; }
.cell-missed { color: #e6a23c; background: #fdf6ec; }
</style>
