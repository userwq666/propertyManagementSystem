<template>
  <div class="inspection-plan">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>巡检计划管理</span>
          <el-button type="primary" @click="handleAdd">新增计划</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="待执行" :value="0" />
            <el-option label="执行中" :value="1" />
            <el-option label="已完成" :value="2" />
            <el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="planName" label="计划名称" />
        <el-table-column prop="planType" label="类型" width="100">
          <template #default="{ row }">
            {{ row.planType === 0 ? '手动创建' : '周期生成' }}
          </template>
        </el-table-column>
        <el-table-column prop="planDate" label="计划日期" />
        <el-table-column prop="startDate" label="开始日期" />
        <el-table-column prop="endDate" label="结束日期" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status === 0" size="small" type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handleStatusChange(row, 1)">开始执行</el-button>
            <el-button v-if="row.status === 1" size="small" type="warning" @click="handleStatusChange(row, 2)">完成</el-button>
            <el-button v-if="row.status === 0" size="small" type="danger" @click="handleStatusChange(row, 3)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next, jumper"
        :total="total" @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageInspectionPlan, updateInspectionPlanStatus } from '../../../../api/inspectionPlan'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ status: null })

const getStatusType = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0: '待执行', 1: '执行中', 2: '已完成', 3: '已取消' }[s] || '未知')

const loadData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value, status: searchForm.value.status }
  const res = await pageInspectionPlan(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => router.push('/inspection/plan/add')
const handleDetail = (row) => router.push(`/inspection/plan/add?id=${row.id}`)
const handleEdit = (row) => router.push(`/inspection/plan/add?id=${row.id}`)

const handleStatusChange = async (row, status) => {
  const text = getStatusText(status)
  await ElMessageBox.confirm(`确认将计划状态更改为"${text}"？`, '提示')
  await updateInspectionPlanStatus({ id: row.id, status })
  ElMessage.success('状态更新成功')
  loadData()
}

onMounted(() => loadData())
</script>

<style scoped>
.inspection-plan { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>