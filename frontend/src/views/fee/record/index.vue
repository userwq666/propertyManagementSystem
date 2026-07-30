<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="业主">
        <el-select v-model="searchForm.ownerId" placeholder="请选择业主" clearable style="width:180px">
          <el-option v-for="o in ownerList.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="房屋">
        <el-select v-model="searchForm.houseId" placeholder="请选择房屋" clearable style="width:180px">
          <el-option v-for="h in houseList.filter(i => i.id != null)" :key="h.id" :label="h.roomNo" :value="h.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width:150px">
          <el-option label="未缴费" :value="0" />
          <el-option label="已缴费" :value="1" />
          <el-option label="逾期" :value="2" />
          <el-option label="减免" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleGenerate" v-permission="'fee:record:add'">批量生成账单</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="feeNo" label="账单编号" width="150" />
        <el-table-column prop="ownerName" label="业主" width="100" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="itemName" label="收费项目" width="120" />
        <el-table-column prop="amount" label="应收金额" width="100" />
        <el-table-column prop="paidAmount" label="已缴金额" width="100" />
        <el-table-column prop="discountAmount" label="优惠金额" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="缴费方式" width="100">
          <template #default="{ row }">{{ payText(row.payType) }}</template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" width="160" />
        <el-table-column prop="startDate" label="开始日期" width="110" />
        <el-table-column prop="endDate" label="结束日期" width="110" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status===0" type="success" size="small" @click="handlePay(row)" v-permission="'fee:record:edit'">缴费</el-button>
            <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <!-- 缴费弹窗 -->
    <el-dialog title="确认缴费" v-model="payDialogVisible" width="400px">
      <el-form :model="payForm" label-width="80px">
        <el-form-item label="缴费方式">
          <el-select v-model="payForm.payWay" style="width:100%"><el-option label="现金" value="CASH" /><el-option label="微信" value="WECHAT" /><el-option label="支付宝" value="ALIPAY" /><el-option label="银行转账" value="BANK" /></el-select>
        </el-form-item>
        <el-form-item label="实缴金额">
          <el-input-number v-model="payForm.paidAmount" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="payDialogVisible=false">取消</el-button>
          <el-button type="primary" @click="submitPay">确定缴费</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeeRecordPage, payFeeRecord, generateFeeRecords } from '@/api/fee/record'
import { getOwnerPage } from '@/api/community/owner'
import { getHousePage } from '@/api/community/house'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const payDialogVisible = ref(false)
const ownerList = ref([])
const houseList = ref([])
const currentPayRow = ref(null)

const searchForm = reactive({ pageNum: 1, pageSize: 10, ownerId: '', houseId: '', status: '' })
const payForm = reactive({ payWay: 'WECHAT', paidAmount: 0 })

const statusTag = (s) => ({ 0: 'info', 1: 'success', 2: 'danger', 3: 'warning' }[s] || 'info')
const statusText = (s) => ({ 0: '未缴费', 1: '已缴费', 2: '逾期', 3: '减免' }[s] || '')
const payText = (t) => ({ 'CASH': '现金', 'WECHAT': '微信', 'ALIPAY': '支付宝', 'BANK': '银行转账' }[t] || '')

onMounted(async () => {
  fetchData()
  const oRes = await getOwnerPage({ pageNum: 1, pageSize: 200 })
  ownerList.value = oRes.data.records
  const hRes = await getHousePage({ pageNum: 1, pageSize: 200 })
  houseList.value = hRes.data.records
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeeRecordPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.ownerId = ''; searchForm.houseId = ''; searchForm.status = ''; handleSearch() }

function handlePay(row) {
  currentPayRow.value = row
  payForm.payWay = 'WECHAT'
  payForm.paidAmount = row.amount || 0
  payDialogVisible.value = true
}

async function submitPay() {
  try {
    await payFeeRecord(currentPayRow.value.id, payForm.payWay)
    ElMessage.success('缴费成功')
    payDialogVisible.value = false
    fetchData()
  } catch (e) {}
}

function handleDetail(row) {
  var detail = [
    '账单编号: ' + (row.feeNo || ''),
    '业主: ' + (row.ownerName || ''),
    '房间号: ' + (row.roomNo || ''),
    '收费项目: ' + (row.itemName || ''),
    '应收金额: ' + (row.amount || 0),
    '已缴金额: ' + (row.paidAmount || 0),
    '状态: ' + statusText(row.status)
  ].join('\n')
  ElMessageBox.alert(detail, '账单详情')
}

async function handleGenerate() {
  await ElMessageBox.confirm('确定要批量生成账单吗？', '提示', { type: 'warning' })
  try {
    await generateFeeRecords([])
    ElMessage.success('账单生成成功')
    fetchData()
  } catch (e) {}
}
</script>