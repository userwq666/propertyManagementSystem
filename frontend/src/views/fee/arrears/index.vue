<template>
  <div class="app-container">
    <div class="page-header">
      <h1>欠费管理</h1>
      <el-button @click="refreshData"><el-icon><Refresh /></el-icon> 刷新</el-button>
    </div>

    <el-row :gutter="16" class="mb-4">
      <el-col :span="8">
        <el-card shadow="never" class="stat-card danger">
          <div class="stat-label">逾期笔数</div>
          <div class="stat-value">{{ overdueCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card warning">
          <div class="stat-label">待缴笔数</div>
          <div class="stat-value">{{ unpaidCount }}</div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="stat-card danger">
          <div class="stat-label">欠费总额</div>
          <div class="stat-value">&yen;{{ formatAmount(totalArrears) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-table v-loading="loading" :data="tableData" border stripe style="width:100%" :default-sort="{ prop:'createTime', order:'descending' }">
        <el-table-column prop="feeNo" label="账单编号" width="160" />
        <el-table-column prop="amount" label="应收金额" width="120" align="right">
          <template #default="{ row }">&yen;{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴金额" width="120" align="right">
          <template #default="{ row }">&yen;{{ formatAmount(row.paidAmount || 0) }}</template>
        </el-table-column>
        <el-table-column label="欠费金额" width="120" align="right">
          <template #default="{ row }">&yen;{{ formatAmount((row.amount||0)-(row.paidAmount||0)) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="row.status==='OVERDUE'?'danger':'warning'" size="small">{{ row.status==='OVERDUE'?'已逾期':'待缴费' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="费用周期" width="200">
          <template #default="{ row }">{{ row.startDate }} ~ {{ row.endDate }}</template>
        </el-table-column>
        <el-table-column prop="createTime" label="生成时间" width="160" />
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { Refresh } from '@element-plus/icons-vue'
import { getFeeRecordList, getFeeRecordStatistics } from '@/api/fee/record'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum:1, pageSize:10, payStatus:0 })

const formatAmount = (v) => (Number(v)||0).toFixed(2)
const overdueCount = computed(() => tableData.value.filter(r=>r.status==='OVERDUE').length)
const unpaidCount = computed(() => tableData.value.filter(r=>r.status==='UNPAID').length)
const totalArrears = computed(() => tableData.value.reduce((s,r)=>s+((r.amount||0)-(r.paidAmount||0)),0))

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeeRecordList({ pageNum:queryParams.pageNum, pageSize:queryParams.pageSize, payStatus:null })
    const all = res.data.records||[]
    tableData.value = all.filter(r => r.status==='UNPAID' || r.status==='OVERDUE')
    total.value = tableData.value.length
  } catch { tableData.value = [] }
  loading.value = false
}
const refreshData = () => { queryParams.pageNum=1; loadData() }
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
  &.warning .stat-value { color:#e6a23c; } &.danger .stat-value { color:#f56c6c; }
}
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>