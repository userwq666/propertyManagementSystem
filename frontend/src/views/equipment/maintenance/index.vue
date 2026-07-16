<template>
  <div class="app-container">
    <div class="page-header">
      <h1>设备维保记录</h1>
    </div>
    <el-card>
      <template #header>
        <div class="card-header">
          <span>维保记录列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['equipment:maintenance:add']">
              <plus /> 新增维保
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="设备名称" prop="equipmentId">
          <el-select v-model="queryParams.equipmentId" placeholder="请选择设备" clearable filterable style="width: 200px">
            <el-option v-for="item in equipmentOptions" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="维保类型" prop="maintenanceType">
          <el-select v-model="queryParams.maintenanceType" placeholder="请选择维保类型" clearable style="width: 180px">
            <el-option v-for="item in maintenanceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 180px">
            <el-option v-for="item in statusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <search /> 查询
          </el-button>
          <el-button @click="resetQuery">
            <refresh /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        border
        style="width: 100%"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column prop="equipmentName" label="设备名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="maintenanceType" label="维保类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="maintenanceTypeColorMap[scope.row.maintenanceType] || ''" effect="dark">
              {{ getDictLabel(maintenanceTypeOptions, scope.row.maintenanceType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceContent" label="维保内容" min-width="200" show-overflow-tooltip />
        <el-table-column prop="maintenancePersonnelName" label="负责人" width="100" align="center" />
        <el-table-column prop="startTime" label="开始时间" width="170" align="center" />
        <el-table-column prop="endTime" label="结束时间" width="170" align="center" />
        <el-table-column prop="cost" label="费用(元)" width="110" align="center">
          <template #default="scope">
            <span v-if="scope.row.cost != null">{{ scope.row.cost }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="statusColorMap[scope.row.status] || ''" effect="dark">
              {{ getDictLabel(statusOptions, scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="260" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleEdit(scope.row)" v-permission="['equipment:maintenance:edit']">编辑</el-button>
            <el-button size="small" type="warning" link @click="handleStart(scope.row)" v-if="scope.row.status === 0" v-permission="['equipment:maintenance:edit']">开始处理</el-button>
            <el-button size="small" type="success" link @click="handleComplete(scope.row)" v-if="scope.row.status === 1" v-permission="['equipment:maintenance:edit']">完成</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)" v-permission="['equipment:maintenance:delete']">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <el-form ref="formRef" :model="form" :rules="formRules" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="维保设备" prop="equipmentId">
              <el-select v-model="form.equipmentId" placeholder="请选择设备" filterable style="width: 100%">
                <el-option v-for="item in equipmentOptions" :key="item.id" :label="item.name" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="维保类型" prop="maintenanceType">
              <el-select v-model="form.maintenanceType" placeholder="请选择维保类型" style="width: 100%">
                <el-option v-for="item in maintenanceTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="maintenancePersonnelId">
              <el-input v-model="form.maintenancePersonnelId" placeholder="请输入负责人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="费用(元)" prop="cost">
              <el-input-number v-model="form.cost" :min="0" :precision="2" style="width: 100%" placeholder="请输入费用" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="维保内容" prop="maintenanceContent">
          <el-input v-model="form.maintenanceContent" type="textarea" :rows="3" placeholder="请输入维保内容" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker v-model="form.startTime" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束时间" prop="endTime">
              <el-date-picker v-model="form.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="更换配件" prop="partsReplaced">
              <el-input v-model="form.partsReplaced" placeholder="请输入更换配件" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="下次维保日期" prop="nextMaintenanceDate">
              <el-date-picker v-model="form.nextMaintenanceDate" type="date" placeholder="选择下次维保日期" value-format="YYYY-MM-DD" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 完成维保弹窗 -->
    <el-dialog
      v-model="completeDialogVisible"
      title="完成维保"
      width="500px"
      :close-on-click-modal="false"
      :destroy-on-close="true"
    >
      <el-form ref="completeFormRef" :model="completeForm" :rules="completeFormRules" label-width="100px" class="dialog-form">
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker v-model="completeForm.endTime" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </el-form-item>
        <el-form-item label="费用(元)" prop="cost">
          <el-input-number v-model="completeForm.cost" :min="0" :precision="2" style="width: 100%" placeholder="请输入费用" />
        </el-form-item>
        <el-form-item label="更换配件" prop="partsReplaced">
          <el-input v-model="completeForm.partsReplaced" placeholder="请输入更换配件" />
        </el-form-item>
        <el-form-item label="下次维保日期" prop="nextMaintenanceDate">
          <el-date-picker v-model="completeForm.nextMaintenanceDate" type="date" placeholder="选择下次维保日期" value-format="YYYY-MM-DD" style="width: 100%" />
        </el-form-item>
        <el-form-item label="完成备注" prop="remark">
          <el-input v-model="completeForm.remark" type="textarea" :rows="2" placeholder="请输入完成备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="completeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitComplete" :loading="completeLoading">确定完成</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Refresh, Search } from '@element-plus/icons-vue'
import {
  getMaintenancePage,
  getMaintenanceInfo,
  addMaintenance,
  updateMaintenance,
  deleteMaintenance,
  startMaintenance,
  completeMaintenance
} from '@/api/equipment/maintenance'
import { getEquipmentPage } from '@/api/equipment/equipment'

// 字典
const maintenanceTypeOptions = [
  { value: 1, label: '日常巡检' },
  { value: 2, label: '定期保养' },
  { value: 3, label: '故障维修' },
  { value: 4, label: '其他' }
]

const maintenanceTypeColorMap = {
  1: 'info',
  2: 'primary',
  3: 'warning',
  4: ''
}

const statusOptions = [
  { value: 0, label: '待处理' },
  { value: 1, label: '进行中' },
  { value: 2, label: '已完成' },
  { value: 3, label: '已取消' }
]

const statusColorMap = {
  0: 'info',
  1: 'warning',
  2: 'success',
  3: 'danger'
}

function getDictLabel(options, value) {
  const item = options.find(item => item.value === value)
  return item ? item.label : value
}

// 设备列表
const equipmentOptions = ref([])

async function loadEquipmentOptions() {
  try {
    const res = await getEquipmentPage({ pageNum: 1, pageSize: 9999 })
    equipmentOptions.value = (res.data && res.data.rows) || res.rows || res.data || []
  } catch (error) {
    console.error('加载设备列表失败:', error)
  }
}

// 表格数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  equipmentId: '',
  maintenanceType: '',
  status: ''
})

async function getList() {
  loading.value = true
  try {
    const params = { ...queryParams }
    // 清理空值
    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key]
      }
    })
    const res = await getMaintenancePage(params)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0

    // 补充设备名称
    tableData.value.forEach(row => {
      if (!row.equipmentName) {
        const eq = equipmentOptions.value.find(e => e.id === row.equipmentId)
        if (eq) row.equipmentName = eq.name
      }
    })
  } catch (error) {
    console.error('获取维保记录列表失败:', error)
  } finally {
    loading.value = false
  }
}

