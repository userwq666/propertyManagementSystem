<template>
  <div class="app-container">
    <div class="page-header">
      <h1>账单管理</h1>
      <div class="header-actions">
        <el-button type="primary" @click="handleGenerate">
          <el-icon><Plus /></el-icon> 生成账单
        </el-button>
        <el-button @click="handleMarkOverdue">
          <el-icon><WarningFilled /></el-icon> 标记逾期
        </el-button>
        <el-button @click="refreshData">
          <el-icon><Refresh /></el-icon> 刷新
        </el-button>
      </div>
    </div>

    <el-row :gutter="16" class="mb-4">
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-label">账单总数</div>
          <div class="stat-value">{{ statistics.totalCount || 0 }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card success">
          <div class="stat-label">已缴金额</div>
          <div class="stat-value">&yen;{{ formatAmount(statistics.paidAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card warning">
          <div class="stat-label">待缴金额</div>
          <div class="stat-value">&yen;{{ formatAmount(statistics.unpaidAmount) }}</div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card danger">
          <div class="stat-label">逾期金额</div>
          <div class="stat-value">&yen;{{ formatAmount(statistics.overdueAmount) }}</div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <el-form :model="queryParams" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="缴费状态">
          <el-select v-model="queryParams.payStatus" placeholder="全部" clearable style="width:140px" @change="handleQuery">
            <el-option label="待缴费" :value="0" />
            <el-option label="已缴费" :value="1" />
            <el-option label="部分缴费" :value="2" />
            <el-option label="已逾期" :value="3" />
          </el-select>
        </el-form-item>
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
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="feeNo" label="账单编号" width="160" />
        <el-table-column prop="ownerId" label="业主ID" width="80" />
        <el-table-column prop="houseId" label="房屋ID" width="80" />
        <el-table-column prop="itemId" label="费用项目ID" width="100" />
        <el-table-column prop="amount" label="应收金额" width="110" align="right">
          <template #default="{ row }">&yen;{{ formatAmount(row.amount) }}</template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="已缴金额" width="110" align="right">
          <template #default="{ row }">&yen;{{ formatAmount(row.paidAmount) }}</template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)" size="small">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="起" width="100" />
        <el-table-column prop="endDate" label="止" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status==='UNPAID'||row.status==='OVERDUE'" type="primary" size="small" link @click="handleConfirmPay(row)">确认缴费</el-button>
            <span v-else style="color:#909399">--</span>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="handleQuery" @current-change="handleQuery" />
      </div>
    </el-card>

    <el-dialog v-model="payDialog.visible" title="确认缴费" width="400px">
      <el-form label-width="100px">
        <el-form-item label="账单编号">{{ payDialog.feeNo }}</el-form-item>
        <el-form-item label="应收金额">&yen;{{ formatAmount(payDialog.amount) }}</el-form-item>
        <el-form-item label="缴费方式" required>
          <el-select v-model="payDialog.payWay" placeholder="选择缴费方式" style="width:100%">
            <el-option label="现金" value="CASH" />
            <el-option label="银行转账" value="BANK_TRANSFER" />
            <el-option label="微信支付" value="WECHAT" />
            <el-option label="支付宝" value="ALIPAY" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payDialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="payDialog.loading" @click="submitPay">确认缴费</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, RefreshRight, Plus, WarningFilled } from '@element-plus/icons-vue'
import { getFeeRecordList, getFeeRecordStatistics, confirmPay, markOverdue } from '@/api/fee/record'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const queryParams = reactive({ pageNum:1, pageSize:10, payStatus:null, ownerId:null, houseId:null })
const statistics = reactive({ totalCount:0, paidAmount:0, unpaidAmount:0, overdueAmount:0 })
const payDialog = reactive({ visible:false, loading:false, id:null, feeNo:'', amount:0, payWay:'' })

const statusType = (s) => ({ UNPAID:'info', PAID:'success', PARTIAL_PAID:'warning', OVERDUE:'danger', CANCELLED:'' })[s]||'info'
const statusLabel = (s) => ({ UNPAID:'待缴费', PAID:'已缴费', PARTIAL_PAID:'部分缴费', OVERDUE:'已逾期', CANCELLED:'已取消' })[s]||s
const formatAmount = (v) => (Number(v)||0).toFixed(2)

const loadData = async () => {
  loading.value = true
  try { const res = await getFeeRecordList(queryParams); tableData.value = res.data.records||[]; total.value = res.data.total||0 } catch { tableData.value = [] }
  loading.value = false
}
const loadStatistics = async () => {
  try { const res = await getFeeRecordStatistics(); if(res.data) Object.assign(statistics, res.data) } catch {}
}
const handleQuery = () => { queryParams.pageNum=1; loadData() }
const resetQuery = () => { Object.assign(queryParams,{ pageNum:1,pageSize:10,payStatus:null,ownerId:null,houseId:null }); loadData() }
const refreshData = () => { loadData(); loadStatistics() }
const handleConfirmPay = (row) => { payDialog.id=row.id; payDialog.feeNo=row.feeNo; payDialog.amount=row.amount; payDialog.payWay=''; payDialog.visible=true }
const submitPay = async () => {
  if(!payDialog.payWay){ ElMessage.warning('请选择缴费方式'); return }
  payDialog.loading=true
  try { await confirmPay(payDialog.id, payDialog.payWay); ElMessage.success('缴费确认成功'); payDialog.visible=false; refreshData() } catch {}
  payDialog.loading=false
}
const handleGenerate = () => ElMessage.info('账单生成请通过后端管理接口操作')
const handleMarkOverdue = async () => {
  try { await ElMessageBox.confirm('确定将所有过期未缴账单标记为逾期吗？','提示',{ type:'warning' }); await markOverdue(); ElMessage.success('逾期标记完成'); refreshData() } catch {}
}
onMounted(refreshData)
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;
  h1 { font-size:20px; font-weight:600; color:#303133; margin:0; }
  .header-actions { display:flex; gap:8px; }
}
.mb-4 { margin-bottom:16px; }
.stat-card { text-align:center;
  .stat-label { font-size:13px; color:#909399; margin-bottom:8px; }
  .stat-value { font-size:22px; font-weight:600; color:#303133; }
  &.success .stat-value { color:#67c23a; } &.warning .stat-value { color:#e6a23c; } &.danger .stat-value { color:#f56c6c; }
}
.search-form { margin-bottom:16px; }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>