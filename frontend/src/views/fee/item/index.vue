<template>
  <div class="fee-item">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>收费项目管理</span>
          <el-button type="primary" @click="handleAdd">新增收费项目</el-button>
        </div>
      </template>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="itemName" label="项目名称" />
        <el-table-column prop="price" label="单价" />
        <el-table-column prop="cycleType" label="收费周期">
          <template #default="{ row }">
            {{ row.cycleType === 1 ? '月' : row.cycleType === 2 ? '季' : '年' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-switch v-model="row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(row)" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="form.itemName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="单价" prop="price">
          <el-input-number v-model="form.price" :min="0.01" :precision="2" />
        </el-form-item>
        <el-form-item label="收费周期" prop="cycleType">
          <el-select v-model="form.cycleType" placeholder="请选择收费周期">
            <el-option label="月" :value="1" />
            <el-option label="季" :value="2" />
            <el-option label="年" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
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
import { pageFeeItem, addFeeItem, updateFeeItem, deleteFeeItem, updateFeeItemStatus } from '@/api/feeItem'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = ref({
  itemName: '',
  price: 0,
  cycleType: 1,
  remark: ''
})

const rules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  cycleType: [{ required: true, message: '请选择收费周期', trigger: 'change' }]
}

const fetchData = async () => {
  const res = await pageFeeItem({ pageNum: currentPage.value, pageSize: pageSize.value })
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => {
  dialogTitle.value = '新增收费项目'
  form.value = { itemName: '', price: 0, cycleType: 1, remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑收费项目'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该收费项目吗？', '提示', { type: 'warning' })
  await deleteFeeItem(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleStatusChange = async (row) => {
  await updateFeeItemStatus(row.id, row.status)
  ElMessage.success('状态更新成功')
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.value.id) {
    await updateFeeItem(form.value)
  } else {
    await addFeeItem(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  fetchData()
}

const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.fee-item { padding: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>