function handleQuery() {
  queryParams.pageNum = 1
  getList()
}

function resetQuery() {
  queryParams.equipmentId = ''
  queryParams.maintenanceType = ''
  queryParams.status = ''
  queryParams.pageNum = 1
  getList()
}

function handleRefresh() {
  getList()
}

// 新增/编辑弹窗
const dialogVisible = ref(false)
const dialogTitle = ref('新增维保记录')
const isAdd = ref(true)
const formRef = ref(null)
const submitLoading = ref(false)

const form = reactive({
  id: undefined,
  equipmentId: undefined,
  maintenanceType: undefined,
  maintenanceContent: '',
  maintenancePersonnelId: '',
  startTime: '',
  endTime: '',
  cost: undefined,
  partsReplaced: '',
  nextMaintenanceDate: '',
  remark: ''
})

const formRules = {
  equipmentId: [{ required: true, message: '请选择维保设备', trigger: 'change' }],
  maintenanceType: [{ required: true, message: '请选择维保类型', trigger: 'change' }],
  maintenanceContent: [{ required: true, message: '请输入维保内容', trigger: 'blur' }]
}

function handleAdd() {
  isAdd.value = true
  dialogTitle.value = '新增维保记录'
  form.id = undefined
  form.equipmentId = undefined
  form.maintenanceType = undefined
  form.maintenanceContent = ''
  form.maintenancePersonnelId = ''
  form.startTime = ''
  form.endTime = ''
  form.cost = undefined
  form.partsReplaced = ''
  form.nextMaintenanceDate = ''
  form.remark = ''
  dialogVisible.value = true
  nextTick(() => {
    formRef.value?.clearValidate()
  })
}

