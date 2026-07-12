<template>
  <div class="parking-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>车位管理</span>
          <el-button type="primary" @click="handleAdd">新增车位</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="车位编号">
          <el-input v-model="queryParams.parkingNo" placeholder="请输入车位编号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
            <el-option label="空闲" :value="0" />
            <el-option label="已租赁" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="parkingNo" label="车位编号" />
        <el-table-column prop="parkingType" label="车位类型">
          <template #default="{ row }">
            {{ row.parkingType === 0 ? '固定车位' : '临时车位' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'warning'">{{ row.status === 0 ? '空闲' : '已租赁' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="expireTime" label="到期时间" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" type="success" link @click="handleRent(row)">租赁</el-button>
            <el-button v-if="row.status === 1" type="warning" link @click="handleRelease(row)">释放</el-button>
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
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="车位编号" prop="parkingNo">
          <el-input v-model="form.parkingNo" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="车位类型" prop="parkingType">
          <el-select v-model="form.parkingType">
            <el-option label="固定车位" :value="0" />
            <el-option label="临时车位" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="rentDialogVisible" title="租赁车位" width="400px">
      <el-form label-width="100px">
        <el-form-item label="业主ID">
          <el-input-number v-model="rentForm.ownerId" :min="1" placeholder="请输入业主ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rentDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRentSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getParkingPage, addParking, updateParking, deleteParking, updateParkingStatus } from '@/api/parking'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const rentDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  parkingNo: '',
  status: null
})

const form = reactive({
  id: null,
  parkingNo: '',
  parkingType: 0,
  status: 0,
  ownerId: null,
  expireTime: null,
  remark: ''
})

const rentForm = reactive({
  parkingId: null,
  ownerId: null
})

const rules = {
  parkingNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getParkingPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.parkingNo = ''
  queryParams.status = null
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
  dialogTitle.value = '新增车位'
  Object.assign(form, { id: null, parkingNo: '', parkingType: 0, status: 0, ownerId: null, expireTime: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑车位'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateParking(form)
    ElMessage.success('编辑成功')
  } else {
    await addParking(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleRent = (row) => {
  rentForm.parkingId = row.id
  rentForm.ownerId = null
  rentDialogVisible.value = true
}

const handleRentSubmit = async () => {
  await updateParkingStatus(rentForm.parkingId, 1, rentForm.ownerId)
  ElMessage.success('租赁成功')
  rentDialogVisible.value = false
  loadData()
}

const handleRelease = async (row) => {
  await ElMessageBox.confirm('确认释放该车位吗？', '提示', { type: 'warning' })
  await updateParkingStatus(row.id, 0, null)
  ElMessage.success('释放成功')
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该车位吗？', '提示', { type: 'warning' })
  await deleteParking(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.parking-container {
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
