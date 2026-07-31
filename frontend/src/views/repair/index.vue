<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="业主">
        <el-select v-model="searchForm.ownerId" placeholder="请选择" clearable filterable>
          <el-option v-for="o in owners.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报修状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待派单" :value="0" /><el-option label="处理中" :value="1" /><el-option label="待确认" :value="2" /><el-option label="已完成" :value="3" /><el-option label="已取消" :value="4" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'repair:record:add'">新增报修</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="业主" width="100">
          <template #default="{ row }">{{ row.ownerName || '系统代报' }}</template>
        </el-table-column>
        <el-table-column label="房号" width="100">
          <template #default="{ row }">{{ row.roomNo || '公共区域' }}</template>
        </el-table-column>
        <el-table-column label="报修类型" width="100">
          <template #default="{ row }">{{ typeText(row.repairType) }}</template>
        </el-table-column>
        <el-table-column label="关联设备" width="120">
          <template #default="{ row }">{{ row.equipmentName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="repairContent" label="报修描述" show-overflow-tooltip />
        <el-table-column prop="handlerName" label="处理人" width="100" />
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="createTime" label="报修时间" width="180" />
        <el-table-column label="操作" min-width="320" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)" v-permission="'repair:record:list'">详情</el-button>
            <el-button v-if="row.status === 0 || row.status === 1" type="primary" size="small" @click="handleEdit(row)" v-permission="'repair:record:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'repair:record:delete'">删除</el-button>
            <el-button v-if="row.status===0 && isAdmin" type="warning" size="small" @click="handleAssign(row)" v-permission="'repair:record:process'">派单</el-button>
            <el-button v-if="row.status===0 && isWorker" type="warning" size="small" @click="handleAccept(row)" v-permission="'repair:record:process'">接单</el-button>
            <el-button v-if="row.status===1 && (isAdmin || row.handlerId === userId)" type="success" size="small" @click="handleComplete(row)" v-permission="'repair:record:process'">结单</el-button>
            <el-button v-if="row.status===2 && !row.evaluateScore && (isAdmin || isOwner)" type="success" size="small" @click="handleRating(row)" v-permission="'repair:record:evaluate'">确认</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <!-- 详情 -->
    <el-dialog title="报修详情" v-model="detailDialogVisible" width="640px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="报修单号">{{ detailRow.repairNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ statusText(detailRow.status) }}</el-descriptions-item>
        <el-descriptions-item label="业主">{{ detailRow.ownerName ? detailRow.ownerName + (detailRow.ownerPhone ? '（' + detailRow.ownerPhone + '）' : '') : '系统代报' + (detailRow.creatorPhone ? '（' + detailRow.creatorName + ' ' + detailRow.creatorPhone + '）' : '') }}</el-descriptions-item>
        <el-descriptions-item label="房号">{{ detailRow.roomNo || '公共区域' }}</el-descriptions-item>
        <el-descriptions-item label="报修类型">{{ typeText(detailRow.repairType) }}</el-descriptions-item>
        <el-descriptions-item label="关联设备">{{ detailRow.equipmentName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报修内容" :span="2">{{ detailRow.repairContent }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailRow.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailRow.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理内容" :span="2">{{ detailRow.handleContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分">{{ detailRow.evaluateScore ? detailRow.evaluateScore + ' 分' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ detailRow.evaluateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ detailRow.evaluateContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="报修时间" :span="2">{{ detailRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <!-- 新增/编辑 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主" prop="ownerId" v-if="isAdmin">
          <el-select v-model="form.ownerId" placeholder="请选择" filterable clearable>
            <el-option label="系统代报" :value="0" />
            <el-option v-for="o in owners.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择" filterable clearable>
            <el-option v-if="isAdmin" label="公共区域" :value="0" />
            <el-option v-for="h in houses.filter(i => i.id != null)" :key="h.id" :label="h.roomNo" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联设备">
          <el-select v-model="form.equipmentId" placeholder="请选择（可空）" filterable clearable>
            <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType"><el-option label="水电维修" value="水电" /><el-option label="门窗维修" value="门窗" /><el-option label="公共设施" value="公共设施" /><el-option label="电器维修" value="家电" /><el-option label="其他" value="其他" /></el-select>
        </el-form-item>
        <el-form-item label="报修描述" prop="repairContent">
          <el-input v-model="form.repairContent" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.repairImages" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 派单 -->
    <el-dialog title="派单" v-model="assignDialogVisible" width="450px">
      <el-form label-width="100px">
        <el-form-item label="维修工" required>
          <el-select v-model="assignForm.handlerId" placeholder="请选择维修工" filterable>
            <el-option v-for="w in workers" :key="w.id" :label="w.realName" :value="w.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 结单 -->
    <el-dialog title="结单" v-model="completeDialogVisible" width="500px">
      <el-form label-width="100px">
        <el-form-item label="关联设备" v-if="completeForm.hasEquipment" required>
          <el-select v-model="completeForm.equipmentId" placeholder="请选择关联设备" filterable>
            <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理内容">
          <el-input v-model="completeForm.handleContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitComplete">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价 -->
    <el-dialog title="确认并评价" v-model="ratingDialogVisible" width="400px">
      <el-form :model="ratingForm" label-width="100px">
        <el-form-item label="评分" required>
          <el-rate v-model="ratingForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="ratingForm.content" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="ratingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRating">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addRepair, updateRepair, deleteRepair, getRepairById, getRepairPage, updateRepairStatus, updateRepairRating, getRepairHouses, getRepairEquipments } from '@/api/repair/record'
import { getOwnerPage } from '@/api/community/owner'
import { getUserPage } from '@/api/system/user'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const detailDialogVisible = ref(false)
const assignDialogVisible = ref(false)
const completeDialogVisible = ref(false)
const ratingDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const owners = ref([])
const houses = ref([])
const equipments = ref([])
const workers = ref([])
const currentRow = ref(null)
const detailRow = ref(null)
const userStore = useUserStore()

const searchForm = reactive({ pageNum: 1, pageSize: 10, ownerId: '', status: '' })
const form = reactive({ id: null, ownerId: null, houseId: null, equipmentId: null, repairType: '水电', repairContent: '', repairImages: '', remark: '' })
const assignForm = reactive({ handlerId: null })
const completeForm = reactive({ equipmentId: null, hasEquipment: false, handleContent: '' })
const ratingForm = reactive({ score: 5, content: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑报修' : '新增报修')
const isAdmin = computed(() => userStore.roles.includes('超级管理员') || userStore.roles.includes('物业管理员') || userStore.userInfo.roleName === '超级管理员' || userStore.userInfo.roleName === '物业管理员')
const isWorker = computed(() => userStore.roles.includes('维修工') || userStore.userInfo.roleName === '维修工')
const isOwner = computed(() => userStore.roles.includes('业主') || userStore.userInfo.roleName === '业主')
const userId = computed(() => userStore.userInfo.id || userStore.userInfo.userId)
const typeText = (t) => ({ 水电: '水电维修', 门窗: '门窗维修', 家电: '电器维修', 公共设施: '公共设施', 其他: '其他' }[t] || t || '')
const statusTag = (s) => ({ 0: 'info', 1: 'warning', 2: 'primary', 3: 'success', 4: 'danger' }[s] || 'info')
const statusText = (s) => ({ 0: '待派单', 1: '处理中', 2: '待确认', 3: '已完成', 4: '已取消' }[s] || '')

const rules = {
  ownerId: [{ required: true, message: '请选择业主', trigger: 'change' }],
  houseId: [{ required: true, message: '请选择房屋', trigger: 'change' }],
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  repairContent: [{ required: true, message: '请输入报修描述', trigger: 'blur' }]
}

watch(() => form.ownerId, async (val) => {
  if (!isAdmin.value) return
  try {
    const res = await getRepairHouses(val && val !== 0 ? { ownerId: val } : {})
    houses.value = res.data
  } catch (e) { /* handled */ }
})

onMounted(async () => {
  fetchData()
  if (isAdmin.value) {
    try {
      const oRes = await getOwnerPage({ pageNum: 1, pageSize: 200 }, { silent: true })
      owners.value = oRes.data.records
    } catch (e) { /* handled */ }
  }
  if (isAdmin.value || isOwner.value) {
    try {
      const hRes = await getRepairHouses({}, { silent: true })
      houses.value = hRes.data
    } catch (e) { /* handled */ }
  }
  try {
    const eRes = await getRepairEquipments({ silent: true })
    equipments.value = eRes.data
  } catch (e) { /* handled */ }
  if (isAdmin.value) {
    try {
      const wRes = await getUserPage({ pageNum: 1, pageSize: 100 }, { silent: true })
      workers.value = (wRes.data.records || []).filter(u => u.roleName === '维修工')
    } catch (e) { /* handled */ }
  }
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRepairPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.ownerId = ''; searchForm.status = ''; handleSearch() }
function handleDetail(row) { detailRow.value = row; detailDialogVisible.value = true }
function handleAdd() { isEdit.value = false; resetForm(); if (isAdmin.value) { form.ownerId = null; form.houseId = null } else { form.ownerId = null } form.equipmentId = null; dialogVisible.value = true }
function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    ...row,
    ownerId: isAdmin.value ? (row.ownerId || 0) : row.ownerId,
    houseId: isAdmin.value ? (row.houseId || 0) : row.houseId,
    equipmentId: row.equipmentId || null,
    repairType: row.repairType || 'WATER_ELECTRICITY',
    repairContent: row.repairContent || row.description || '',
    repairImages: row.repairImages || row.images || ''
  })
  dialogVisible.value = true
}
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    const payload = { ...form }
    if (payload.ownerId === 0) payload.ownerId = null
    if (payload.houseId === 0) payload.houseId = null
    if (isEdit.value) await updateRepair(payload)
    else await addRepair(payload)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该报修记录吗？', '提示', { type: 'warning' })
  try { await deleteRepair(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

function handleAssign(row) {
  currentRow.value = row
  assignForm.handlerId = null
  assignDialogVisible.value = true
}
async function submitAssign() {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择维修工')
    return
  }
  try {
    await updateRepairStatus({ id: currentRow.value.id, status: 1, handlerId: assignForm.handlerId })
    ElMessage.success('派单成功')
    assignDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}
async function handleAccept(row) {
  await ElMessageBox.confirm('确定接单吗？', '提示', { type: 'warning' })
  try {
    await updateRepairStatus({ id: row.id, status: 1 })
    ElMessage.success('接单成功')
    fetchData()
  } catch (e) { /* handled */ }
}
function handleComplete(row) {
  currentRow.value = row
  completeForm.equipmentId = row.equipmentId || null
  completeForm.hasEquipment = !!row.equipmentId
  completeForm.handleContent = ''
  completeDialogVisible.value = true
}
async function submitComplete() {
  if (completeForm.hasEquipment && !completeForm.equipmentId) {
    ElMessage.warning('该报修已关联设备，结单时必须选择关联设备')
    return
  }
  try {
    await updateRepairStatus({ id: currentRow.value.id, status: 2, equipmentId: completeForm.hasEquipment ? completeForm.equipmentId : undefined, handleContent: completeForm.handleContent })
    ElMessage.success('结单成功，等待确认')
    completeDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

function handleRating(row) { currentRow.value = row; ratingForm.score = 5; ratingForm.content = ''; ratingDialogVisible.value = true }
async function submitRating() {
  if (!ratingForm.score) {
    ElMessage.warning('请先评分')
    return
  }
  try {
    await updateRepairRating({ id: currentRow.value.id, ...ratingForm })
    ElMessage.success('确认并评价成功')
    ratingDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
