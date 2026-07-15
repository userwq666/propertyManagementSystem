<template>
  <div class="app-container">
    <div class="page-header">
      <h1>缴费记录管理</h1>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-20">
      <el-col :span="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">总金额</div>
            <div class="stat-value">{{ formatAmount(statistics.totalAmount) }}元</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card success">
          <div class="stat-content">
            <div class="stat-label">已缴金额</div>
            <div class="stat-value">{{ formatAmount(statistics.paidAmount) }}元</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card warning">
          <div class="stat-content">
            <div class="stat-label">退费金额</div>
            <div class="stat-value">{{ formatAmount(statistics.refundAmount) }}元</div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="never" class="stat-card danger">
          <div class="stat-content">
            <div class="stat-label">欠费金额</div>
            <div class="stat-value">{{ formatAmount(statistics.arrearsAmount) }}元</div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费记录列表</span>
          <el-button-group>
            <el-button type="success" @click="handleExport" v-permission="['fee:record:export']">
              <Download /> 导出
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="业主/房屋">
          <el-input v-model="queryParams.ownerName" placeholder="业主姓名/房屋编号" clearable style="width: 220px" />
        </el-form-item>
        <el-form-item label="缴费项目">
          <el-select v-model="queryParams.itemId" placeholder="请选择缴费项目" clearable style="width: 200px" filterable>
            <el-option
              v-for="item in chargeItemOptions"
              :key="item.itemId"
              :label="item.itemName"
              :value="item.itemId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费方式">
          <el-select v-model="queryParams.payMethod" placeholder="缴费方式" clearable style="width: 160px">
            <el-option v-for="item in PAY_METHOD_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="queryParams.payStatus" placeholder="缴费状态" clearable style="width: 160px">
            <el-option v-for="item in PAY_STATUS_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="收费周期">
          <el-select v-model="queryParams.payPeriod" placeholder="收费周期" clearable style="width: 160px">
            <el-option v-for="item in CHARGE_CYCLE_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="缴费时间">
          <el-date-picker
            v-model="queryParams.dateRange"
            type="daterange"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 300px"
            unlink-panels
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <Search /> 查询
          </el-button>
          <el-button @click="resetQuery">
            <Refresh /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 缴费记录表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="recordId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="recordId" label="记录ID" width="80" align="center" />
        <el-table-column prop="recordNo" label="缴费单号" min-width="160" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="业主姓名" width="100" align="center" />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="130" align="center" />
        <el-table-column prop="houseNo" label="房屋编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="itemName" label="缴费项目" min-width="120" show-overflow-tooltip />
        <el-table-column prop="chargeCycleLabel" label="收费周期" width="90" align="center" />
        <el-table-column prop="payableAmount" label="应缴金额(元)" width="110" align="right">
          <template #default="scope">
            {{ formatAmount(scope.row.payableAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="paidAmount" label="实缴金额(元)" width="110" align="right">
          <template #default="scope">
            {{ formatAmount(scope.row.paidAmount) }}
          </template>
        </el-table-column>
        <el-table-column prop="refundAmount" label="退费金额(元)" width="110" align="right">
          <template #default="scope">
            <span v-if="scope.row.refundAmount > 0" style="color: #E6A23C">{{ formatAmount(scope.row.refundAmount) }}</span>
            <span v-else style="color: #909399">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="arrearsAmount" label="欠费金额(元)" width="110" align="right">
          <template #default="scope">
            <span v-if="scope.row.arrearsAmount > 0" style="color: #F56C6C">{{ formatAmount(scope.row.arrearsAmount) }}</span>
            <span v-else style="color: #67C23A">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="payMethodLabel" label="缴费方式" width="100" align="center" />
        <el-table-column prop="payStatus" label="缴费状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getPayStatusType(scope.row.payStatus)">
              {{ scope.row.payStatusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="refundStatus" label="退费状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getRefundStatusType(scope.row.refundStatus)">
              {{ scope.row.refundStatusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="payTime" label="缴费时间" width="180" align="center" />
        <el-table-column prop="payPeriod" label="收费周期" min-width="120" show-overflow-tooltip />
        <el-table-column label="操作" align="center" width="220" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleDetail(scope.row)"
              v-permission="['fee:record:detail']"
            >
              <View /> 详情
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleRefund(scope.row)"
              v-permission="['fee:record:refund']"
              :disabled="scope.row.paidAmount <= 0 || scope.row.refundStatus === '2'"
            >
              <Money /> 退费
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="'缴费记录详情 - ' + detailRecordNo"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="detailForm" label-width="120px" class="detail-form" disabled>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="缴费单号">
              <el-input v-model="detailForm.recordNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主姓名">
              <el-input v-model="detailForm.ownerName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主电话">
              <el-input v-model="detailForm.ownerPhone" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="房屋编号">
              <el-input v-model="detailForm.houseNo" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="楼栋/单元/房间">
              <el-input v-model="detailForm.buildingUnitRoom" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费项目">
              <el-input v-model="detailForm.itemName" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费周期">
              <el-input v-model="detailForm.chargeCycleLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应缴金额(元)">
              <el-input v-model="detailForm.payableAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实缴金额(元)">
              <el-input v-model="detailForm.paidAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退费金额(元)">
              <el-input v-model="detailForm.refundAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="欠费金额(元)">
              <el-input v-model="detailForm.arrearsAmount" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费方式">
              <el-input v-model="detailForm.payMethodLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费状态">
              <el-input v-model="detailForm.payStatusLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退费状态">
              <el-input v-model="detailForm.refundStatusLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费时间">
              <el-input v-model="detailForm.payTime" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费周期">
              <el-input v-model="detailForm.payPeriod" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退费时间">
              <el-input v-model="detailForm.refundTime" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退费方式">
              <el-input v-model="detailForm.refundMethodLabel" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="退费原因">
              <el-input v-model="detailForm.refundReason" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="detailForm.remark" type="textarea" :rows="2" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建人">
              <el-input v-model="detailForm.createBy" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input v-model="detailForm.createTime" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-dialog>

    <!-- 退费弹窗 -->
    <el-dialog
      v-model="refundDialogVisible"
      title="退费申请"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeRefundDialog"
      destroy-on-close
    >
      <el-form ref="refundFormRef" :model="refundForm" :rules="refundRules" label-width="100px" class="dialog-form">
        <el-form-item label="缴费单号" prop="recordNo">
          <el-input v-model="refundForm.recordNo" disabled />
        </el-form-item>
        <el-form-item label="业主/房屋" prop="ownerHouse">
          <el-input v-model="refundForm.ownerHouse" disabled />
        </el-form-item>
        <el-form-item label="缴费项目" prop="itemName">
          <el-input v-model="refundForm.itemName" disabled />
        </el-form-item>
        <el-form-item label="实缴金额(元)" prop="paidAmount">
          <el-input v-model="refundForm.paidAmount" disabled />
        </el-form-item>
        <el-form-item label="已退金额(元)" prop="refundedAmount">
          <el-input v-model="refundForm.refundedAmount" disabled />
        </el-form-item>
        <el-form-item label="可退金额(元)" prop="refundableAmount">
          <el-input v-model="refundForm.refundableAmount" disabled />
        </el-form-item>
        <el-form-item label="退费金额(元)" prop="refundAmount" :rules="[
          { required: true, message: '请输入退费金额', trigger: 'blur' },
          { type: 'number', min: 0.01, message: '退费金额必须大于0', trigger: 'blur' }
        ]">
          <el-input-number
            v-model="refundForm.refundAmount"
            :min="0.01"
            :max="refundForm.refundableAmount"
            :step="0.01"
            :precision="2"
            style="width: 100%"
            @change="validateRefundAmount"
          />
          <template #append>元</template>
        </el-form-item>
        <el-form-item label="退费原因" prop="refundReason" :rules="[{ required: true, message: '请选择退费原因', trigger: 'change' }]">
          <el-select v-model="refundForm.refundReason" placeholder="请选择退费原因" style="width: 100%">
            <el-option v-for="item in REFUND_REASON_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="退费方式" prop="refundMethod" :rules="[{ required: true, message: '请选择退费方式', trigger: 'change' }]">
          <el-select v-model="refundForm.refundMethod" placeholder="请选择退费方式" style="width: 100%">
            <el-option v-for="item in REFUND_METHOD_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="退费备注" prop="refundRemark">
          <el-input v-model="refundForm.refundRemark" type="textarea" :rows="3" placeholder="请输入退费备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeRefundDialog">取消</el-button>
          <el-button type="primary" @click="submitRefund" :loading="refundLoading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Download, Search, Refresh, View, Money
} from '@element-plus/icons-vue'
import {
  getFeeRecordList,
  getFeeRecordInfo,
  refundFeeRecord,
  exportFeeRecord,
  getFeeRecordStatistics,
  getChargeItemListForSelect
} from '@/api/fee/record'
import { usePermission } from '@/hooks/usePermission'
import {
  FeeRecordQuery,
  FeeRecordStatistics,
  RefundForm,
  RefundFormExtend,
  PAY_METHOD_OPTIONS,
  PAY_STATUS_OPTIONS,
  REFUND_STATUS_OPTIONS,
  REFUND_METHOD_OPTIONS,
  REFUND_REASON_OPTIONS,
  CHARGE_CYCLE_OPTIONS,
  getPayStatusType,
  getRefundStatusType,
  formatAmount,
  type ChargeItem
} from '@/types/fee/record'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const selectionIds = ref<number[]>([])

// 统计数据
const statistics = reactive<FeeRecordStatistics>({
  totalAmount: 0,
  paidAmount: 0,
  refundAmount: 0,
  arrearsAmount: 0
})

// 缴费项目下拉选项
const chargeItemOptions = ref<ChargeItem[]>([])

// 查询参数
const queryParams = reactive<FeeRecordQuery>({
  pageNum: 1,
  pageSize: 10,
  ownerName: '',
  itemId: undefined,
  payMethod: '',
  payStatus: '',
  payPeriod: '',
  beginTime: '',
  endTime: '',
  dateRange: []
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailRecordNo = ref('')
const detailForm = reactive({
  recordNo: '',
  ownerName: '',
  ownerPhone: '',
  houseNo: '',
  buildingUnitRoom: '',
  itemName: '',
  chargeCycleLabel: '',
  payableAmount: '',
  paidAmount: '',
  refundAmount: '',
  arrearsAmount: '',
  payMethodLabel: '',
  payStatusLabel: '',
  refundStatusLabel: '',
  payTime: '',
  payPeriod: '',
  refundTime: '',
  refundMethodLabel: '',
  refundReason: '',
  remark: '',
  createBy: '',
  createTime: ''
})

// 退费弹窗
const refundDialogVisible = ref(false)
const refundLoading = ref(false)
const currentRefundRecord = ref<any>(null)

const refundForm = reactive<RefundFormExtend>({
  recordId: 0,
  recordNo: '',
  ownerHouse: '',
  itemName: '',
  paidAmount: '',
  refundedAmount: '',
  refundableAmount: '',
  refundAmount: 0,
  refundReason: '',
  refundMethod: '',
  refundRemark: ''
})

const refundRules = reactive({
  recordNo: [],
  ownerHouse: [],
  itemName: [],
  paidAmount: [],
  refundedAmount: [],
  refundableAmount: [],
  refundAmount: [
    { required: true, message: '请输入退费金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '退费金额必须大于0', trigger: 'blur' }
  ],
  refundReason: [{ required: true, message: '请选择退费原因', trigger: 'change' }],
  refundMethod: [{ required: true, message: '请选择退费方式', trigger: 'change' }],
  refundRemark: []
})

const refundFormRef = ref()

// 获取缴费项目列表
const loadChargeItems = async () => {
  try {
    const res = await getChargeItemListForSelect({ pageNum: 1, pageSize: 1000 })
    chargeItemOptions.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取收费项目失败:', error)
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const params = { ...queryParams }
    if (params.dateRange && params.dateRange.length === 2) {
      params.beginTime = params.dateRange[0]
      params.endTime = params.dateRange[1]
    }
    delete params.dateRange
    delete params.pageNum
    delete params.pageSize

    const res = await getFeeRecordStatistics(params)
    const data = res.data || res
    statistics.totalAmount = data.totalAmount || 0
    statistics.paidAmount = data.paidAmount || 0
    statistics.refundAmount = data.refundAmount || 0
    statistics.arrearsAmount = data.arrearsAmount || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取列表
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (params.dateRange && params.dateRange.length === 2) {
      params.beginTime = params.dateRange[0]
      params.endTime = params.dateRange[1]
    }
    delete params.dateRange

    const res = await getFeeRecordList(params)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取缴费记录列表失败:', error)
  } finally {
    loading.value = false
  }
  // 同时获取统计数据
  await getStatistics()
}

// 日期范围变化
const handleDateChange = (value: string[]) => {
  queryParams.dateRange = value
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.ownerName = ''
  queryParams.itemId = undefined
  queryParams.payMethod = ''
  queryParams.payStatus = ''
  queryParams.payPeriod = ''
  queryParams.dateRange = []
  queryParams.beginTime = ''
  queryParams.endTime = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection: any[]) => {
  selectionIds.value = selection.map(item => item.recordId)
}

// 查看详情
const handleDetail = async (row: any) => {
  try {
    const res = await getFeeRecordInfo(row.recordId)
    const data = res.data || res
    detailRecordNo.value = data.recordNo || ''
    detailForm.recordNo = data.recordNo || ''
    detailForm.ownerName = data.ownerName || ''
    detailForm.ownerPhone = data.ownerPhone || ''
    detailForm.houseNo = data.houseNo || ''
    detailForm.buildingUnitRoom = `${data.buildingName || ''} ${data.unitNo || ''}单元 ${data.roomNo || ''}室`.trim()
    detailForm.itemName = data.itemName || ''
    detailForm.chargeCycleLabel = data.chargeCycleLabel || ''
    detailForm.payableAmount = formatAmount(data.payableAmount)
    detailForm.paidAmount = formatAmount(data.paidAmount)
    detailForm.refundAmount = formatAmount(data.refundAmount)
    detailForm.arrearsAmount = formatAmount(data.arrearsAmount)
    detailForm.payMethodLabel = data.payMethodLabel || ''
    detailForm.payStatusLabel = data.payStatusLabel || ''
    detailForm.refundStatusLabel = data.refundStatusLabel || ''
    detailForm.payTime = data.payTime || ''
    detailForm.payPeriod = data.payPeriod || ''
    detailForm.refundTime = data.refundTime || ''
    detailForm.refundMethodLabel = data.refundMethodLabel || ''
    detailForm.refundReason = data.refundReason || ''
    detailForm.remark = data.remark || ''
    detailForm.createBy = data.createBy || ''
    detailForm.createTime = data.createTime || ''
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
    ElMessage.error('获取详情失败')
  }
}

// 退费操作
const handleRefund = (row: any) => {
  if (row.paidAmount <= 0) {
    ElMessage.warning('该记录无实缴金额，不能退费')
    return
  }
  if (row.refundStatus === '2') {
    ElMessage.warning('该记录已退费，不能重复退费')
    return
  }

  currentRefundRecord.value = row
  refundForm.recordId = row.recordId
  refundForm.recordNo = row.recordNo
  refundForm.refundAmount = 0
  refundForm.refundReason = ''
  refundForm.refundMethod = ''
  refundForm.refundRemark = ''

  // 计算可退金额
  const refunded = row.refundAmount || 0
  const refundable = row.paidAmount - refunded

  // 设置显示用字段
  refundForm.ownerHouse = `${row.ownerName || ''} / ${row.houseNo || ''}`
  refundForm.itemName = row.itemName || ''
  refundForm.paidAmount = formatAmount(row.paidAmount)
  refundForm.refundedAmount = formatAmount(refunded)
  refundForm.refundableAmount = formatAmount(refundable)

  nextTick(() => {
    if (refundFormRef.value) {
      refundFormRef.value.clearValidate()
    }
  })

  refundDialogVisible.value = true
}

// 验证退费金额不超过可退金额
const validateRefundAmount = () => {
  const refunded = currentRefundRecord.value?.refundAmount || 0
  const refundable = (currentRefundRecord.value?.paidAmount || 0) - refunded
  if (refundForm.refundAmount > refundable) {
    refundFormRef.value?.validateField('refundAmount')
  }
}

// 提交退费
const submitRefund = async () => {
  if (!refundFormRef.value) return
  try {
    await refundFormRef.value.validate()

    const refunded = currentRefundRecord.value?.refundAmount || 0
    const refundable = (currentRefundRecord.value?.paidAmount || 0) - refunded
    if (refundForm.refundAmount > refundable) {
      ElMessage.error(`退费金额不能大于可退金额（${formatAmount(refundable)}元）`)
      return
    }

    refundLoading.value = true
    await refundFeeRecord({
      recordId: refundForm.recordId,
      refundAmount: refundForm.refundAmount,
      refundReason: refundForm.refundReason,
      refundMethod: refundForm.refundMethod,
      refundRemark: refundForm.refundRemark
    })

    ElMessage.success('退费申请提交成功')
    refundDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('退费失败:', error)
  } finally {
    refundLoading.value = false
  }
}

// 关闭退费弹窗
const closeRefundDialog = (done: Function) => {
  if (refundFormRef.value) {
    refundFormRef.value.resetFields()
  }
  currentRefundRecord.value = null
  done()
}

// 导出
const handleExport = async () => {
  try {
    loading.value = true
    const params = { ...queryParams }
    if (params.dateRange && params.dateRange.length === 2) {
      params.beginTime = params.dateRange[0]
      params.endTime = params.dateRange[1]
    }
    delete params.dateRange
    delete params.pageNum
    delete params.pageSize

    const res = await exportFeeRecord(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `缴费记录数据_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

// 初始化
onMounted(async () => {
  await loadChargeItems()
  getList()
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
  h1 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.search-form {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #fafafa;
  border-radius: 4px;
  border: 1px solid #ebeef5;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.dialog-form {
  padding: 10px 0;
}

.detail-form {
  padding: 10px 0;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.mb-20 {
  margin-bottom: 20px;
}

/* 统计卡片 */
.stat-card {
  .el-card__body {
    padding: 16px 20px;
  }
}

.stat-card.success {
  border-left: 4px solid #67C23A;
}

.stat-card.warning {
  border-left: 4px solid #E6A23C;
}

.stat-card.danger {
  border-left: 4px solid #F56C6C;
}

.stat-content {
  .stat-label {
    font-size: 14px;
    color: #909399;
    margin-bottom: 4px;
  }
  .stat-value {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

/* 表格工具栏按钮组 */
.el-button-group {
  .el-button {
    margin-left: 8px;
    &:first-child {
      margin-left: 0;
    }
  }
}

/* 禁用状态按钮样式 */
:deep(.el-button--disabled) {
  opacity: 0.5;
}
</style>