<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="设备分类">
        <el-select v-model="searchForm.categoryId" placeholder="请选择" clearable>
          <el-option v-for="c in categories.filter(i => i.id != null)" :key="c.id" :label="c.categoryName" :value="c.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备状�?>
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="正常" :value="1" /><el-option label="故障" :value="2" /><el-option label="维修�? :value="3" /><el-option label="停用" :value="4" /><el-option label="报废" :value="5" />
        </el-select>
      </el-form-item>
      <el-form-item label="设备名称">
        <el-input v-model="searchForm.equipmentName" placeholder="请输�? clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'equipment:list:add'">新增设备</el-button>
        </div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentName" label="设备名称" />
        <el-table-column prop="categoryName" label="分类" />
        <el-table-column prop="equipmentNo" label="编号" />
        <el-table-column prop="model" label="型号" />
        <el-table-column prop="location" label="位置" />
        <el-table-column label="状�? width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'equipment:list:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'equipment:list:delete'">删除</el-button>
            <el-button size="small" @click="handleStatus(row)" v-permission="'equipment:list:edit'">状�?/el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="设备名称" prop="equipmentName">
          <el-input v-model="form.equipmentName" />
        </el-form-item>
        <el-form-item label="设备编号" prop="equipmentNo">
          <el-input v-model="form.equipmentNo" />
        </el-form-item>
        <el-form-item label="设备分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择">
            <el-option v-for="c in categories.filter(i => i.id != null)" :key="c.id" :label="c.categoryName" :value="c.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="型号" prop="model">
          <el-input v-model="form.model" />
        </el-form-item>
        <el-form-item label="规格" prop="spec">
          <el-input v-model="form.spec" />
        </el-form-item>
        <el-form-item label="位置" prop="location">
          <el-input v-model="form.location" />
        </el-form-item>
        <el-form-item label="所属楼�?>
          <el-select v-model="form.buildingId" placeholder="请选择" clearable>
            <el-option v-for="b in buildings.filter(i => i.id != null)" :key="b.id" :label="b.buildingNo" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="安装日期">
          <el-date-picker v-model="form.installDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="维保到期�?>
          <el-date-picker v-model="form.warrantyEndDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="设备状�? prop="status">
          <el-select v-model="form.status">
            <el-option label="正常" :value="1" /><el-option label="故障" :value="2" /><el-option label="维修�? :value="3" /><el-option label="停用" :value="4" /><el-option label="报废" :value="5" />
          </el-select>
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
import { addEquipment, updateEquipment, deleteEquipment, getEquipmentPage, updateEquipmentStatus } from '@/api/equipment/equipment'
import { getCategoryPage } from '@/api/equipment/category'
import { getBuildingPage } from '@/api/community/building'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const categories = ref([])
const buildings = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, categoryId: '', status: '', equipmentName: '' })
const form = reactive({
  id: null, equipmentName: '', equipmentNo: '', categoryId: null, model: '', spec: '',
  location: '', buildingId: null, installDate: '', warrantyEndDate: '', status: 1, remark: ''
})

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑设备' : '新增设备')
const rules = {
  equipmentName: [{ required: true, message: '请输入设备名�?, trigger: 'blur' }],
  equipmentNo: [{ required: true, message: '请输入设备编�?, trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }],
  status: [{ required: true, message: '请选择状�?, trigger: 'change' }]
}

const statusType = (s) => ({ 0: 'success', 1: 'danger', 2: 'warning', 3: 'info' }[s] || 'info')
const statusText = (s) => ({ 0: '正常', 1: '故障', 2: '维修�?, 3: '报废' }[s] || '未知')

onMounted(async () => {
  fetchData()
  const catRes = await getCategoryPage({ pageNum: 1, pageSize: 100 })
  categories.value = catRes.data.records
  const bldRes = await getBuildingPage({ pageNum: 1, pageSize: 100 })
  buildings.value = bldRes.data.records
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getEquipmentPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { Object.assign(searchForm, { pageNum: 1, pageSize: 10, categoryId: '', status: '', equipmentName: '' }); fetchData() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, { ...row, installDate: row.installDate || row.purchaseDate || '', warrantyEndDate: row.warrantyEndDate || row.warrantyExpire || '' }); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateEquipment(form)
    else await addEquipment(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该设备吗�?, '提示', { type: 'warning' })
  try { await deleteEquipment(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ } finally { submitting.value = false }
}

async function handleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  await ElMessageBox.confirm('确定修改设备状态吗�?, '提示', { type: 'warning' })
  try { await updateEquipmentStatus({ id: row.id, status: newStatus }); ElMessage.success('状态修改成�?); fetchData() } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
