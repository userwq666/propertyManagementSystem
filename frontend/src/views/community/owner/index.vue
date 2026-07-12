<template>
  <div class="owner-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>业主管理</span>
          <el-button type="primary" @click="handleAdd">新增业主</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="业主姓名">
          <el-input v-model="queryParams.name" placeholder="请输入业主姓名" clearable />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="name" label="业主姓名" />
        <el-table-column prop="idCard" label="身份证号" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" />
        <el-table-column prop="emergencyContact" label="紧急联系人" />
        <el-table-column prop="emergencyPhone" label="紧急联系电话" />
        <el-table-column prop="checkInTime" label="入住时间" />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" link @click="handleBindUser(row)">绑定用户</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="业主姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入业主姓名" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="联系电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="紧急联系人" prop="emergencyContact">
          <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
        </el-form-item>
        <el-form-item label="紧急联系电话" prop="emergencyPhone">
          <el-input v-model="form.emergencyPhone" placeholder="请输入紧急联系电话" />
        </el-form-item>
        <el-form-item label="入住时间" prop="checkInTime">
          <el-date-picker v-model="form.checkInTime" type="datetime" placeholder="请选择入住时间" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="bindDialogVisible" title="绑定用户" width="400px">
      <el-form label-width="80px">
        <el-form-item label="用户ID">
          <el-input-number v-model="bindForm.userId" :min="1" placeholder="请输入用户ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="bindDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleBindSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOwnerPage, addOwner, updateOwner, deleteOwner, bindUser } from '@/api/owner'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const bindDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  name: '',
  phone: ''
})

const form = reactive({
  id: null,
  userId: null,
  name: '',
  idCard: '',
  phone: '',
  emergencyContact: '',
  emergencyPhone: '',
  checkInTime: null
})

const bindForm = reactive({
  ownerId: null,
  userId: null
})

const rules = {
  name: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getOwnerPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.name = ''
  queryParams.phone = ''
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增业主'
  Object.assign(form, { id: null, userId: null, name: '', idCard: '', phone: '', emergencyContact: '', emergencyPhone: '', checkInTime: null })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑业主'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateOwner(form)
    ElMessage.success('编辑成功')
  } else {
    await addOwner(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleBindUser = (row) => {
  bindForm.ownerId = row.id
  bindForm.userId = row.userId
  bindDialogVisible.value = true
}

const handleBindSubmit = async () => {
  await bindUser(bindForm.ownerId, bindForm.userId)
  ElMessage.success('绑定成功')
  bindDialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该业主吗？', '提示', { type: 'warning' })
  await deleteOwner(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.owner-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
