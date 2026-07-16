<template>
  <div class="app-container">
    <div class="page-header">
      <h1>投诉建议管理</h1>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待受理</div>
            <div class="stat-value warning">{{ statistics.pendingCount }}</div>
          </div>
          <el-icon class="stat-icon warning"><bell /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">处理中</div>
            <div class="stat-value primary">{{ statistics.processingCount }}</div>
          </div>
          <el-icon class="stat-icon primary"><loading /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">已完成</div>
            <div class="stat-value success">{{ statistics.completedCount }}</div>
          </div>
          <el-icon class="stat-icon success"><circle-check /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">平均满意度</div>
            <div class="stat-value info">{{ statistics.avgSatisfaction || '-' }}</div>
          </div>
          <el-icon class="stat-icon info"><star /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉建议列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['complaint:suggestion:add']">
              <plus /> 新增
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入投诉标题" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="投诉类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择投诉类型" clearable style="width: 180px">
            <el-option v-for="item in complaintTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择投诉状态" clearable style="width: 180px">
            <el-option v-for="item in complaintStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉人" prop="complainantName">
          <el-input v-model="queryParams.complainantName" placeholder="请输入投诉人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="投诉时间" prop="beginTime">
          <el-date-picker
            v-model="dateRange"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 320px"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="处理人" prop="handlerName">
          <el-input v-model="queryParams.handlerName" placeholder="请输入处理人" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <search /> 查询
          </el-button>
          <el-button @click="resetQuery">
            <refresh /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 列表表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="id"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="complaintNo" label="投诉编号" width="160" align="center" show-overflow-tooltip />
        <el-table-column prop="title" label="投诉标题" min-width="180" show-overflow-tooltip />
        <el-table-column prop="type" label="投诉类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="complaintTypeColorMap[scope.row.type] || ''" effect="dark">
              {{ getDictLabel(complaintTypeOptions, scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="complainantName" label="投诉人" width="100" align="center" />
        <el-table-column prop="complainantPhone" label="投诉电话" width="130" align="center" />
        <el-table-column prop="createTime" label="投诉时间" width="180" align="center" />
        <el-table-column prop="handlerName" label="处理人" width="100" align="center">
          <template #default="scope">
            {{ scope.row.handlerName || '-' }}
          </template>
        </el-table-column>
        <el-table-column prop="status" label="投诉状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="complaintStatusColorMap[scope.row.status] || ''" effect="dark">
              {{ getDictLabel(complaintStatusOptions, scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="satisfactionScore" label="满意度" width="160" align="center">
          <template #default="scope">
            <el-rate v-if="scope.row.satisfactionScore" v-model="scope.row.satisfactionScore" disabled show-score text-color="#ff9900" />
            <span v-else class="no-evaluate">未评价</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="300" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleDetail(scope.row)" v-permission="['complaint:suggestion:list']">详情</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="success" link @click="handleAccept(scope.row)" v-if="canAccept(scope.row)" v-permission="['complaint:suggestion:reply']">受理</el-button>
            <el-button size="small" type="warning" link @click="handleReply(scope.row)" v-if="canReply(scope.row)" v-permission="['complaint:suggestion:reply']">处理</el-button>
            <el-button size="small" type="info" link @click="handleEvaluate(scope.row)" v-if="canEvaluate(scope.row)" v-permission="['complaint:suggestion:evaluate']">评价</el-button>
            <el-divider direction="vertical" v-if="canClose(scope.row)" />
            <el-button size="small" type="danger" link @click="handleClose(scope.row)" v-if="canClose(scope.row)" v-permission="['complaint:suggestion:reply']">关闭</el-button>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="addDialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeAddDialog"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px" class="dialog-form">
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入投诉标题" />
        </el-form-item>
        <el-form-item label="投诉类型" prop="type">
          <el-select v-model="addForm.type" placeholder="请选择投诉类型" style="width: 100%">
            <el-option v-for="item in complaintTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="投诉位置" prop="houseId">
          <el-tree-select
            v-model="addForm.houseId"
            :props="houseTreeProps"
            :data="houseTreeData"
            placeholder="请选择投诉位置"
            style="width: 100%"
            check-strictly
          />
        </el-form-item>
        <el-form-item label="投诉描述" prop="content">
          <el-input v-model="addForm.content" type="textarea" placeholder="请详细描述投诉内容" :rows="4" />
        </el-form-item>
        <el-form-item label="图片上传" prop="images">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleImageChange"
            :on-remove="handleImageRemove"
            :file-list="addForm.imageList"
            list-type="picture-card"
            :limit="5"
            :on-exceed="handleExceed"
            accept="image/*"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="联系电话" prop="complainantPhone">
          <el-input v-model="addForm.complainantPhone" placeholder="请输入联系电话" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 受理弹窗 -->
    <el-dialog
      v-model="acceptDialogVisible"
      title="受理投诉"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="acceptFormRef" :model="acceptForm" :rules="acceptRules" label-width="100px" class="dialog-form">
        <el-form-item label="投诉编号" prop="complaintNo">
          <el-input v-model="acceptForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="acceptForm.title" disabled />
        </el-form-item>
        <el-form-item label="指派处理人" prop="handlerId">
          <el-select v-model="acceptForm.handlerId" placeholder="请选择处理人" style="width: 100%" filterable>
            <el-option v-for="item in handlerList" :key="item.userId" :label="item.nickName" :value="item.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理备注" prop="handleContent">
          <el-input v-model="acceptForm.handleContent" type="textarea" placeholder="请输入处理备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="acceptDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAcceptForm">确定受理</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理回复弹窗 -->
    <el-dialog
      v-model="replyDialogVisible"
      title="处理回复"
      width="700px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="replyFormRef" :model="replyForm" :rules="replyRules" label-width="100px" class="dialog-form">
        <el-form-item label="投诉编号" prop="complaintNo">
          <el-input v-model="replyForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="replyForm.title" disabled />
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
          <el-input v-model="replyForm.handleResult" type="textarea" placeholder="请输入处理结果" :rows="3" />
        </el-form-item>
        <el-form-item label="整改措施" prop="rectification">
          <el-input v-model="replyForm.rectification" type="textarea" placeholder="请输入整改措施" :rows="3" />
        </el-form-item>
        <el-form-item label="回复内容" prop="handleContent">
          <el-input v-model="replyForm.handleContent" type="textarea" placeholder="请输入回复内容" :rows="3" />
        </el-form-item>
        <el-form-item label="处理图片" prop="handleImages">
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleReplyImageChange"
            :on-remove="handleReplyImageRemove"
            :file-list="replyForm.imageList"
            list-type="picture-card"
            :limit="5"
            :on-exceed="handleExceed"
            accept="image/*"
          >
            <el-icon><plus /></el-icon>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="replyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitReplyForm">确定回复</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 满意度评价弹窗 -->
    <el-dialog
      v-model="evaluateDialogVisible"
      title="满意度评价"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="evaluateFormRef" :model="evaluateForm" :rules="evaluateRules" label-width="100px" class="dialog-form">
        <el-form-item label="投诉编号" prop="complaintNo">
          <el-input v-model="evaluateForm.complaintNo" disabled />
        </el-form-item>
        <el-form-item label="投诉标题" prop="title">
          <el-input v-model="evaluateForm.title" disabled />
        </el-form-item>
        <el-form-item label="满意度" prop="satisfactionScore">
          <el-rate v-model="evaluateForm.satisfactionScore" :max="5" :texts="['非常不满意', '不满意', '一般', '满意', '非常满意']" show-text />
        </el-form-item>
        <el-form-item label="评价内容" prop="evaluateContent">
          <el-input v-model="evaluateForm.evaluateContent" type="textarea" placeholder="请输入评价内容" :rows="4" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="evaluateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluateForm">提交评价</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 查看详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="投诉详情"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-descriptions :column="2" border class="detail-descriptions" v-if="detailData">
        <el-descriptions-item label="投诉编号">{{ detailData.complaintNo }}</el-descriptions-item>
        <el-descriptions-item label="投诉状态">
          <el-tag :type="complaintStatusColorMap[detailData.status] || ''" effect="dark">
            {{ getDictLabel(complaintStatusOptions, detailData.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投诉标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="投诉类型">
          <el-tag :type="complaintTypeColorMap[detailData.type] || ''" effect="dark">
            {{ getDictLabel(complaintTypeOptions, detailData.type) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="投诉时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="投诉人">{{ detailData.complainantName }}</el-descriptions-item>
        <el-descriptions-item label="联系电话">{{ detailData.complainantPhone }}</el-descriptions-item>
        <el-descriptions-item label="投诉位置" :span="2">{{ detailData.houseName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="投诉描述" :span="2">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="投诉图片" :span="2" v-if="detailData.imageList && detailData.imageList.length > 0">
          <div class="detail-images">
            <el-image
              v-for="(img, index) in detailData.imageList"
              :key="index"
              :src="img"
              :preview-src-list="detailData.imageList"
              :initial-index="index"
              class="detail-image"
            />
          </div>
        </el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="满意度">
          <el-rate v-if="detailData.satisfactionScore" v-model="detailData.satisfactionScore" disabled show-score text-color="#ff9900" />
          <span v-else>未评价</span>
        </el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2" v-if="detailData.handleResult">{{ detailData.handleResult }}</el-descriptions-item>
        <el-descriptions-item label="整改措施" :span="2" v-if="detailData.rectification">{{ detailData.rectification }}</el-descriptions-item>
        <el-descriptions-item label="回复内容" :span="2" v-if="detailData.handleContent">{{ detailData.handleContent }}</el-descriptions-item>
        <el-descriptions-item label="回复时间" :span="2" v-if="detailData.handleTime">{{ detailData.handleTime }}</el-descriptions-item>
        <el-descriptions-item label="处理图片" :span="2" v-if="detailData.handleImageList && detailData.handleImageList.length > 0">
          <div class="detail-images">
            <el-image
              v-for="(img, index) in detailData.handleImageList"
              :key="index"
              :src="img"
              :preview-src-list="detailData.handleImageList"
              :initial-index="index"
              class="detail-image"
            />
          </div>
        </el-descriptions-item>
      </el-descriptions>

      <!-- 处理进度时间线 -->
      <div class="progress-timeline" v-if="detailData.progressLogs && detailData.progressLogs.length > 0">
        <h4>处理进度</h4>
        <el-timeline>
          <el-timeline-item
            v-for="(log, index) in detailData.progressLogs"
            :key="index"
            :timestamp="log.createTime"
            :type="index === detailData.progressLogs.length - 1 ? 'primary' : ''"
            :hollow="index !== detailData.progressLogs.length - 1"
          >
            <div class="timeline-content">
              <div class="timeline-title">{{ log.operatorName }} {{ log.action }}</div>
              <div class="timeline-remark" v-if="log.remark">{{ log.remark }}</div>
              <div class="timeline-images" v-if="log.images && log.images.length > 0">
                <el-image
                  v-for="(img, imgIndex) in log.images"
                  :key="imgIndex"
                  :src="img"
                  :preview-src-list="log.images"
                  :initial-index="imgIndex"
                  class="timeline-image"
                />
              </div>
            </div>
          </el-timeline-item>
        </el-timeline>
      </div>

      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Refresh, Search, Bell, Loading,
  CircleCheck, Star, Edit, Delete, Document
} from '@element-plus/icons-vue'
import {
  getSuggestionPage,
  getSuggestionInfo,
  addSuggestion,
  updateSuggestion,
  deleteSuggestion,

  getSuggestionStatistics,
  getHandlerList,
} from '@/api/complaint/suggestion'
import { usePermission } from '@/hooks/usePermission'


const { hasPermission } = usePermission()

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])
const dateRange = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: '',
  status: '',
  complainantName: '',
  handlerName: '',
  beginTime: '',
  endTime: ''
})

const statistics = reactive({
  pendingCount: 0,
  processingCount: 0,
  completedCount: 0,
  avgSatisfaction: '-'
})

const complaintTypeOptions = ref(ComplaintTypeOptions)
const complaintStatusOptions = ref(ComplaintStatusOptions)
const complaintTypeColorMap = ComplaintTypeColorMap
const complaintStatusColorMap = ComplaintStatusColorMap

const houseTreeData = ref([])
const houseTreeProps = { label: 'label', value: 'id', children: 'children' }
const handlerList = ref([])

const detailDialogVisible = ref(false)
const detailData = ref(null)

const addDialogVisible = ref(false)
const addDialogTitle = ref('新增投诉')
const isAdd = ref(true)
const addFormRef = ref(null)
const addForm = reactive({
  id: undefined,
  title: '',
  type: '',
  houseId: '',
  content: '',
  imageList: [],
  complainantPhone: ''
})
const addRules = {
  title: [{ required: true, message: '请输入投诉标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择投诉类型', trigger: 'change' }],
  houseId: [{ required: true, message: '请选择投诉位置', trigger: 'change' }],
  content: [{ required: true, message: '请输入投诉描述', trigger: 'blur' }],
  complainantPhone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ]
}

const acceptDialogVisible = ref(false)
const acceptFormRef = ref(null)
const acceptForm = reactive({
  id: 0,
  complaintNo: '',
  title: '',
  handlerId: '',
  handleContent: ''
})
const acceptRules = {
  handlerId: [{ required: true, message: '请选择处理人', trigger: 'change' }]
}

const replyDialogVisible = ref(false)
const replyFormRef = ref(null)
const replyForm = reactive({
  id: 0,
  complaintNo: '',
  title: '',
  handleResult: '',
  rectification: '',
  handleContent: '',
  imageList: []
})
const replyRules = {
  handleResult: [{ required: true, message: '请输入处理结果', trigger: 'blur' }],
  handleContent: [{ required: true, message: '请输入回复内容', trigger: 'blur' }]
}

const evaluateDialogVisible = ref(false)
const evaluateFormRef = ref(null)
const evaluateForm = reactive({
  id: 0,
  complaintNo: '',
  title: '',
  satisfactionScore: 5,
  evaluateContent: ''
})
const evaluateRules = {
  satisfactionScore: [{ required: true, message: '请选择满意度', trigger: 'change' }]
}

const getDictLabel = (options, value) => {
  if (!options || !value) return ''
  const item = options.find(d => d.value === String(value))
  return item ? item.label : ''
}

const canAccept = (row) => row.status === 0
const canReply = (row) => row.status === 1 || row.status === 2
const canEvaluate = (row) => row.status === 3
const canClose = (row) => row.status !== 4 && row.status !== 5

onMounted(async () => {
  await   await getHandlerListData()
  await getStatistics()
  getList()
})

const getHouseTreeData = async () => {
  try { catch (error) {
    console.error('获取房屋树失败:', error)
  }
}

const getHandlerListData = async () => {
  try {
    const res = await getHandlerList({ status: '0' })
    handlerList.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取处理人列表失败:', error)
  }
}

const getStatistics = async () => {
  try {
    const res = await getSuggestionStatistics()
    const data = res.data || res
    statistics.pendingCount = data.pendingCount || 0
    statistics.processingCount = data.processingCount || 0
    statistics.completedCount = data.completedCount || 0
    statistics.avgSatisfaction = data.avgSatisfaction || '-'
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getSuggestionPage(queryParams)
    const response = res
    tableData.value = response.rows || response.data?.rows || []
    total.value = response.total || response.data?.total || 0
  } catch (error) {
    console.error('获取列表失败:', error)
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.title = ''
  queryParams.type = ''
  queryParams.status = ''
  queryParams.complainantName = ''
  queryParams.handlerName = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  dateRange.value = []
  handleQuery()
}

const handleDateChange = (value) => {
  if (value && value.length === 2) {
    queryParams.beginTime = value[0]
    queryParams.endTime = value[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.id)
}

const handleRefresh = () => {
  getStatistics()
  getList()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  isAdd.value = true
  addDialogTitle.value = '新增投诉'
  resetAddForm()
  addDialogVisible.value = true
}

const resetAddForm = () => {
  addForm.id = undefined
  addForm.title = ''
  addForm.type = ''
  addForm.houseId = ''
  addForm.content = ''
  addForm.imageList = []
  addForm.complainantPhone = ''
  nextTick(() => {
    if (addFormRef.value) {
      addFormRef.value.clearValidate()
    }
  })
}

const closeAddDialog = (done) => {
  resetAddForm()
  if (done) done()
}

const handleImageChange = (file, fileList) => {
  addForm.imageList = fileList
}

const handleImageRemove = (file, fileList) => {
  addForm.imageList = fileList
}

const handleExceed = (files, fileList) => {
  ElMessage.warning(`最多只能上传 5 张图片，当前已选择 ${fileList.length} 张`)
}

const handleReplyImageChange = (file, fileList) => {
  replyForm.imageList = fileList
}

const handleReplyImageRemove = (file, fileList) => {
  replyForm.imageList = fileList
}

const uploadImages = async (files) => {
    return []
  }

const submitAddForm = async () => {
  if (!addFormRef.value) return
  try {
    await addFormRef.value.validate()
    const imageUrls = await uploadImages(addForm.imageList)
    const formData = {
      ...addForm,
      images: imageUrls.join(','),
      houseId: Number(addForm.houseId) || null
    }
    delete formData.imageList
    if (isAdd.value) {
      await addSuggestion(formData)
      ElMessage.success('新增成功')
    } else {
      await updateSuggestion(formData)
      ElMessage.success('修改成功')
    }
    addDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const handleDetail = async (row) => {
  try {
    const res = await getSuggestionInfo(row.id)
    detailData.value = res.data || res
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleAccept = (row) => {
  acceptForm.id = row.id
  acceptForm.complaintNo = row.complaintNo
  acceptForm.title = row.title
  acceptForm.handlerId = ''
  acceptForm.handleContent = ''
  nextTick(() => {
    if (acceptFormRef.value) {
      acceptFormRef.value.clearValidate()
    }
  })
  acceptDialogVisible.value = true
}

const submitAcceptForm = async () => {
  if (!acceptFormRef.value) return
  try {
    await acceptFormRef.value.validate()
    await acceptSuggestion({
      id: acceptForm.id,
      handlerId: Number(acceptForm.handlerId),
      handleContent: acceptForm.handleContent
    })
    ElMessage.success('受理成功')
    acceptDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('受理失败:', error)
  }
}

const handleReply = (row) => {
  replyForm.id = row.id
  replyForm.complaintNo = row.complaintNo
  replyForm.title = row.title
  replyForm.handleResult = ''
  replyForm.rectification = ''
  replyForm.handleContent = ''
  replyForm.imageList = []
  nextTick(() => {
    if (replyFormRef.value) {
      replyFormRef.value.clearValidate()
    }
  })
  replyDialogVisible.value = true
}

const submitReplyForm = async () => {
  if (!replyFormRef.value) return
  try {
    await replyFormRef.value.validate()
    const imageUrls = await uploadImages(replyForm.imageList)
    await replySuggestion({
      id: replyForm.id,
      handleResult: replyForm.handleResult,
      rectification: replyForm.rectification,
      handleContent: replyForm.handleContent,
      handleImages: imageUrls
    })
    ElMessage.success('回复成功')
    replyDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('回复失败:', error)
  }
}

const handleEvaluate = (row) => {
  evaluateForm.id = row.id
  evaluateForm.complaintNo = row.complaintNo
  evaluateForm.title = row.title
  evaluateForm.satisfactionScore = 5
  evaluateForm.evaluateContent = ''
  nextTick(() => {
    if (evaluateFormRef.value) {
      evaluateFormRef.value.clearValidate()
    }
  })
  evaluateDialogVisible.value = true
}

const submitEvaluateForm = async () => {
  if (!evaluateFormRef.value) return
  try {
    await evaluateFormRef.value.validate()
    await evaluateSuggestion({
      id: evaluateForm.id,
      satisfactionScore: evaluateForm.satisfactionScore,
      evaluateContent: evaluateForm.evaluateContent
    })
    ElMessage.success('评价成功')
    evaluateDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('评价失败:', error)
  }
}

const handleClose = (row) => {
  ElMessageBox.confirm('确定要关闭该投诉建议吗？', '关闭确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await updateSuggestionStatus(row.id, 4)
      ElMessage.success('关闭成功')
      getList()
      getStatistics()
    } catch (error) {
      console.error('关闭失败:', error)
    }
  }).catch(() => {})
}
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

.mb-4 {
  margin-bottom: 20px;
}

.stat-card {
  transition: all 0.3s;
  &:hover {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  .stat-content {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .stat-label {
      font-size: 14px;
      color: #909399;
    }
    .stat-value {
      font-size: 28px;
      font-weight: 600;
      &.warning { color: #E6A23C; }
      &.primary { color: #409EFF; }
      &.success { color: #67C23A; }
      &.info { color: #909399; }
    }
  }
  .stat-icon {
    font-size: 24px;
    &.warning { color: #E6A23C; }
    &.primary { color: #409EFF; }
    &.success { color: #67C23A; }
    &.info { color: #909399; }
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

.detail-descriptions {
  :deep(.el-descriptions__label) {
    font-weight: 600;
    color: #606266;
  }
}

.detail-images {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  .detail-image {
    width: 100px;
    height: 100px;
    border-radius: 4px;
    object-fit: cover;
  }
}

.upload-demo {
  :deep(.el-upload-list__item) {
    width: 100px;
    height: 100px;
    :deep(.el-upload-list__item-thumbnail) {
      width: 100px;
      height: 100px;
    }
  }
  :deep(.el-upload--picture-card) {
    width: 100px;
    height: 100px;
  }
}

.small-padding {
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}

.no-evaluate {
  color: #909399;
  font-size: 12px;
}

.progress-timeline {
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  h4 {
    font-size: 16px;
    font-weight: 600;
    color: #303133;
    margin-bottom: 20px;
  }

  .timeline-content {
    .timeline-title {
      font-size: 14px;
      color: #303133;
      margin-bottom: 8px;
    }
    .timeline-remark {
      font-size: 13px;
      color: #606266;
      margin-bottom: 8px;
    }
    .timeline-images {
      display: flex;
      flex-wrap: wrap;
      gap: 8px;
      .timeline-image {
        width: 80px;
        height: 80px;
        border-radius: 4px;
        object-fit: cover;
      }
    }
  }
}
</style>
