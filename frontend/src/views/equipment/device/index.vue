<template>
  <div class="equipment-device">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备管理</span>
          <el-button type="primary" @click="handleAdd">新增设备</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备分类">
          <el-select v-model="searchForm.categoryId" placeholder="请选择分类" clearable>
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="维修中" :value="1" />
            <el-option label="停用" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentName" label="设备名称" />
        <el-table-column prop="equipmentCode" label="设备编号" />
        <el-table-column prop="categoryId" label="分类" width="120">
          <template #default="{ row }">
            {{ getCategoryName(row.categoryId) }}
          </template>
        </el-table-column>
        <el-table-column prop="location" label="位置" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="maintenanceUser" label="维护人员" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" @click="handleStatusChange(row, 1)">维修</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="handleStatusChange(row, 0)">恢复正常</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleStatusChange(row, 2)">停用</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
        </el-form-item>
        <el-form-item label="设备编号" prop="equipmentCode">
          <el-input v-model="form.equipmentCode" placeholder="请输入设备编号" />
        </el-form-item>
        <el-form-item label="设备分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择分类">
            <el-option v-for="item in categoryList" :key="item.id" :label="item.categoryName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装位置">
          <el-input v-model="form.location" placeholder="请输入安装位置" />
        </el-form-item>
        <el-form-item label="维护人员">
          <el-input v-model="form.maintenanceUser" placeholder="请输入维护人员" />
        </el-form-item>
        <el-form-item label="安装日期">
          <el-date-picker v-model="form.installDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="保修到期">
          <el-date-picker v-model="form.warrantyDate" type="date" placeholder="选择日期" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { listEquipmentCategory } from '../../../api/equipmentCategory'
import {
  pageEquipment,
  addEquipment,
  updateEquipment,
  deleteEquipment,
  updateEquipmentStatus
} from '../../../api/equipment'

const tableData = ref([])
const categoryList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = ref({ categoryId: null, status: null })

const form = ref({
  equipmentName: '',
  equipmentCode: '',
  categoryId: null,
  location: '',
  maintenanceUser: '',
  installDate: null,
  warrantyDate: null,
  remark: ''
})

const rules = {
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  equipmentCode: [{ required: true, message: '请输入设备编号', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const getCategoryName = (id) => {
  const category = categoryList.value.find(item => item.id === id)
  return category ? category.categoryName : ''
}

const getStatusType = (s) => ({ 0: 'success', 1: 'warning', 2: 'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0: '正常', 1: '维修中', 2: '停用' }[s] || '未知')

const loadCategories = async () => {
  const res = await listEquipmentCategory()
  categoryList.value = res.data
}

const loadData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    categoryId: searchForm.value.categoryId,
    status: searchForm.value.status
  }
  const res = await pageEquipment(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { categoryId: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增设备'
  form.value = { equipmentName: '', equipmentCode: '', categoryId: null, location: '', maintenanceUser: '', installDate: null, warrantyDate: null, remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑设备'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateEquipment(form.value)
    ElMessage.success('更新成功')
  } else {
    await addEquipment(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleStatusChange = async (row, status) => {
  const text = getStatusText(status)
  await ElMessageBox.confirm(`确认将设备状态更改为"${text}"？`, '提示')
  await updateEquipmentStatus({ id: row.id, status })
  ElMessage.success('状态更新成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该设备？', '提示')
  await deleteEquipment(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>

<style scoped>
.equipment-device { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>