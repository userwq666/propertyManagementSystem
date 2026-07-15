<template>
  <div class="app-container">
    <div class="page-header">
      <h1>缴费通知管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>缴费通知列表</span>
          <el-button-group>
<el-button type="primary" @click="handleAdd" v-permission="['fee:notice:add']">
            <Plus /> 新增
          </el-button>
          <el-button type="danger" @click="handleBatchDelete" v-permission="['fee:notice:delete']">
            <Delete /> 批量删除
          </el-button>
          <el-button type="success" @click="handleExport" v-permission="['fee:notice:export']">
            <Download /> 导出
          </el-button>
          <el-button type="warning" @click="handleBatchSend" v-permission="['fee:notice:send']">
            <Send /> 批量发送
          </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="通知标题">
          <el-input v-model="queryParams.noticeTitle" placeholder="通知标题" clearable style="width: 200px" />
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
        <el-form-item label="业主/房屋">
          <el-input v-model="queryParams.ownerName" placeholder="业主姓名/房屋编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="发送状态">
          <el-select v-model="queryParams.sendStatus" placeholder="发送状态" clearable style="width: 160px">
            <el-option v-for="item in SEND_STATUS_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="发送时间">
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

      <!-- 缴费通知表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="noticeId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="noticeId" label="通知ID" width="80" align="center" />
        <el-table-column prop="noticeTitle" label="通知标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="itemName" label="缴费项目" min-width="120" show-overflow-tooltip />
        <el-table-column prop="applicableScopeLabel" label="适用范围" width="100" align="center" />
        <el-table-column prop="houseNames" label="指定房屋" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-tag v-if="scope.row.applicableScope === '2'" size="small" effect="plain" :max-width="180">
              {{ scope.row.houseNames }}
            </el-tag>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column prop="ownerNames" label="指定业主" min-width="120" show-overflow-tooltip>
          <template #default="scope">
            <el-tag v-if="scope.row.applicableScope === '3'" size="small" effect="plain" :max-width="180">
              {{ scope.row.ownerNames }}
            </el-tag>
            <span v-else>--</span>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="应缴金额(元)" width="120" align="right">
          <template #default="scope">
            {{ formatAmount(scope.row.amount) }}
          </template>
        </el-table-column>
        <el-table-column prop="dueDate" label="应缴日期" width="120" align="center" />
        <el-table-column prop="deadline" label="缴费截止日期" width="120" align="center" />
        <el-table-column prop="sendMethodLabels" label="发送方式" min-width="150">
          <template #default="scope">
            <el-tag v-for="method in scope.row.sendMethodLabels" :key="method" size="small" effect="plain">
              {{ method }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendStatus" label="发送状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getSendStatusType(scope.row.sendStatus)">
              {{ scope.row.sendStatusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="readStatus" label="阅读状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.readStatus === '1' ? 'success' : 'info'">
              {{ scope.row.readStatusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" align="center" />
        <el-table-column label="操作" align="center" width="380" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['fee:notice:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['fee:notice:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleSend(scope.row)"
              v-permission="['fee:notice:send']"
              :disabled="scope.row.sendStatus === '1'"
            >
              <Send /> 发送
            </el-button>
            <el-button size="small" type="info" @click="handleDetail(scope.row)">
              <View /> 详情
            </el-button>
            <el-button
              size="small"
              :type="scope.row.readStatus === '1' ? 'warning' : 'success'"
              @click="handleMarkRead(scope.row)"
            >
              <Read /> {{ scope.row.readStatus === '1' ? '标记未读' : '标记已读' }}
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

    <!-- 新增/编辑缴费通知弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="通知标题" prop="noticeTitle" :rules="[{ required: true, message: '请输入通知标题', trigger: 'blur' }]">
              <el-input v-model="form.noticeTitle" placeholder="请输入通知标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费项目" prop="itemId" :rules="[{ required: true, message: '请选择缴费项目', trigger: 'change' }]">
              <el-select v-model="form.itemId" placeholder="请选择缴费项目" style="width: 100%" filterable>
                <el-option
                  v-for="item in chargeItemOptions"
                  :key="item.itemId"
                  :label="item.itemName"
                  :value="item.itemId"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用范围" prop="applicableScope" :rules="[{ required: true, message: '请选择适用范围', trigger: 'change' }]">
              <el-radio-group v-model="form.applicableScope">
                <el-radio :label="'1'">全体</el-radio>
                <el-radio :label="'2'">指定房屋</el-radio>
                <el-radio :label="'3'">指定业主</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.applicableScope === '2'">
            <el-form-item label="指定房屋" prop="houseIds" :rules="[{ required: true, message: '请选择指定房屋', trigger: 'change' }]">
              <el-transfer
                v-model="form.houseIds"
                :data="availableHouses"
                :titles="['可选房屋', '已选房屋']"
                :button-texts="['添加', '移除']"
                filterable
                filter-placeholder="输入房屋编号/楼栋搜索"
                :render-content="renderHouseContent"
                @change="handleHouseTransferChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24" v-if="form.applicableScope === '3'">
            <el-form-item label="指定业主" prop="ownerIds" :rules="[{ required: true, message: '请选择指定业主', trigger: 'change' }]">
              <el-transfer
                v-model="form.ownerIds"
                :data="availableOwners"
                :titles="['可选业主', '已选业主']"
                :button-texts="['添加', '移除']"
                filterable
                filter-placeholder="输入业主姓名/电话搜索"
                :render-content="renderOwnerContent"
                @change="handleOwnerTransferChange"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应缴金额(元)" prop="amount" :rules="[
              { required: true, message: '请输入应缴金额', trigger: 'blur' },
              { type: 'number', min: 0.01, message: '应缴金额必须大于0', trigger: 'blur' }
            ]">
              <el-input-number v-model="form.amount" :min="0.01" :step="0.01" :precision="2" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="应缴日期" prop="dueDate" :rules="[{ required: true, message: '请选择应缴日期', trigger: 'change' }]">
              <el-date-picker v-model="form.dueDate" type="date" placeholder="请选择应缴日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="缴费截止日期" prop="deadline" :rules="[{ required: true, message: '请选择缴费截止日期', trigger: 'change' }]">
              <el-date-picker v-model="form.deadline" type="date" placeholder="请选择缴费截止日期" style="width: 100%" value-format="YYYY-MM-DD" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="发送方式" prop="sendMethods" :rules="[{ required: true, type: 'array', min: 1, message: '请至少选择一种发送方式', trigger: 'change' }]">
              <el-checkbox-group v-model="form.sendMethods">
                <el-checkbox
                  v-for="method in SEND_METHOD_OPTIONS"
                  :key="method.dictValue"
                  :label="method.dictValue"
                >
                  {{ method.dictLabel }}
                </el-checkbox>
              </el-checkbox-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="通知内容" prop="content" :rules="[{ required: true, message: '请输入通知内容', trigger: 'blur' }]">
              <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入通知内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 发送详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="'发送详情 - ' + detailNoticeTitle"
      width="1000px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-card>
        <el-form :model="detailQueryParams" :inline="true" class="search-form" label-width="80px" style="margin-bottom: 16px;">
          <el-form-item label="发送方式">
            <el-select v-model="detailQueryParams.sendMethod" placeholder="发送方式" clearable style="width: 160px">
              <el-option v-for="item in SEND_METHOD_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
            </el-select>
          </el-form-item>
          <el-form-item label="发送状态">
            <el-select v-model="detailQueryParams.sendStatus" placeholder="发送状态" clearable style="width: 160px">
              <el-option v-for="item in SEND_STATUS_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
            </el-select>
          </el-form-item>
          <el-form-item label="阅读状态">
            <el-select v-model="detailQueryParams.readStatus" placeholder="阅读状态" clearable style="width: 160px">
              <el-option v-for="item in READ_STATUS_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getSendDetailList">
              <Search /> 查询
            </el-button>
            <el-button @click="resetDetailQuery">
              <Refresh /> 重置
            </el-button>
          </el-form-item>
        </el-form>

        <el-table
          v-loading="detailLoading"
          :data="sendDetailData"
          row-key="sendId"
          border
          style="width: 100%"
        >
          <el-table-column prop="sendId" label="发送ID" width="80" align="center" />
          <el-table-column prop="receiveTypeLabel" label="接收类型" width="100" align="center" />
          <el-table-column prop="receiverName" label="接收对象" min-width="120" show-overflow-tooltip />
          <el-table-column prop="sendMethodLabel" label="发送方式" width="100" align="center" />
          <el-table-column prop="sendStatus" label="发送状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="getSendStatusType(scope.row.sendStatus)">
                {{ scope.row.sendStatusLabel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="readStatus" label="阅读状态" width="100" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.readStatus === '1' ? 'success' : 'info'">
                {{ scope.row.readStatusLabel }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="sendTime" label="发送时间" width="180" align="center" />
          <el-table-column prop="readTime" label="阅读时间" width="180" align="center" />
          <el-table-column prop="failReason" label="失败原因" min-width="150" show-overflow-tooltip />
        </el-table>

        <div class="pagination-container" style="margin-top: 16px;">
          <el-pagination
            v-model:current-page="detailQueryParams.pageNum"
            v-model:page-size="detailQueryParams.pageSize"
            :total="detailTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="getSendDetailList"
          />
        </div>
      </el-card>
    </el-dialog>

    <!-- 批量发送确认弹窗 -->
    <el-dialog
      v-model="batchSendDialogVisible"
      title="批量发送确认"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="batch-send-content">
        <p>确定要发送选中的 <strong>{{ selectionIds.length }}</strong> 条缴费通知吗？</p>
        <el-form :model="batchSendForm" label-width="80px" class="batch-send-form">
          <el-form-item label="发送方式">
            <el-checkbox-group v-model="batchSendForm.sendMethods">
              <el-checkbox
                v-for="method in SEND_METHOD_OPTIONS"
                :key="method.dictValue"
                :label="method.dictValue"
              >
                {{ method.dictLabel }}
              </el-checkbox>
            </el-checkbox-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="batchSendDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmBatchSend" :loading="batchSendLoading">
            确定发送
          </el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Search, Refresh, Edit, Delete, Send, View, Read
} from '@element-plus/icons-vue'
import {
  getNoticeList,
  getNoticeInfo,
  addNotice,
  updateNotice,
  deleteNotice,
  sendNotice,
  getSendDetail,
  markReadStatus,
  exportNotice,
  getChargeItemList,
  getHouseTree,
  getOwnerList
} from '@/api/fee/notice'
import { usePermission } from '@/hooks/usePermission'
import {
  FeeNoticeForm,
  FeeNoticeQuery,
  SendDetailQuery,
  SendNoticeData,
  MarkReadData,
  APPLICABLE_SCOPE_OPTIONS,
  SEND_METHOD_OPTIONS,
  SEND_STATUS_OPTIONS,
  READ_STATUS_OPTIONS
} from '@/types/fee/notice'
import type { ChargeItem } from '@/types/fee/item'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref<any[]>([])
const total = ref(0)
const selectionIds = ref<number[]>([])

const queryParams = reactive<FeeNoticeQuery>({
  pageNum: 1,
  pageSize: 10,
  noticeTitle: '',
  itemId: undefined,
  ownerName: '',
  sendStatus: '',
  beginTime: '',
  endTime: '',
  dateRange: []
})

// 收费项目下拉选项
const chargeItemOptions = ref<ChargeItem[]>([])

// 弹窗状态
const dialogVisible = ref(false)
const dialogTitle = ref('新增缴费通知')
const isAdd = ref(true)

const form = reactive<FeeNoticeForm>({
  noticeId: undefined,
  noticeTitle: '',
  itemId: undefined,
  applicableScope: '1',
  houseIds: [],
  ownerIds: [],
  amount: 0,
  dueDate: '',
  deadline: '',
  content: '',
  sendMethods: [],
  remark: ''
})

const rules = reactive({
  noticeTitle: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  itemId: [{ required: true, message: '请选择缴费项目', trigger: 'change' }],
  applicableScope: [{ required: true, message: '请选择适用范围', trigger: 'change' }],
  houseIds: [{ required: true, message: '请选择指定房屋', trigger: 'change' }],
  ownerIds: [{ required: true, message: '请选择指定业主', trigger: 'change' }],
  amount: [
    { required: true, message: '请输入应缴金额', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '应缴金额必须大于0', trigger: 'blur' }
  ],
  dueDate: [{ required: true, message: '请选择应缴日期', trigger: 'change' }],
  deadline: [{ required: true, message: '请选择缴费截止日期', trigger: 'change' }],
  sendMethods: [{ required: true, type: 'array', min: 1, message: '请至少选择一种发送方式', trigger: 'change' }],
  content: [{ required: true, message: '请输入通知内容', trigger: 'blur' }]
})

const formRef = ref()

// 监听适用范围变化
watch(() => form.applicableScope, (newVal) => {
  if (newVal === '1') {
    form.houseIds = []
    form.ownerIds = []
  }
  nextTick(() => {
    formRef.value?.validateField('houseIds')
    formRef.value?.validateField('ownerIds')
  })
})

// 房屋/业主选择器数据
const availableHouses = ref<any[]>([])
const availableOwners = ref<any[]>([])
const houseTreeData = ref<any[]>([])
const houseTreeProps = {
  children: 'children',
  label: 'label',
  value: 'value'
}

// 发送详情弹窗
const detailDialogVisible = ref(false)
const detailNoticeTitle = ref('')
const detailLoading = ref(false)
const sendDetailData = ref<any[]>([])
const detailTotal = ref(0)
const currentDetailNoticeId = ref<number | null>(null)

const detailQueryParams = reactive<SendDetailQuery>({
  pageNum: 1,
  pageSize: 10,
  noticeId: 0,
  sendMethod: '',
  sendStatus: '',
  readStatus: ''
})

// 批量发送弹窗
const batchSendDialogVisible = ref(false)
const batchSendLoading = ref(false)
const batchSendForm = reactive({
  sendMethods: [] as string[]
})

// 获取收费项目列表
const loadChargeItems = async () => {
  try {
    const res = await getChargeItemList({ pageNum: 1, pageSize: 1000 })
    chargeItemOptions.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取收费项目失败:', error)
  }
}

// 获取房屋树数据（用于转移框显示）
const loadHouseTree = async () => {
  try {
    const res = await getHouseTree({})
    const treeData = res.data || res || []
    houseTreeData.value = treeData
    // 转换为 transfer 组件需要的扁平结构
    availableHouses.value = flattenHouseTree(treeData)
  } catch (error) {
    console.error('获取房屋树失败:', error)
  }
}

// 扁平化房屋树
const flattenHouseTree = (tree: any[]): any[] => {
  const result: any[] = []
  const traverse = (nodes: any[], prefix = '') => {
    nodes.forEach(node => {
      if (node.children && node.children.length > 0) {
        traverse(node.children, `${prefix}${node.label}/`)
      } else {
        result.push({
          key: node.value || node.houseId,
          label: `${prefix}${node.label}`,
          disabled: false
        })
      }
    })
  }
  traverse(tree)
  return result
}

// 获取业主列表
const loadOwnerList = async () => {
  try {
    const res = await getOwnerList({ pageNum: 1, pageSize: 1000 })
    const list = res.rows || res.data?.rows || res.data || []
    availableOwners.value = list.map((item: any) => ({
      key: item.ownerId,
      label: `${item.ownerName} (${item.ownerPhone})`,
      disabled: false
    }))
  } catch (error) {
    console.error('获取业主列表失败:', error)
  }
}

// 房屋转移框渲染内容
const renderHouseContent = (h: any, option: any) => {
  return h('span', { style: 'display: flex; align-items: center;' }, [
    h('span', option.label)
  ])
}

// 业主转移框渲染内容
const renderOwnerContent = (h: any, option: any) => {
  return h('span', { style: 'display: flex; align-items: center;' }, [
    h('span', option.label)
  ])
}

// 房屋选择变化
const handleHouseTransferChange = (value: number[], direction: string, movedKeys: number[]) => {
  form.houseIds = value
  nextTick(() => {
    formRef.value?.validateField('houseIds')
  })
}

// 业主选择变化
const handleOwnerTransferChange = (value: number[], direction: string, movedKeys: number[]) => {
  form.ownerIds = value
  nextTick(() => {
    formRef.value?.validateField('ownerIds')
  })
}

// 适用范围变化监听
watch(() => form.applicableScope, (value) => {
  if (value === '1') {
    form.houseIds = []
    form.ownerIds = []
  }
  nextTick(() => {
    formRef.value?.validateField('houseIds')
    formRef.value?.validateField('ownerIds')
  })
})

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

    const res = await getNoticeList(params)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
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
  queryParams.noticeTitle = ''
  queryParams.itemId = undefined
  queryParams.ownerName = ''
  queryParams.sendStatus = ''
  queryParams.dateRange = []
  queryParams.beginTime = ''
  queryParams.endTime = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection: any[]) => {
  selectionIds.value = selection.map(item => item.noticeId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增缴费通知'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row: any) => {
  isAdd.value = false
  dialogTitle.value = '修改缴费通知'
  resetForm()
  try {
    const res = await getNoticeInfo(row.noticeId)
    const data = res.data || res
    form.noticeId = data.noticeId
    form.noticeTitle = data.noticeTitle
    form.itemId = data.itemId
    form.applicableScope = data.applicableScope
    form.houseIds = data.houseIds || []
    form.ownerIds = data.ownerIds || []
    form.amount = data.amount
    form.dueDate = data.dueDate
    form.deadline = data.deadline
    form.content = data.content
    form.sendMethods = data.sendMethods ? data.sendMethods.split(',') : []
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 删除
const handleDelete = (row: any) => {
  const noticeIds = row.noticeId ? row.noticeId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除通知ID为"${noticeIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(noticeIds)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectionIds.value.length === 0) {
    ElMessage.warning('请选择要删除的缴费通知')
    return
  }
  handleDelete({ noticeId: selectionIds.value.join(',') })
}

// 发送通知（单条）
const handleSend = async (row: any) => {
  try {
    await sendNotice({ noticeIds: [row.noticeId] })
    ElMessage.success('发送成功')
    getList()
  } catch (error) {
    console.error('发送失败:', error)
  }
}

// 批量发送
const handleBatchSend = () => {
  if (selectionIds.value.length === 0) {
    ElMessage.warning('请选择要发送的缴费通知')
    return
  }
  batchSendForm.sendMethods = []
  batchSendDialogVisible.value = true
}

const confirmBatchSend = async () => {
  if (batchSendForm.sendMethods.length === 0) {
    ElMessage.warning('请至少选择一种发送方式')
    return
  }
  batchSendLoading.value = true
  try {
    await sendNotice({
      noticeIds: selectionIds.value,
      sendMethods: batchSendForm.sendMethods
    })
    ElMessage.success('批量发送成功')
    batchSendDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('批量发送失败:', error)
  } finally {
    batchSendLoading.value = false
  }
}

// 查看发送详情
const handleDetail = async (row: any) => {
  currentDetailNoticeId.value = row.noticeId
  detailNoticeTitle.value = row.noticeTitle
  detailQueryParams.noticeId = row.noticeId
  detailQueryParams.pageNum = 1
  detailQueryParams.sendMethod = ''
  detailQueryParams.sendStatus = ''
  detailQueryParams.readStatus = ''
  detailDialogVisible.value = true
  await getSendDetailList()
}

const getSendDetailList = async () => {
  detailLoading.value = true
  try {
    const res = await getSendDetail(detailQueryParams)
    sendDetailData.value = res.rows || res.data?.rows || []
    detailTotal.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取发送详情失败:', error)
  } finally {
    detailLoading.value = false
  }
}

const resetDetailQuery = () => {
  detailQueryParams.sendMethod = ''
  detailQueryParams.sendStatus = ''
  detailQueryParams.readStatus = ''
  getSendDetailList()
}

// 标记已读/未读
const handleMarkRead = async (row: any) => {
  const newStatus = row.readStatus === '1' ? '0' : '1'
  const action = newStatus === '1' ? '标记已读' : '标记未读'
  try {
    await markReadStatus({ noticeIds: [row.noticeId], readStatus: newStatus })
    ElMessage.success(`${action}成功`)
    getList()
  } catch (error) {
    console.error(`${action}失败:`, error)
  }
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

    const res = await exportNotice(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `缴费通知数据_${new Date().getTime()}.xlsx`
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

// 关闭弹窗
const closeDialog = (done: Function) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.noticeId = undefined
  form.noticeTitle = ''
  form.itemId = undefined
  form.applicableScope = '1'
  form.houseIds = []
  form.ownerIds = []
  form.amount = 0
  form.dueDate = ''
  form.deadline = ''
  form.content = ''
  form.sendMethods = []
  form.remark = ''
  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (isAdd.value) {
      await addNotice(form)
      ElMessage.success('新增成功')
    } else {
      await updateNotice(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 格式化金额
const formatAmount = (value: number): string => {
  if (value === null || value === undefined || value === '') return '0.00'
  return Number(value).toFixed(2)
}

// 获取发送状态Tag类型
const getSendStatusType = (status: string): 'success' | 'warning' | 'danger' | 'info' => {
  switch (status) {
    case '1': return 'success'
    case '2': return 'danger'
    default: return 'warning'
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([
    loadChargeItems(),
    loadHouseTree(),
    loadOwnerList()
  ])
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
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

/* 树形选择器宽度适配 */
:deep(.el-tree-select__input) {
  width: 100%;
}

:deep(.el-tree-select__tags) {
  max-width: 100%;
}

/* 转移框样式 */
:deep(.el-transfer) {
  width: 100%;
}

:deep(.el-transfer__list) {
  height: 280px;
}

:deep(.el-transfer-panel__body) {
  height: 240px !important;
}

/* 批量发送弹窗 */
.batch-send-content {
  padding: 10px 0;
  p {
    margin: 0 0 16px 0;
    color: #606266;
  }
}

.batch-send-form {
  margin-top: 16px;
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .dialog-form .el-col {
    :nth-child(n) {
      flex: 0 0 100%;
      max-width: 100%;
    }
  }
}
</style>