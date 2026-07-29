<template>
  <div class="app-container">
<el-row :gutter="20">
    <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
      <el-card shadow="never" class="stat-card">
        <div class="stat-content">
          <div class="stat-label">总记录数</div>
          <div class="stat-value primary">{{ statistics.totalCount }}</div>
        </div>
        <el-icon class="stat-icon primary"><document /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">正常数</div>
            <div class="stat-value success">{{ statistics.normalCount }}</div>
          </div>
          <el-icon class="stat-icon success"><circle-check /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">异常数</div>
            <div class="stat-value danger">{{ statistics.abnormalCount }}</div>
          </div>
          <el-icon class="stat-icon danger"><warning-filled /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">未执行数</div>
            <div class="stat-value info">{{ statistics.unexecutedCount }}</div>
          </div>
          <el-icon class="stat-icon info"><remove /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>记录列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['inspection:record:add']">
              <plus /> 新增记录
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="100px" @keyup.enter="handleQuery">
        <el-form-item label="关联计划" prop="planName">
          <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="执行人" prop="executorName">
          <el-input v-model="queryParams.executorName" placeholder="请输入执行人" clearable style="width: 160px" />
        </el-form-item>
        <el-form-item label="执行状态" prop="executeStatus">
          <el-select v-model="queryParams.executeStatus" placeholder="请选择执行状态" clearable style="width: 160px">
            <el-option v-for="item in executeStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行时间" prop="beginTime">
          <el-date-picker
            v-model="queryParams.beginTime"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 320px"
            value-format="YYYY-MM-DD HH:mm:ss"
            @change="handleDateChange"
          />
        </el-form-item>
        <el-form-item label="是否异常" prop="isAbnormal">
          <el-select v-model="queryParams.isAbnormal" placeholder="请选择" clearable style="width: 120px">
            <el-option label="是" value="1" />
            <el-option label="否" value="0" />
          </el-select>
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

      <!-- 记录表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="recordId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="recordNo" label="记录编号" width="160" align="center" show-overflow-tooltip />
        <el-table-column prop="planName" label="关联计划" min-width="160" show-overflow-tooltip />
        <el-table-column prop="planType" label="计划类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="planTypeColorMap[scope.row.planType] || ''" effect="dark">
              {{ getDictLabel(planTypeOptions, scope.row.planType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="executorName" label="执行人" width="100" align="center" />
        <el-table-column prop="executeDate" label="执行日期" width="120" align="center" />
        <el-table-column prop="executeTime" label="执行时间" width="100" align="center" />
        <el-table-column prop="executeStatus" label="执行状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="executeStatusColorMap[scope.row.executeStatus] || ''" effect="dark">
              {{ getDictLabel(executeStatusOptions, scope.row.executeStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="abnormalCount" label="异常项数" width="100" align="center">
          <template #default="scope">
            <span :class="{ 'abnormal-highlight': scope.row.abnormalCount > 0 }">
              {{ scope.row.abnormalCount }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="duration" label="巡检用时(分)" width="110" align="center" />
        <el-table-column prop="weather" label="天气状况" width="100" align="center" />
        <el-table-column label="操作" align="center" width="280" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleDetail(scope.row)" v-permission="['inspection:record:list']">详情</el-button>
            <el-button size="small" type="success" link @click="handleExecute(scope.row)" v-if="canExecute(scope.row)" v-permission="['inspection:record:edit']">执行</el-button>
            <el-button size="small" type="warning" link @click="updateInspectionRecord(scope.row)" v-if="canHandleAbnormal(scope.row)" v-permission="['inspection:record:handle']">异常处理</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)" v-permission="['inspection:record:delete']">删除</el-button>
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
      title="巡检记录详情"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div v-if="detailData" class="detail-content">
        <el-descriptions :column="2" border class="detail-descriptions">
          <el-descriptions-item label="记录编号">{{ detailData.recordNo }}</el-descriptions-item>
          <el-descriptions-item label="关联计划">{{ detailData.planName }}</el-descriptions-item>
          <el-descriptions-item label="计划类型">
            <el-tag :type="planTypeColorMap[detailData.planType] || ''" effect="dark">
              {{ getDictLabel(planTypeOptions, detailData.planType) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="执行人">{{ detailData.executorName }}</el-descriptions-item>
          <el-descriptions-item label="执行日期">{{ detailData.executeDate }}</el-descriptions-item>
          <el-descriptions-item label="执行时间">{{ detailData.executeTime }}</el-descriptions-item>
          <el-descriptions-item label="执行状态">
            <el-tag :type="executeStatusColorMap[detailData.executeStatus] || ''" effect="dark">
              {{ getDictLabel(executeStatusOptions, detailData.executeStatus) }}
            </el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="异常项数">
            <span :class="{ 'abnormal-highlight': detailData.abnormalCount > 0 }">
              {{ detailData.abnormalCount }}
            </span>
          </el-descriptions-item>
          <el-descriptions-item label="巡检用时">{{ detailData.duration }} 分钟</el-descriptions-item>
          <el-descriptions-item label="天气状况">{{ detailData.weather }}</el-descriptions-item>
          <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
        </el-descriptions>

        <!-- 巡检项结果列表 -->
        <div class="item-results-section" v-if="detailData.itemResults && detailData.itemResults.length > 0">
          <h4>巡检项执行结果</h4>
          <el-table :data="detailData.itemResults" border style="width: 100%">
            <el-table-column prop="itemName" label="巡检项名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="checkStandard" label="检查标准" min-width="180" show-overflow-tooltip />
            <el-table-column prop="checkResult" label="检查结果" width="100" align="center">
              <template #default="scope">
                <el-tag :type="checkResultColorMap[scope.row.checkResult] || ''" effect="dark">
                  {{ getDictLabel(checkResultOptions, scope.row.checkResult) }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="actualValue" label="实际值" width="120" align="center" />
            <el-table-column prop="abnormalDesc" label="异常描述" min-width="150" show-overflow-tooltip>
              <template #default="scope">
                <span :class="{ 'abnormal-text': scope.row.isAbnormal }">
                  {{ scope.row.abnormalDesc || '-' }}
                </span>
              </template>
            </el-table-column>
            <el-table-column prop="photos" label="现场照片" width="120" align="center">
              <template #default="scope">
                <div v-if="scope.row.photos && scope.row.photos.length > 0" class="photo-preview">
                  <el-image
                    :src="scope.row.photos[0]"
                    :preview-src-list="scope.row.photos"
                    :initial-index="0"
                    fit="cover"
                    class="preview-image"
                  />
                  <span v-if="scope.row.photos.length > 1" class="photo-count">+{{ scope.row.photos.length - 1 }}</span>
                </div>
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增记录弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      title="新增巡检记录"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeAddDialog"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px" class="dialog-form">
        <el-form-item label="关联计划" prop="planId">
          <el-select v-model="addForm.planId" placeholder="请选择关联计划" style="width: 100%" filterable @change="handlePlanChange">
            <el-option v-for="item in planOptions" :key="item.planId" :label="item.planName" :value="item.planId" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行人" prop="executorId">
          <el-select v-model="addForm.executorId" placeholder="请选择执行人" style="width: 100%" filterable>
            <el-option v-for="item in userList" :key="item.userId" :label="item.nickName || item.userName" :value="item.userId" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行日期" prop="executeDate">
          <el-date-picker
            v-model="addForm.executeDate"
            type="date"
            placeholder="请选择执行日期"
            value-format="YYYY-MM-DD"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="天气状况" prop="weather">
          <el-select v-model="addForm.weather" placeholder="请选择天气状况" style="width: 100%" clearable>
            <el-option v-for="item in weatherOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="执行备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" placeholder="请输入执行备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 执行记录弹窗 -->
    <el-dialog
      v-model="executeDialogVisible"
      title="执行巡检记录"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeExecuteDialog"
      destroy-on-close
    >
      <div class="execute-content" v-if="executeForm">
        <el-descriptions :column="2" border class="execute-info">
          <el-descriptions-item label="关联计划">{{ executePlanInfo?.planName }}</el-descriptions-item>
          <el-descriptions-item label="执行人">{{ executeForm.executorName }}</el-descriptions-item>
        </el-descriptions>

        <div class="item-results-section">
          <h4>巡检项结果填写</h4>
          <div v-if="executeForm.itemResults.length === 0" class="empty-items">
            <el-empty description="暂无巡检项目" />
          </div>
          <el-form v-else ref="executeFormRef" :model="executeForm" :rules="executeRules" label-position="top" class="execute-form">
            <el-card v-for="(item, index) in executeForm.itemResults" :key="item.itemId" class="item-card" shadow="never">
              <template #header>
                <div class="item-card-header">
                  <span class="item-name">{{ item.itemName }}</span>
                  <span class="item-standard">检查标准：{{ item.checkStandard }}</span>
                </div>
              </template>
              <el-form-item :prop="'itemResults.' + index + '.checkResult'" :rules="[{ required: true, message: '请选择检查结果', trigger: 'change' }]">
                <label class="form-label">检查结果</label>
                <el-radio-group v-model="item.checkResult" @change="handleCheckResultChange(index)">
                  <el-radio-button v-for="opt in checkResultOptions" :key="opt.value" :value="opt.value">{{ opt.label }}</el-radio-button>
                </el-radio-group>
              </el-form-item>
              <el-form-item :prop="'itemResults.' + index + '.actualValue'">
                <label class="form-label">实际值</label>
                <el-input v-model="item.actualValue" placeholder="请输入实际值" style="width: 240px" />
              </el-form-item>
              <el-form-item :prop="'itemResults.' + index + '.photos'">
                <label class="form-label">现场照片</label>
                <el-upload
                  class="upload-demo"
                  action="#"
                  :auto-upload="false"
                  :on-change="(file, fileList) => handleItemPhotoChange(index, file, fileList)"
                  :on-remove="(file, fileList) => handleItemPhotoRemove(index, file, fileList)"
                  :file-list="item.photos"
                  list-type="picture-card"
                  :limit="3"
                  :on-exceed="handleExceed"
                  accept="image/*"
                >
                  <el-icon><plus /></el-icon>
                </el-upload>
              </el-form-item>
              <el-form-item v-if="item.checkResult === '2'" :prop="'itemResults.' + index + '.abnormalDesc'" :rules="[{ required: true, message: '请输入异常描述', trigger: 'blur' }]">
                <label class="form-label">异常描述</label>
                <el-input v-model="item.abnormalDesc" type="textarea" placeholder="请描述异常情况（不合格时必填）" :rows="2" style="width: 100%" />
              </el-form-item>
            </el-card>
          </el-form>
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeExecuteDialog">取消</el-button>
          <el-button type="primary" @click="submitExecuteForm">提交执行</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 异常处理弹窗 -->
    <el-dialog
      v-model="abnormalDialogVisible"
      title="异常处理"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="abnormalFormRef" :model="abnormalForm" :rules="abnormalRules" label-width="100px" class="dialog-form">
        <el-form-item label="整改要求" prop="rectificationReq">
          <el-input v-model="abnormalForm.rectificationReq" type="textarea" placeholder="请输入整改要求" :rows="3" />
        </el-form-item>
        <el-form-item label="整改期限" prop="rectificationDeadline">
          <el-date-picker
            v-model="abnormalForm.rectificationDeadline"
            type="datetime"
            placeholder="请选择整改期限"
            value-format="YYYY-MM-DD HH:mm:ss"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="整改人" prop="rectificationPerson">
          <el-input v-model="abnormalForm.rectificationPerson" placeholder="请输入整改人" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="abnormalForm.remark" type="textarea" placeholder="请输入备注" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="abnormalDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAbnormalForm">确定提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>
<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Refresh, Search, Document, CircleCheck,
  WarningFilled, Remove, Edit, Delete, Picture, ZoomIn
} from '@element-plus/icons-vue'
import {
  getInspectionRecordList,
  getInspectionRecordInfo,
  addInspectionRecord,
  deleteInspectionRecord,
  getInspectionRecordStatistics,
  getInspectionPlanOptions,
  getUserList,
} from '@/api/inspection/record'

const executeStatusOptions = ref(ExecuteStatusOptions)
const checkResultOptions = ref(CheckResultOptions)
const weatherOptions = ref(WeatherOptions)
const executeStatusColorMap = ExecuteStatusColorMap
const checkResultColorMap = CheckResultColorMap

const planTypeOptions = ref([
  { value: '1', label: '日常巡检' },
  { value: '2', label: '专项巡检' },
  { value: '3', label: '设备巡检' },
  { value: '4', label: '安全巡检' },
  { value: '5', label: '消防巡检' }
])

const planTypeColorMap = {
  '1': 'success',
  '2': 'primary',
  '3': 'warning',
  '4': 'danger',
  '5': ''
}

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  planName: '',
  executorName: '',
  executeStatus: '',
  beginTime: '',
  endTime: '',
  isAbnormal: ''
})

const statistics = reactive({
  totalCount: 0,
  normalCount: 0,
  abnormalCount: 0,
  unexecutedCount: 0
})

const planOptions = ref([])
const userList = ref([])

const detailDialogVisible = ref(false)
const detailData = ref(null)

const addDialogVisible = ref(false)
const addFormRef = ref(null)
const addForm = reactive({
  planId: '',
  executorId: '',
  executeDate: '',
  weather: '',
  remark: ''
})
const addRules = reactive({
  planId: [{ required: true, message: '请选择关联计划', trigger: 'change' }],
  executorId: [{ required: true, message: '请选择执行人', trigger: 'change' }],
  executeDate: [{ required: true, message: '请选择执行日期', trigger: 'change' }],
  weather: [{ required: true, message: '请选择天气状况', trigger: 'change' }]
})

const executeDialogVisible = ref(false)
const executeFormRef = ref(null)
const executePlanInfo = ref(null)
const executeForm = ref({
  recordId: 0,
  executorName: '',
  itemResults: []
})
const executeRules = reactive({})

const abnormalDialogVisible = ref(false)
const abnormalFormRef = ref(null)
const abnormalForm = reactive({
  recordId: 0,
  resultId: undefined,
  rectificationReq: '',
  rectificationDeadline: '',
  rectificationPerson: '',
  remark: ''
})
const abnormalRules = reactive({
  rectificationReq: [{ required: true, message: '请输入整改要求', trigger: 'blur' }],
  rectificationDeadline: [{ required: true, message: '请选择整改期限', trigger: 'change' }],
  rectificationPerson: [{ required: true, message: '请输入整改人', trigger: 'blur' }]
})

const getDictLabel = (options, value) => {
  if (!options || !value) return ''
  const item = options.find(d => d.value === value)
  return item ? item.label : ''
}

onMounted(async () => {
  await getPlanOptions()
  await getUserOptions()
  await getStatistics()
  getList()
})

const getPlanOptions = async () => {
  try {
    const res = await getInspectionPlanOptions({ pageNum: 1, pageSize: 100, planStatus: '2' })
    const data = res.rows || res.data?.rows || res.data || []
    planOptions.value = data.map(item => ({
      planId: item.planId,
      planName: item.planName,
      planType: item.planType,
      inspectionItems: item.inspectionItems || []
    }))
  } catch (error) {
    console.error('获取计划选项失败:', error)
  }
}

const getUserOptions = async () => {
  try {
    const res = await getUserList({ pageNum: 1, pageSize: 100 })
    userList.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

const getStatistics = async () => {
  try {
    const res = await getInspectionRecordStatistics()
    const data = res.data || res
    statistics.totalCount = data.totalCount || 0
    statistics.normalCount = data.normalCount || 0
    statistics.abnormalCount = data.abnormalCount || 0
    statistics.unexecutedCount = data.unexecutedCount || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

const getList = async () => {
  loading.value = true
  try {
    const res = await getInspectionRecordList(queryParams)
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
  queryParams.planName = ''
  queryParams.executorName = ''
  queryParams.executeStatus = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  queryParams.isAbnormal = ''
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
  selectionIds.value = selection.map(item => item.recordId)
}

const handleRefresh = () => {
  getStatistics()
  getList()
  ElMessage.success('刷新成功')
}

const handleAdd = () => {
  resetAddForm()
  addDialogVisible.value = true
}

const resetAddForm = () => {
  addForm.planId = ''
  addForm.executorId = ''
  addForm.executeDate = ''
  addForm.weather = ''
  addForm.remark = ''
  nextTick(() => {
    if (addFormRef.value) {
      addFormRef.value.clearValidate()
    }
  })
}

const closeAddDialog = (done) => {
  resetAddForm()
  done()
}

const handlePlanChange = (planId) => {
  const plan = planOptions.value.find(p => p.planId === planId)
  if (plan) {
    executePlanInfo.value = plan
  }
}

const submitAddForm = async () => {
  if (!addFormRef.value) return
  try {
    await addFormRef.value.validate()
    await addInspectionRecord({
      planId: Number(addForm.planId),
      executorId: Number(addForm.executorId),
      executeDate: addForm.executeDate,
      weather: addForm.weather,
      remark: addForm.remark
    })
    ElMessage.success('新增记录成功')
    addDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

const canExecute = (row) => row.executeStatus === '4' || !row.executeStatus
const canHandleAbnormal = (row) => row.executeStatus === '2' || row.executeStatus === '3'

const handleDetail = async (row) => {
  try {
    const res = await getInspectionRecordInfo(row.recordId)
    detailData.value = res.data || res
    detailDialogVisible.value = true
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

const handleExecute = async (row) => {
  try {
    const res = await getInspectionRecordInfo(row.recordId)
    const record = res.data || res
    const plan = planOptions.value.find(p => p.planId === record.planId)
    const items = plan?.inspectionItems || []

    executeForm.value = {
      recordId: row.recordId,
      executorName: row.executorName,
      itemResults: items.map(item => ({
        itemId: item.itemId,
        itemName: item.itemName,
        checkStandard: item.checkStandard,
        checkResult: '',
        actualValue: '',
        photos: [],
        abnormalDesc: ''
      }))
    }
    executePlanInfo.value = plan
    executeDialogVisible.value = true
  } catch (error) {
    console.error('获取记录详情失败:', error)
  }
}

const closeExecuteDialog = (done) => {
  executeForm.value = { recordId: 0, executorName: '', itemResults: [] }
  executePlanInfo.value = null
  done()
}

const handleCheckResultChange = (index) => {
  const item = executeForm.value.itemResults[index]
  if (item.checkResult !== '2') {
    item.abnormalDesc = ''
  }
}

const handleItemPhotoChange = (index, file, fileList) => {
  executeForm.value.itemResults[index].photos = fileList
}

const handleItemPhotoRemove = (index, file, fileList) => {
  executeForm.value.itemResults[index].photos = fileList
}

const handleExceed = (files, fileList) => {
  ElMessage.warning('最多只能上传 3 张图片')
}

const uploadImages = async (files) => {
  const urls = []
  for (const file of files) {
    const formData = new FormData()
    formData.append('file', file.raw || file)
    try {
      const res = await addInspectionRecord(formData)
      urls.push(res.data?.url || res.url || res.data)
    } catch (error) {
      console.error('图片上传失败:', error)
    }
  }
  return urls
}

const submitExecuteForm = async () => {
  if (!executeFormRef.value) return
  try {
    await executeFormRef.value.validate()

    const itemResults = []
    for (const item of executeForm.value.itemResults) {
      const photoUrls = []
      if (item.photos && item.photos.length > 0) {
        for (const photo of item.photos) {
          if (photo.url) {
            photoUrls.push(photo.url)
          } else if (photo.raw) {
            const uploaded = await uploadImages([photo])
            photoUrls.push(...uploaded)
          }
        }
      }
      itemResults.push({
        itemId: item.itemId,
        checkResult: item.checkResult,
        actualValue: item.actualValue,
        photos: photoUrls,
        abnormalDesc: item.abnormalDesc
      })
    }

    await addInspectionRecord({
      recordId: executeForm.value.recordId,
      itemResults
    })
    ElMessage.success('执行成功')
    executeDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('执行失败:', error)
  }
}

const handleAbnormal = (row) => {
  abnormalForm.recordId = row.recordId
  abnormalForm.resultId = undefined
  abnormalForm.rectificationReq = ''
  abnormalForm.rectificationDeadline = ''
  abnormalForm.rectificationPerson = ''
  abnormalForm.remark = ''
  nextTick(() => {
    if (abnormalFormRef.value) {
      abnormalFormRef.value.clearValidate()
    }
  })
  abnormalDialogVisible.value = true
}

const submitAbnormalForm = async () => {
  if (!abnormalFormRef.value) return
  try {
    await abnormalFormRef.value.validate()
    await handleAbnormalApi({
      recordId: abnormalForm.recordId,
      resultId: abnormalForm.resultId,
      rectificationReq: abnormalForm.rectificationReq,
      rectificationDeadline: abnormalForm.rectificationDeadline,
      rectificationPerson: abnormalForm.rectificationPerson,
      remark: abnormalForm.remark
    })
    ElMessage.success('异常处理成功')
    abnormalDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('异常处理失败:', error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认要删除该巡检记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteInspectionRecord(row.recordId)
      ElMessage.success('删除成功')
      getList()
      getStatistics()
    } catch (error) {
      console.error('删除失败:', error)
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
      &.primary { color: #409EFF; }
      &.success { color: #67C23A; }
      &.danger { color: #F56C6C; }
      &.info { color: #909399; }
    }
  }
  .stat-icon {
    font-size: 24px;
    &.primary { color: #409EFF; }
    &.success { color: #67C23A; }
    &.danger { color: #F56C6C; }
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
  :deep(.el-descriptions__row) {
    &:nth-child(odd) {
      :deep(.el-descriptions__cell) {
        background-color: #fafafa;
      }
    }
  }
  :deep(.el-descriptions__label) {
    font-weight: 600;
    color: #606266;
  }
}

.detail-content {
  .item-results-section {
    margin-top: 20px;
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 15px;
    }
  }
}

.abnormal-highlight {
  color: #F56C6C;
  font-weight: 600;
}

.abnormal-text {
  color: #F56C6C;
  font-weight: 600;
}

.photo-preview {
  position: relative;
  display: inline-block;
  .preview-image {
    width: 60px;
    height: 60px;
    border-radius: 4px;
  }
  .photo-count {
    position: absolute;
    bottom: 0;
    right: 0;
    background-color: rgba(0, 0, 0, 0.6);
    color: #fff;
    font-size: 12px;
    padding: 1px 6px;
    border-radius: 4px;
  }
}

.execute-content {
  .execute-info {
    margin-bottom: 20px;
  }
  .item-results-section {
    h4 {
      font-size: 16px;
      font-weight: 600;
      color: #303133;
      margin-bottom: 15px;
    }
  }
  .empty-items {
    padding: 40px 0;
  }
}

.item-card {
  margin-bottom: 15px;
  .item-card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    .item-name {
      font-weight: 600;
      color: #303133;
    }
    .item-standard {
      color: #909399;
      font-size: 13px;
    }
  }
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #606266;
  font-size: 14px;
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
    :deep(.el-upload__input) {
      width: 100px;
      height: 100px;
    }
  }
}

.small-padding {
  :deep(.cell) { display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}

.mb-4 {
  margin-bottom: 20px;
}
</style>
