<template>
  <div class="inspection-record">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>巡检记录管理</span>
          <el-button type="primary" @click="handleAdd">新增记录</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="设备">
          <el-select v-model="searchForm.equipmentId" placeholder="请选择设备" clearable>
            <el-option v-for="item in equipmentList" :key="item.id" :label="item.equipmentName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="巡检结果">
          <el-select v-model="searchForm.result" placeholder="请选择结果" clearable>
            <el-option label="正常" :value="0" />
            <el-option label="一般异常" :value="1" />
            <el-option label="严重异常" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="equipmentId" label="设备" width="150">
          <template #default="{ row }">
            {{ getEquipmentName(row.equipmentId) }}
          </template>
        </el-table-column>
        <el-table-column prop="inspectorId" label="巡检人员" width="120" />
        <el-table-column prop="inspectTime" label="巡检时间" />
        <el-table-column prop="result" label="巡检结果" width="120">
          <template #default="{ row }">
            <el-tag :type="getResultType(row.result)">{{ getResultText(row.result) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="faultDesc" label="异常描述" show-overflow-tooltip />
        <el-table-column prop="budget" label="预估费用" width="120" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
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
import { pageEquipment } from '../../../../api/equipment'
import { pageInspectionRecord, deleteInspectionRecord } from '../../../../api/inspectionRecord'

const router = useRouter()
const tableData = ref([])
const equipmentList = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ equipmentId: null, result: null })

const getEquipmentName = (id) => {
  const equipment = equipmentList.value.find(item => item.id === id)
  return equipment ? equipment.equipmentName : ''
}

const getResultType = (r) => ({ 0: 'success', 1: 'warning', 2: 'danger' }[r] || 'info')
const getResultText = (r) => ({ 0: '正常', 1: '一般异常', 2: '严重异常' }[r] || '未知')

const loadEquipment = async () => {
  const res = await pageEquipment({ pageNum: 1, pageSize: 1000 })
  equipmentList.value = res.data.records
}

const loadData = async () => {
  const params = {
    pageNum: currentPage.value,
    pageSize: pageSize.value,
    equipmentId: searchForm.value.equipmentId,
    result: searchForm.value.result
  }
  const res = await pageInspectionRecord(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => { currentPage.value = 1; loadData() }
const handleReset = () => { searchForm.value = { equipmentId: null, result: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; loadData() }
const handleCurrentChange = () => loadData()

const handleAdd = () => router.push('/inspection/record/add')
const handleDetail = (row) => router.push(`/inspection/record/add?id=${row.id}`)
const handleEdit = (row) => router.push(`/inspection/record/add?id=${row.id}`)

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该记录？', '提示')
  await deleteInspectionRecord(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadEquipment()
  loadData()
})
</script>

<style scoped>
.inspection-record { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>