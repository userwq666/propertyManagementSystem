<template>
  <div class="equipment-category">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备分类管理</span>
          <el-button type="primary" @click="handleAdd">新增分类</el-button>
        </div>
      </template>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="categoryName" label="分类名称" />
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="sortOrder" label="排序" width="100" />
        <el-table-column prop="isDefault" label="是否预设" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.isDefault === 1" type="warning">预设</el-tag>
            <el-tag v-else type="info">自定义</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.isDefault === 0" size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sortOrder" :min="0" />
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
import {
  listEquipmentCategory,
  addEquipmentCategory,
  updateEquipmentCategory,
  deleteEquipmentCategory
} from '../../../api/equipmentCategory'

const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const isEdit = ref(false)

const form = ref({
  categoryName: '',
  description: '',
  sortOrder: 0
})

const rules = {
  categoryName: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await listEquipmentCategory()
  tableData.value = res.data
}

const handleAdd = () => {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  form.value = { categoryName: '', description: '', sortOrder: 0 }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateEquipmentCategory(form.value)
    ElMessage.success('更新成功')
  } else {
    await addEquipmentCategory(form.value)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示')
  await deleteEquipmentCategory(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.equipment-category { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>