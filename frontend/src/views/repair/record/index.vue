<template>
  <div class="repair-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>报修记录管理</span>
          <el-button type="primary" @click="handleAdd">新增报修</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="业主ID">
          <el-input v-model="searchForm.ownerId" placeholder="请输入业主ID" clearable />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="searchForm.houseId" placeholder="请输入房屋ID" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待处理" :value="0" />
            <el-option label="处理中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已驳回" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ownerId" label="业主ID" />
        <el-table-column prop="houseId" label="房屋ID" />
        <el-table-column prop="repairType" label="报修类型" />
        <el-table-column prop="content" label="故障描述" show-overflow-tooltip />
        <el-table-column prop="status" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleUser" label="处理人" />
        <el-table-column prop="rating" label="评分">
          <template #default="{ row }">
            <el-rate v-if="row.rating" v-model="row.rating" disabled />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="报修时间" />
        <el-table-column label="操作" width="300">
          <template #default="{ row }">
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" @click="handleAccept(row)">接单</el-button>
            <el-button v-if="row.status === 1" size="small" type="success" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleReject(row)">驳回</el-button>
            <el-button v-if="row.status === 2 && !row.rating" size="small" type="primary" @click="handleRate(row)">评价</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper" :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入业主ID" />
        </el-form-item>
        <el-form-item label="房屋ID" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请输入房屋ID" />
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType" placeholder="请选择报修类型">
            <el-option label="水电" value="水电" />
            <el-option label="门窗" value="门窗" />
            <el-option label="公共设备" value="公共设备" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="故障描述" prop="content">
          <el-input v-model="form.content" type="textarea" rows="4" placeholder="请输入故障描述" />
        </el-form-item>
        <el-form-item label="图片URL">
          <el-input v-model="form.imgUrl" placeholder="请输入图片URL" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="completeDialogVisible" title="完成报修" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="completeForm.handleResult" type="textarea" rows="4" placeholder="请输入处理结果" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="rateDialogVisible" title="评价报修" width="400px">
      <el-form :model="rateForm" label-width="100px">
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" show-text :texts="['很差', '较差', '一般', '较好', '很好']" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="rateDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleRateSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageRepairRecord, addRepairRecord, updateRepairRecord, deleteRepairRecord, updateRepairRecordStatus, updateRepairRecordRating } from '@/api/repairRecord'

const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const completeDialogVisible = ref(false)
const rateDialogVisible = ref(false)
const currentRow = ref(null)

const searchForm = ref({
  ownerId: '',
  houseId: '',
  status: null
})

const form = ref({
  ownerId: '',
  houseId: '',
  repairType: '',
  content: '',
  imgUrl: ''
})

const completeForm = ref({
  handleResult: ''
})

const rateForm = ref({
  rating: 5
})

const rules = {
  ownerId: [{ required: true, message: '请输入业主ID', trigger: 'blur' }],
  houseId: [{ required: true, message: '请输入房屋ID', trigger: 'blur' }],
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入故障描述', trigger: 'blur' }]
}

const getStatusType = (status) => {
  const types = { 0: 'warning', 1: 'primary', 2: 'success', 3: 'danger' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '待处理', 1: '处理中', 2: '已完成', 3: '已驳回' }
  return texts[status] || '未知'
}

const fetchData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value
  }
  if (searchForm.value.ownerId) params.ownerId = searchForm.value.ownerId
  if (searchForm.value.houseId) params.houseId = searchForm.value.houseId
  if (searchForm.value.status !== null) params.status = searchForm.value.status
  const res = await pageRepairRecord(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => {
  dialogTitle.value = '新增报修'
  form.value = { ownerId: '', houseId: '', repairType: '', content: '', imgUrl: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑报修'
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该报修记录吗？', '提示', { type: 'warning' })
  await deleteRepairRecord(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

const handleAccept = async (row) => {
  await updateRepairRecordStatus({ id: row.id, status: 1, handleUser: '当前用户' })
  ElMessage.success('接单成功')
  fetchData()
}

const handleComplete = (row) => {
  currentRow.value = row
  completeForm.value.handleResult = ''
  completeDialogVisible.value = true
}

const handleCompleteSubmit = async () => {
  await updateRepairRecordStatus({
    id: currentRow.value.id,
    status: 2,
    handleResult: completeForm.value.handleResult
  })
  ElMessage.success('完成成功')
  completeDialogVisible.value = false
  fetchData()
}

const handleReject = async (row) => {
  await ElMessageBox.confirm('确认驳回该报修记录吗？', '提示', { type: 'warning' })
  await updateRepairRecordStatus({ id: row.id, status: 3 })
  ElMessage.success('驳回成功')
  fetchData()
}

const handleRate = (row) => {
  currentRow.value = row
  rateForm.value.rating = 5
  rateDialogVisible.value = true
}

const handleRateSubmit = async () => {
  await updateRepairRecordRating({ id: currentRow.value.id, rating: rateForm.value.rating })
  ElMessage.success('评价成功')
  rateDialogVisible.value = false
  fetchData()
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.value.id) {
    await updateRepairRecord(form.value)
  } else {
    await addRepairRecord(form.value)
  }
  ElMessage.success('操作成功')
  dialogVisible.value = false
  fetchData()
}

const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { ownerId: '', houseId: '', status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => { fetchData() }

onMounted(() => { fetchData() })
</script>

<style scoped>
.card-header { display: flex; justify-content: space-between; align-items: center; }
.repair-record { padding: 20px; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>
