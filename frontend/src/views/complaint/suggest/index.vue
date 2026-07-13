<template>
  <div class="complaint-suggest">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉建议管理</span>
          <el-button type="primary" @click="handleAdd">新增投诉建议</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="投诉" value="投诉" />
            <el-option label="建议" value="建议" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待受理" :value="0" />
            <el-option label="已受理" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="已完成" :value="3" />
            <el-option label="已评价" :value="4" />
            <el-option label="已驳回" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handleUser" label="处理人" />
        <el-table-column prop="rating" label="评分" width="150">
          <template #default="{ row }">
            <el-rate v-if="row.rating" v-model="row.rating" disabled />
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="提交时间" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="warning" @click="handleAccept(row)">受理</el-button>
            <el-button v-if="row.status === 1 || row.status === 2" size="small" type="success" @click="handleComplete(row)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleReject(row)">驳回</el-button>
            <el-button v-if="row.status === 3 && !row.rating" size="small" type="primary" @click="handleRate(row)">评价</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>

    <!-- 完成弹窗 -->
    <el-dialog v-model="completeDialogVisible" title="完成处理" width="500px">
      <el-form :model="completeForm" label-width="100px">
        <el-form-item label="处理结果">
          <el-input v-model="completeForm.handleResult" type="textarea" rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="completeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleCompleteSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 评价弹窗 -->
    <el-dialog v-model="rateDialogVisible" title="评价" width="400px">
      <el-form :model="rateForm" label-width="100px">
        <el-form-item label="评分">
          <el-rate v-model="rateForm.rating" show-text :texts="['很差','较差','一般','较好','很好']" />
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
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  pageComplaintSuggest, deleteComplaintSuggest,
  updateComplaintSuggestStatus, updateComplaintSuggestRating
} from '@/api/complaintSuggest'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)
const completeDialogVisible = ref(false)
const rateDialogVisible = ref(false)
const currentRow = ref(null)

const searchForm = ref({ type: null, status: null })
const completeForm = ref({ handleResult: '' })
const rateForm = ref({ rating: 5 })

const getStatusType = (s) => ({ 0:'warning', 1:'primary', 2:'', 3:'success', 4:'success', 5:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'待受理', 1:'已受理', 2:'处理中', 3:'已完成', 4:'已评价', 5:'已驳回' }[s] || '未知')

const fetchData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value }
  if (searchForm.value.type) params.type = searchForm.value.type
  if (searchForm.value.status !== null) params.status = searchForm.value.status
  const res = await pageComplaintSuggest(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => router.push('/complaint/suggest/add')
const handleDetail = (row) => router.push(`/complaint/suggest/${row.id}`)
const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { type: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => fetchData()

const handleAccept = async (row) => {
  await ElMessageBox.confirm('确认受理该投诉建议？', '提示')
  await updateComplaintSuggestStatus({ id: row.id, status: 1 })
  ElMessage.success('受理成功')
  fetchData()
}

const handleComplete = (row) => {
  currentRow.value = row
  completeForm.value.handleResult = ''
  completeDialogVisible.value = true
}

const handleCompleteSubmit = async () => {
  await updateComplaintSuggestStatus({
    id: currentRow.value.id,
    status: 3,
    handleResult: completeForm.value.handleResult
  })
  ElMessage.success('完成成功')
  completeDialogVisible.value = false
  fetchData()
}

const handleReject = async (row) => {
  await ElMessageBox.confirm('确认驳回该投诉建议？', '提示')
  await updateComplaintSuggestStatus({ id: row.id, status: 5 })
  ElMessage.success('驳回成功')
  fetchData()
}

const handleRate = (row) => {
  currentRow.value = row
  rateForm.value.rating = 5
  rateDialogVisible.value = true
}

const handleRateSubmit = async () => {
  await updateComplaintSuggestRating({ id: currentRow.value.id, rating: rateForm.value.rating })
  ElMessage.success('评价成功')
  rateDialogVisible.value = false
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该投诉建议？', '提示')
  await deleteComplaintSuggest(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.complaint-suggest { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>