async function handleEdit(row) {
  isAdd.value = false
  dialogTitle.value = '编辑维保记录'
  try {
    const res = await getMaintenanceInfo(row.id)
    const data = res.data || res
    form.id = data.id
    form.equipmentId = data.equipmentId
    form.maintenanceType = data.maintenanceType
    form.maintenanceContent = data.maintenanceContent || ''
    form.maintenancePersonnelId = data.maintenancePersonnelId || ''
    form.startTime = data.startTime || ''
    form.endTime = data.endTime || ''
    form.cost = data.cost
    form.partsReplaced = data.partsReplaced || ''
    form.nextMaintenanceDate = data.nextMaintenanceDate || ''
    form.remark = data.remark || ''
    dialogVisible.value = true
    nextTick(() => {
      formRef.value?.clearValidate()
    })
  } catch (error) {
    console.error('获取维保详情失败:', error)
  }
}

function submitForm() {
  formRef.value?.validate(async (valid) => {
    if (!valid) return
    submitLoading.value = true
    try {
      const submitData = { ...form }
      // 清理空值
      Object.keys(submitData).forEach(key => {
        if (submitData[key] === '' || submitData[key] === null || submitData[key] === undefined) {
          delete submitData[key]
        }
      })
      if (isAdd.value) {
        delete submitData.id
        await addMaintenance(submitData)
        ElMessage.success('新增成功')
      } else {
        await updateMaintenance(submitData)
        ElMessage.success('更新成功')
      }
      dialogVisible.value = false
      getList()
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      submitLoading.value = false
    }
  })
}

// 开始处理
async function handleStart(row) {
  try {
    await ElMessageBox.confirm('确认开始处理该维保记录？', '提示', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    await startMaintenance(row.id)
    ElMessage.success('已开始处理')
    getList()
  } catch (error) {
    console.error('开始处理失败:', error)
  }
}

// 完成维保
const completeDialogVisible = ref(false)
const completeFormRef = ref(null)
const completeLoading = ref(false)
const completeRecordId = ref(0)

const completeForm = reactive({
  endTime: '',
  cost: undefined,
  partsReplaced: '',
  nextMaintenanceDate: '',
  remark: ''
})

const completeFormRules = {
  endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }]
}

function handleComplete(row) {
  completeRecordId.value = row.id
  completeForm.endTime = ''
  completeForm.cost = undefined
  completeForm.partsReplaced = ''
  completeForm.nextMaintenanceDate = ''
  completeForm.remark = ''
  completeDialogVisible.value = true
  nextTick(() => {
    completeFormRef.value?.clearValidate()
  })
}

async function submitComplete() {
  completeFormRef.value?.validate(async (valid) => {
    if (!valid) return
    completeLoading.value = true
    try {
      const data = { ...completeForm }
      Object.keys(data).forEach(key => {
        if (data[key] === '' || data[key] === null || data[key] === undefined) {
          delete data[key]
        }
      })
      await completeMaintenance(completeRecordId.value, data)
      ElMessage.success('维保完成')
      completeDialogVisible.value = false
      getList()
    } catch (error) {
      console.error('完成维保失败:', error)
    } finally {
      completeLoading.value = false
    }
  })
}

// 删除
function handleDelete(row) {
  ElMessageBox.confirm(`确认删除该维保记录？`, '删除确认', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    try {
      await deleteMaintenance(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadEquipmentOptions()
  getList()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  h1 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form {
  padding: 10px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.small-padding {
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}
</style>
