<template>
  <div class="app-container">
    <div class="page-header">
      <h1>设备列表管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['equipment:list:add']">
              <plus /> 新增设备
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="设备编号" prop="equipmentNo">
          <el-input v-model="queryParams.equipmentNo" placeholder="请输入设备编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="queryParams.equipmentName" placeholder="请输入设备名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="设备分类" prop="categoryId">
          <el-select v-model="queryParams.categoryId" placeholder="请选择分类" clearable style="width: 180px">
            <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="设备状态" prop="status">
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

      <!-- 设备表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="id"
        border
        style="width: 100%"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column prop="equipmentNo" label="设备编号" width="150" align="center" />
        <el-table-column prop="equipmentName" label="设备名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="brand" label="品牌" width="120" align="center" />
        <el-table-column prop="model" label="型号" width="120" align="center" />
        <el-table-column prop="location" label="位置" min-width="150" show-overflow-tooltip />
        <el-table-column prop="status" label="设备状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="statusColorMap[scope.row.status] || ''" effect="dark">
              {{ getDictLabel(statusOptions, scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="installDate" label="安装日期" width="130" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="170" align="center" />
        <el-table-column label="操作" align="center" width="260" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleDetail(scope.row)" v-permission="['equipment:list:list']">详情</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="warning" link @click="handleEdit(scope.row)" v-permission="['equipment:list:edit']">编辑</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="success" link @click="handleStatusToggle(scope.row)" v-if="scope.row.status === '4'" v-permission="['equipment:list:edit']">启用</el-button>
            <el-button size="small" type="info" link @click="handleStatusToggle(scope.row)" v-if="scope.row.status === '1'" v-permission="['equipment:list:edit']">停用</el-button>
            <el-divider direction="vertical" v-if="scope.row.status === '1' || scope.row.status === '4'" />
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)" v-permission="['equipment:list:delete']">删除</el-button>
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
          @size-change="getList"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="设备详情"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-descriptions :column="2" border class="detail-descriptions" v-if="detailData">
        <el-descriptions-item label="设备编号">{{ detailData.equipmentNo }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ detailData.equipmentName }}</el-descriptions-item>
        <el-descriptions-item label="设备分类">{{ getCategoryName(detailData.categoryId) }}</el-descriptions-item>
        <el-descriptions-item label="品牌">{{ detailData.brand || '-' }}</el-descriptions-item>
        <el-descriptions-item label="型号">{{ detailData.model || '-' }}</el-descriptions-item>
        <el-descriptions-item label="规格">{{ detailData.spec || '-' }}</el-descriptions-item>
        <el-descriptions-item label="位置">{{ detailData.location || '-' }}</el-descriptions-item>
        <el-descriptions-item label="楼层">{{ detailData.floor || '-' }}</el-descriptions-item>
        <el-descriptions-item label="安装日期">{{ detailData.installDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="质保到期">{{ detailData.warrantyEndDate || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备状态">
          <el-tag :type="statusColorMap[detailData.status] || ''" effect="dark">
            {{ getDictLabel(statusOptions, detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="addDialogTitle"
      width="800px"
      :close-on-click-modal="false"
      :before-close="closeAddDialog"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="110px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备编号" prop="equipmentNo">
              <el-input v-model="addForm.equipmentNo" placeholder="请输入设备编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="设备名称" prop="equipmentName">
              <el-input v-model="addForm.equipmentName" placeholder="请输入设备名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="设备分类" prop="categoryId">
              <el-select v-model="addForm.categoryId" placeholder="请选择分类" style="width: 100%">
                <el-option v-for="item in categoryOptions" :key="item.id" :label="item.categoryName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="品牌" prop="brand">
              <el-input v-model="addForm.brand" placeholder="请输入品牌" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="型号" prop="model">
              <el-input v-model="addForm.model" placeholder="请输入型号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="规格" prop="spec">
              <el-input v-model="addForm.spec" placeholder="请输入规格" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="位置" prop="location">
              <el-input v-model="addForm.location" placeholder="请输入位置" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼层" prop="floor">
              <el-input v-model="addForm.floor" placeholder="请输入楼层" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="安装日期" prop="installDate">
              <el-date-picker
                v-model="addForm.installDate"
                type="date"
                placeholder="请选择安装日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="质保到期" prop="warrantyEndDate">
              <el-date-picker
                v-model="addForm.warrantyEndDate"
                type="date"
                placeholder="请选择质保到期日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" placeholder="请输入备注" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddDialogAuto">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
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
  getEquipmentPage,
  getEquipmentInfo,
  addEquipment,
  updateEquipment,
  deleteEquipment,
  updateEquipmentStatus
} from '@/api/equipment/equipment'
import { getCategoryPage } from '@/api/equipment/category'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const categoryOptions = ref([])
const categoryMap = ref({})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  equipmentNo: '',
  equipmentName: '',
  categoryId: '',
  status: ''
})

// 状态选项
const statusOptions = [
  { value: '1', label: '正常' },
  { value: '2', label: '故障' },
  { value: '3', label: '维修中' },
  { value: '4', label: '停用' },
  { value: '5', label: '报废' }
]

const statusColorMap = {
  '1': 'success',
  '2': 'danger',
  '3': 'warning',
  '4': 'info',
  '5': 'danger'
}

// 详情弹窗
const detailDialogVisible = ref(false)
const detailData = ref(null)

// 新增/编辑弹窗
const addDialogVisible = ref(false)
const addDialogTitle = ref('新增设备')
const isAdd = ref(true)
const addFormRef = ref(null)

const addForm = reactive({
  id: undefined,
  equipmentNo: '',
  equipmentName: '',
  categoryId: '',
  brand: '',
  model: '',
  spec: '',
  location: '',
  buildingId: '',
  floor: '',
  installDate: '',
  warrantyEndDate: '',
  remark: ''
})

const addRules = reactive({
  equipmentNo: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择设备分类', trigger: 'change' }]
})

// 初始化
onMounted(async () => {
  await loadCategoryOptions()
  getList()
})

// 加载分类下拉
const loadCategoryOptions = async () => {
  try {
    const res = await getCategoryPage({ pageNum: 1, pageSize: 999 })
    const rows = res.rows || res.data?.rows || res.data || []
    categoryOptions.value = rows
    // 构建分类映射
    const map = {}
    rows.forEach(item => {
      map[item.id] = item.categoryName
    })
    categoryMap.value = map
  } catch (error) {
    console.error('获取分类列表失败:', error)
  }
}

// 获取分类名称
const getCategoryName = (id) => {
  return categoryMap.value[id] || '-'
}

// 获取设备列表
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    const res = await getEquipmentPage(params)
    const response = res
    tableData.value = response.rows || response.data?.rows || []
    total.value = response.total || response.data?.total || 0
  } catch (error) {
    console.error('获取设备列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.equipmentNo = ''
  queryParams.equipmentName = ''
  queryParams.categoryId = ''
  queryParams.status = ''
  handleQuery()
}

// 刷新
const handleRefresh = () => {
  getList()
  ElMessage.success('刷新成功')
}

// 获取字典标签
const getDictLabel = (options, value) => {
  if (!options || !value) return ''
  const item = options.find(d => d.value === value)
  return item ? item.label : ''
}

// 新增设备
const handleAdd = () => {
  isAdd.value = true
  addDialogTitle.value = '新增设备'
  resetAddForm()
  addDialogVisible.value = true
}

// 编辑设备
const handleEdit = async (row) => {
  try {
    const res = await getEquipmentInfo(row.id)
    const data = res.data || res
    isAdd.value = false
    addDialogTitle.value = '编辑设备'

    // 填充表单数据
    addForm.id = data.id
    addForm.equipmentNo = data.equipmentNo
    addForm.equipmentName = data.equipmentName
    addForm.categoryId = data.categoryId
    addForm.brand = data.brand || ''
    addForm.model = data.model || ''
    addForm.spec = data.spec || ''
    addForm.location = data.location || ''
    addForm.buildingId = data.buildingId || ''
    addForm.floor = data.floor || ''
    addForm.installDate = data.installDate || ''
    addForm.warrantyEndDate = data.warrantyEndDate || ''
    addForm.remark = data.remark || ''

    addDialogVisible.value = true
  } catch (error) {
    console.error('获取设备详情失败:', error)
  }
}

// 详情
const handleDetail = async (row) => {
  try {
    const res = await getEquipmentInfo(row.id)
    detailData.value = res.data || res
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取设备详情失败:', error)
  }
}

// 重置新增表单
const resetAddForm = () => {
  addForm.id = undefined
  addForm.equipmentNo = ''
  addForm.equipmentName = ''
  addForm.categoryId = ''
  addForm.brand = ''
  addForm.model = ''
  addForm.spec = ''
  addForm.location = ''
  addForm.buildingId = ''
  addForm.floor = ''
  addForm.installDate = ''
  addForm.warrantyEndDate = ''
  addForm.remark = ''
  nextTick(() => {
    if (addFormRef.value) {
      addFormRef.value.clearValidate()
    }
  })
}

// 关闭新增弹窗
const closeAddDialog = (done) => {
  resetAddForm()
  done()
}

const closeAddDialogAuto = () => {
  resetAddForm()
  addDialogVisible.value = false
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return
  try {
    await addFormRef.value.validate()

    const formData = { ...addForm }

    if (isAdd.value) {
      await addEquipment(formData)
      ElMessage.success('新增设备成功')
    } else {
      await updateEquipment(formData)
      ElMessage.success('修改设备成功')
    }
    addDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 删除设备
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除设备"${row.equipmentName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteEquipment(row.id)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 状态切换（正常 ↔ 停用）
const handleStatusToggle = (row) => {
  const newStatus = row.status === '1' ? '4' : '1'
  const actionLabel = newStatus === '1' ? '启用' : '停用'
  ElMessageBox.confirm(`确定要${actionLabel}设备"${row.equipmentName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateEquipmentStatus({ id: row.id, status: newStatus })
      ElMessage.success(`${actionLabel}成功`)
      getList()
    } catch (error) {
      console.error('状态变更失败:', error)
    }
  }).catch(() => {})
}
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

.detail-descriptions {
  :deep(.el-descriptions__row) {
    &:nth-child(odd) {
      :deep(.el-descriptions__cell) {
        background-color: #fafafa;
      }
    }
  }
  :deep(.el-descriptions__label) {
    font-weight: 600;
    color: #606266;
  }
}

.small-padding {
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}
</style>
