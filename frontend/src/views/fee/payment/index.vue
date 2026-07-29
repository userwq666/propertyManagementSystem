<template>
  <div class="app-container">
    <div class="page-header">
      <h1>缴费记录</h1>
      <el-button @click="refreshData"><el-icon><Refresh /></el-icon> 刷新</el-button>
    </div>

    <el-row :gutter="16" class="mb-4">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card success">
          <div class="stat-label">已缴笔数</div>
          <div class="stat-value">{{ total }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">缴费总额</div>
          <div class="stat-value">&yen;{{ formatAmount(totalPaid) }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">本月缴费</div>
          <div class="stat-value">&yen;{{ formatAmount(monthPaid) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-form :model="queryParams" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="业主ID">
          <el-input v-model="queryParams.ownerId" placeholder="业主ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item label="房屋ID">
          <el-input v-model="queryParams.houseId" placeholder="房屋ID" clearable style="width:140px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe style="width:100%">
        <el-table-column prop="feeNo" label="账单编号" width="160" />
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column prop="houseId" label="房屋ID" width="80" />
        <el-table-column prop="amount" label="金额" width="110" align="right">
          <template #default="{ row }">&yen;{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="payType" label="缴费方式" width="100" align="center" />
        <el-table-column prop="payTime" label="缴费时间" width="160" />
        <el-table-column prop="startDate" label="费用周期" width="200">
          <template #default="{ row }">{{ row.startDate }} ~ {{ row.endDate }}</template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="120" show-overflow-tooltip />
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="handleQuery" @current-change="handleQuery" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Search, Refresh, RefreshRight } from '@element-plus/icons-vue'
import { getFeeRecordList, getFeeRecordStatistics } from '@/api/fee/record'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum:1, pageSize:10, payStatus:1, ownerId:null, houseId:null })

const formatAmount = (v) => (Number(v)||0).toFixed(2)
const totalPaid = computed(() => tableData.value.reduce((s,r)=>s+(Number(r.paidAmount)||Number(r.amount)||0),0))
const monthPaid = computed(() => {
  const now = new Date(); const m = `${now.getFullYear()}-${String(now.getMonth()+1).padStart(2,'0')}`
  return tableData.value.filter(r => r.payTime && r.payTime.startsWith(m)).reduce((s,r)=>s+(Number(r.paidAmount)||Number(r.amount)||0),0)
})

const loadData = async () => {
  loading.value = true
  try { const res = await getFeeRecordList(queryParams); tableData.value = res.data.records||[]; total.value = res.data.total||0 } catch { tableData.value = [] }
  loading.value = false
}
const handleQuery = () => { queryParams.pageNum=1; loadData() }
const resetQuery = () => { Object.assign(queryParams,{ pageNum:1,pageSize:10,payStatus:1,ownerId:null,houseId:null }); loadData() }
const refreshData = () => loadData()
onMounted(refreshData)
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;
  h1 { font-size:20px; font-weight:600; color:#303133; margin:0; }
}
.mb-4 { margin-bottom:16px; }
.stat-card { text-align:center;
  .stat-label { font-size:13px; color:#909399; margin-bottom:8px; }
  .stat-value { font-size:22px; font-weight:600; color:#303133; }
  &.success .stat-value { color:#67c23a; }
}
.search-form { margin-bottom:16px; }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>