<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="巡检计划">
        <el-select v-model="searchForm.planId" placeholder="全部" clearable filterable style="width:180px">
          <el-option v-for="p in plans.filter(i => i.id != null)" :key="p.id" :label="p.planName" :value="p.id" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'inspection:record:add'">新增记录</el-button>
          <span class="legend"><i class="dot dot-pending"></i>待接单 <i class="dot dot-accepted"></i>已接单 <i class="dot dot-normal"></i>正常 <i class="dot dot-abnormal"></i>异常 <i class="dot dot-missed"></i>未巡检</span>
        </div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>

      <el-table :data="matrixRows" border stripe v-loading="loading" row-key="equipmentId">
        <el-table-column prop="equipmentName" label="设备" min-width="150" fixed />
        <el-table-column v-for="d in dates" :key="d" :label="d" min-width="120">
          <template #default="{ row }">
            <div class="matrix-cell" :class="cellClass(row, d)" @click="openCell(row, d)">
              <template v-if="cellOf(row, d)">
                {{ taskText(cellOf(row, d)) }}
              </template>
              <span v-else>-</span>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <!-- 任务操作 -->
    <el-dialog title="巡检任务" v-model="taskDialogVisible" width="560px">
      <template v-if="currentRecord">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="计划">{{ currentRecord.planName }}</el-descriptions-item>
          <el-descriptions-item label="设备">{{ currentRecord.equipmentName }}</el-descriptions-item>
          <el-descriptions-item label="巡检人">{{ currentRecord.inspectorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="巡检时间">{{ currentRecord.inspectionTime }}</el-descriptions-item>
          <el-descriptions-item label="任务状态">{{ taskText(currentRecord) }}</el-descriptions-item>
          <el-descriptions-item label="结果">{{ resultText(currentRecord.status) }}</el-descriptions-item>
          <el-descriptions-item v-if="currentRecord.abnormalDesc" label="异常描述" :span="2">{{ currentRecord.abnormalDesc }}</el-descriptions-item>
          <el-descriptions-item v-if="currentRecord.remark" label="备注" :span="2">{{ currentRecord.remark }}</el-descriptions-item>
        </el-descriptions>

        <el-form v-if="fillVisible" :model="fillForm" label-width="90px" style="margin-top:16px">
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
        <el-button @click="taskDialogVisible = false">关闭</el-button>
        <el-button v-if="canAccept" type="success" @click="handleAccept">接单</el-button>
        <el-button v-if="canFill && !fillVisible" type="primary" @click="fillVisible = true">提交巡检</el-button>
        <el-button v-if="canFill && fillVisible" type="primary" @click="handleFillSubmit">确定提交</el-button>
        <el-button v-if="canRepair" type="danger" @click="handleRepair">生成报修单</el-button>
      </template>
    </el-dialog>

    <!-- 手动新增 -->
    <el-dialog title="新增记录" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="巡检计划"><el-select v-model="form.planId" filterable placeholder="请选择" style="width:100%"><el-option v-for="p in plans.filter(i => i.id != null)" :key="p.id" :label="p.planName" :value="p.id" /></el-select></el-form-item>
        <el-form-item label="设备" prop="equipmentId"><el-select v-model="form.equipmentId" filterable placeholder="请选择" style="width:100%"><el-option v-for="e in equipments.filter(i => i.id != null)" :key="e.id" :label="e.equipmentName" :value="e.id" /></el-select></el-form-item>
        <el-form-item label="巡检人员" prop="inspectorUserId"><el-select v-model="form.inspectorUserId" filterable placeholder="请选择" style="width:100%"><el-option v-for="u in users.filter(i => i.id != null)" :key="u.id" :label="u.realName" :value="u.id" /></el-select></el-form-item>
        <el-form-item label="巡检时间"><el-date-picker v-model="form.inspectionTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
        <el-form-item label="巡检结果" prop="status"><el-select v-model="form.status"><el-option label="正常" :value="1" /><el-option label="异常" :value="2" /></el-select></el-form-item>
        <el-form-item v-if="form.status === 2" label="异常描述"><el-input v-model="form.abnormalDesc" type="textarea" :rows="3" /></el-form-item>
        <el-form-item label="备注"><el-input v-model="form.remark" type="textarea" :rows="2" /></el-form-item>
      </el-form>
      <template #footer><div class="dialog-footer"><el-button @click="dialogVisible = false">取消</el-button><el-button type="primary" @click="handleSubmit">确定</el-button></div></template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { addRecord, updateRecord, getRecordPage, acceptRecord, createRecordRepair } from '@/api/inspection/record'
import { getPlanPage } from '@/api/inspection/plan'
import { getEquipmentPage } from '@/api/equipment/equipment'
import { getUserPage } from '@/api/system/user'

const loading = ref(false)
const records = ref([])
const plans = ref([])
const equipments = ref([])
const users = ref([])
const searchForm = reactive({ planId: '' })
const taskDialogVisible = ref(false)
const currentRecord = ref(null)
const fillVisible = ref(false)
const fillForm = reactive({ status: 1, abnormalDesc: '', remark: '' })
const dialogVisible = ref(false)
const formRef = ref(null)
const form = reactive({ id: null, planId: null, equipmentId: null, inspectorUserId: null, inspectionTime: '', status: 1, abnormalDesc: '', remark: '' })

const rules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  inspectorUserId: [{ required: true, message: '请选择巡检人员', trigger: 'change' }],
  status: [{ required: true, message: '请选择结果', trigger: 'change' }]
}

const dates = computed(() => [...new Set(records.value.map(r => (r.inspectionTime || '').slice(0, 10)))].sort())
const matrixRows = computed(() => {
  const map = new Map()
  records.value.forEach(r => {
    if (!map.has(r.equipmentId)) map.set(r.equipmentId, { equipmentId: r.equipmentId, equipmentName: r.equipmentName })
  })
  return [...map.values()]
})

const cellOf = (row, date) => records.value.find(r => r.equipmentId === row.equipmentId && (r.inspectionTime || '').startsWith(date))
const taskText = (r) => {
  if (!r) return ''
  if (r.taskStatus === 2) return resultText(r.status)
  if (r.taskStatus === 1) return '已接单'
  return '待接单'
}
const resultText = (s) => ({ 1: '正常', 2: '异常', 3: '未巡检' }[s] || '')
const cellClass = (row, date) => {
  const r = cellOf(row, date)
  if (!r) return ''
  if (r.taskStatus === 2) return r.status === 2 ? 'cell-abnormal' : r.status === 1 ? 'cell-normal' : 'cell-missed'
  return r.taskStatus === 1 ? 'cell-accepted' : 'cell-pending'
}

const canAccept = computed(() => currentRecord.value && currentRecord.value.taskStatus === 0 && !currentRecord.value.inspectorUserId)
const canFill = computed(() => currentRecord.value && currentRecord.value.taskStatus === 1)
const canRepair = computed(() => currentRecord.value && currentRecord.value.status === 2 && !currentRecord.value.repairRecordId)

onMounted(async () => {
  fetchData()
  const p = await getPlanPage({ pageNum: 1, pageSize: 200 }); plans.value = p.data.records
  const e = await getEquipmentPage({ pageNum: 1, pageSize: 200 }); equipments.value = e.data.records
  const u = await getUserPage({ pageNum: 1, pageSize: 200 }); users.value = u.data.records
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRecordPage({ pageNum: 1, pageSize: 500, planId: searchForm.planId || undefined })
    records.value = res.data.records
  } finally { loading.value = false }
}
function handleSearch() { fetchData() }
function resetSearch() { searchForm.planId = ''; fetchData() }

function openCell(row, date) {
  const r = cellOf(row, date)
  if (!r) { ElMessage.info('该日无巡检任务'); return }
  currentRecord.value = r
  fillVisible.value = false
  fillForm.status = 1
  fillForm.abnormalDesc = ''
  fillForm.remark = ''
  taskDialogVisible.value = true
}

async function handleAccept() {
  try {
    await acceptRecord(currentRecord.value.id)
    ElMessage.success('接单成功')
    taskDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleFillSubmit() {
  if (fillForm.status === 2 && !fillForm.abnormalDesc) {
    ElMessage.warning('异常必须填写异常描述')
    return
  }
  try {
    await updateRecord({
      id: currentRecord.value.id,
      equipmentId: currentRecord.value.equipmentId,
      inspectorUserId: currentRecord.value.inspectorUserId,
      status: fillForm.status,
      abnormalDesc: fillForm.abnormalDesc,
      remark: fillForm.remark
    })
    ElMessage.success('巡检记录已提交')
    taskDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleRepair() {
  try {
    const res = await createRecordRepair(currentRecord.value.id)
    ElMessage.success('已生成报修单，单号ID:' + res.data)
    taskDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

function handleAdd() { resetForm(); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); Object.assign(form, { id: null, planId: null, equipmentId: null, inspectorUserId: null, inspectionTime: '', status: 1, abnormalDesc: '', remark: '' }) }
async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await addRecord(form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}
</script>

<style scoped>
.legend { margin-left: 16px; font-size: 13px; color: #909399; }
.dot { display: inline-block; width: 10px; height: 10px; border-radius: 50%; margin: 0 4px 0 10px; vertical-align: middle; }
.dot-pending { background: #c0c4cc; }
.dot-accepted { background: #409eff; }
.dot-normal { background: #67c23a; }
.dot-abnormal { background: #f56c6c; }
.dot-missed { background: #e6a23c; }
.matrix-cell { min-height: 40px; display: flex; align-items: center; justify-content: center; cursor: pointer; border-radius: 4px; font-size: 13px; }
.cell-pending { color: #909399; background: #f5f7fa; }
.cell-accepted { color: #409eff; background: #ecf5ff; }
.cell-normal { color: #67c23a; background: #f0f9eb; }
.cell-abnormal { color: #f56c6c; background: #fef0f0; }
.cell-missed { color: #e6a23c; background: #fdf6ec; }
</style>
