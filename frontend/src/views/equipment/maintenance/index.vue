<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="设备">
        <el-select v-model="searchForm.equipmentId" placeholder="请选择" clearable filterable>
          <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="维保状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待维保" :value="0" /><el-option label="维保中" :value="1" /><el-option label="已完成" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'equipment:maintenance:add'">新增维保记录</el-button>
        </div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentName" label="设备名称" />
        <el-table-column label="维保类型" width="100">
          <template #default="{ row }">{{ typeText(row.maintenanceType) }}</template>
        </el-table-column>
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenancePersonnelId" label="维保人员ID" width="100" />
        <el-table-column prop="startTime" label="开始时间" width="120" />
        <el-table-column prop="completeDate" label="完成日期" width="120" />
        <el-table-column prop="cost" label="费用" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'equipment:maintenance:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'equipment:maintenance:delete'">删除</el-button>
            <el-button v-if="row.status===0" type="success" size="small" @click="handleStart(row)" v-permission="'equipment:maintenance:edit'">开始</el-button>
            <el-button v-if="row.status===1" type="warning" size="small" @click="handleComplete(row)" v-permission="'equipment:maintenance:edit'">完成</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备" prop="equipmentId">
          <el-select v-model="form.equipmentId" placeholder="请选择" filterable>
            <el-option v-for="e in equipments" :key="e.id" :label="e.equipmentName" :value="e.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="维保类型" prop="maintenanceType">
          <el-select v-model="form.maintenanceType">
            <el-option label="日常保养" :value="0" /><el-option label="定期检修" :value="1" /><el-option label="故障维修" :value="2" /><el-option label="大修" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="维保人员ID" prop="maintenancePersonnelId">
          <el-input v-model="form.maintenancePersonnelId" />
        </el-form-item>
        <el-form-item label="开始时间">
          <el-date-picker v-model="form.startTime" type="date" value-format="YYYY-MM-DD HH:mm:ss" />
        </el-form-item>
        <el-form-item label="维保内容">
          <el-input v-model="form.maintenanceContent" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="费用">
          <el-input-number v-model="form.cost" :min="0" :precision="2" style="width:100%" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addMaintenance, updateMaintenance, deleteMaintenance, getMaintenancePage, startMaintenance, completeMaintenance } from '@/api/equipment/maintenance'
import { getEquipmentPage } from '@/api/equipment/equipment'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const equipments = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, equipmentId: '', status: '' })
const form = reactive({
    id: null, equipmentId: null, maintenanceType: 0, maintenancePersonnelId: null,
    startTime: '', maintenanceContent: '', cost: 0, remark: ''
  })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑维保记录' : '新增维保记录')
const typeText = (t) => ({ 0: '日常保养', 1: '定期检修', 2: '故障维修', 3: '大修' }[t] || '')
const statusTag = (s) => ({ 0: 'info', 1: 'warning', 2: 'success' }[s] || 'info')
const statusText = (s) => ({ 0: '待维保', 1: '维保中', 2: '已完成' }[s] || '')

const rules = {
  equipmentId: [{ required: true, message: '请选择设备', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维保类型', trigger: 'change' }],
  maintenancePersonnelId: [{ required: true, message: '请输入维保人员ID', trigger: 'blur' }]
}

onMounted(async () => {
  fetchData()
  const res = await getEquipmentPage({ pageNum: 1, pageSize: 200 })
  equipments.value = res.data.records
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getMaintenancePage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.equipmentId = ''; searchForm.status = ''; handleSearch() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateMaintenance(form)
    else await addMaintenance(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该记录吗？', '提示', { type: 'warning' })
  try { await deleteMaintenance(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleStart(row) {
  try { await startMaintenance(row.id); ElMessage.success('已开始维保'); fetchData() } catch (e) { /* handled */ }
}

async function handleComplete(row) {
  try { await completeMaintenance(row.id); ElMessage.success('维保已完成'); fetchData() } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
