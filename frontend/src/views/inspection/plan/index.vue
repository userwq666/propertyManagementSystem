<template>
  <div class="app-container">
    <div class="page-header">
      <h1>巡检计划管理</h1>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb-4">
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">待执行</div>
            <div class="stat-value warning">{{ statistics.pendingCount }}</div>
          </div>
          <el-icon class="stat-icon warning"><clock /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">执行中</div>
            <div class="stat-value primary">{{ statistics.executingCount }}</div>
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
          <el-icon class="stat-icon success"><check-circle /></el-icon>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6" :lg="6" :xl="6">
        <el-card shadow="never" class="stat-card">
          <div class="stat-content">
            <div class="stat-label">已暂停</div>
            <div class="stat-value danger">{{ statistics.pausedCount }}</div>
          </div>
          <el-icon class="stat-icon danger"><video-pause /></el-icon>
        </el-card>
      </el-col>
    </el-row>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>计划列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['inspection:plan:add']">
              <plus /> 新增计划
            </el-button>
            <el-button type="info" @click="handleRefresh">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px" @keyup.enter="handleQuery">
        <el-form-item label="计划名称" prop="planName">
          <el-input v-model="queryParams.planName" placeholder="请输入计划名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="计划类型" prop="planType">
          <el-select v-model="queryParams.planType" placeholder="请选择计划类型" clearable style="width: 180px">
            <el-option v-for="item in planTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="计划状态" prop="planStatus">
          <el-select v-model="queryParams.planStatus" placeholder="请选择计划状态" clearable style="width: 180px">
            <el-option v-for="item in planStatusOptions" :key="item.value" :label="item.label" :value="item.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="负责人" prop="leaderName">
          <el-input v-model="queryParams.leaderName" placeholder="请输入负责人" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="计划时间" prop="beginTime">
          <el-date-picker
            v-model="dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 280px"
            value-format="YYYY-MM-DD"
            @change="handleDateChange"
          />
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

      <!-- 计划表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="planId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-sort="{ prop: 'createTime', order: 'descending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="planName" label="计划名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="planType" label="计划类型" width="120" align="center">
          <template #default="scope">
            <el-tag :type="planTypeColorMap[scope.row.planType] || ''" effect="dark">
              {{ getDictLabel(planTypeOptions, scope.row.planType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="buildingNames" label="计划区域" min-width="150" show-overflow-tooltip />
        <el-table-column prop="frequency" label="巡检频次" width="110" align="center">
          <template #default="scope">
            <el-tag :type="frequencyColorMap[scope.row.frequency] || ''" effect="dark">
              {{ getDictLabel(frequencyOptions, scope.row.frequency) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="routeName" label="巡检路线" min-width="120" show-overflow-tooltip />
        <el-table-column prop="leaderName" label="负责人" width="100" align="center" />
        <el-table-column prop="inspectionTimeRange" label="巡检时段" width="140" align="center">
          <template #default="scope">
            {{ scope.row.inspectionTimeStart }} - {{ scope.row.inspectionTimeEnd }}
          </template>
        </el-table-column>
        <el-table-column prop="planStatus" label="计划状态" width="110" align="center">
          <template #default="scope">
            <el-tag :type="planStatusColorMap[scope.row.planStatus] || ''" effect="dark">
              {{ getDictLabel(planStatusOptions, scope.row.planStatus) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="nextExecuteTime" label="下次执行时间" width="170" align="center" />
        <el-table-column prop="completedCount" label="完成进度" width="100" align="center">
          <template #default="scope">
            <span>{{ scope.row.completedCount }}/{{ scope.row.totalCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="dateRange" label="计划时间" width="200" align="center">
          <template #default="scope">
            <span>{{ scope.row.startDate }} 至 {{ scope.row.endDate }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" width="300" fixed="right" class-name="small-padding">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleDetail(scope.row)" v-permission="['inspection:plan:list']">详情</el-button>
            <el-button size="small" type="warning" link @click="handleEdit(scope.row)" v-if="canEdit(scope.row)" v-permission="['inspection:plan:edit']">编辑</el-button>
            <el-button size="small" type="success" link @click="handleStatusChange(scope.row, '1')" v-if="canEnable(scope.row)" v-permission="['inspection:plan:enable']">启用</el-button>
            <el-button size="small" type="danger" link @click="handleStatusChange(scope.row, '3')" v-if="canPause(scope.row)" v-permission="['inspection:plan:disable']">暂停</el-button>
            <el-button size="small" type="info" link @click="handleStatusChange(scope.row, '4')" v-if="canComplete(scope.row)" v-permission="['inspection:plan:edit']">完成</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)" v-if="canDelete(scope.row)" v-permission="['inspection:plan:delete']">删除</el-button>
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
          @size-change="getList"
        />
      </div>
    </el-card>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailDialogVisible"
      :title="detailTitle"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-tabs v-model="detailActiveTab">
        <el-tab-pane label="计划信息" name="info">
          <el-descriptions :column="2" border class="detail-descriptions" v-if="detailData">
            <el-descriptions-item label="计划名称">{{ detailData.planName }}</el-descriptions-item>
            <el-descriptions-item label="计划类型">
              <el-tag :type="planTypeColorMap[detailData.planType] || ''" effect="dark">
                {{ getDictLabel(planTypeOptions, detailData.planType) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="计划区域">{{ detailData.buildingNames }}</el-descriptions-item>
            <el-descriptions-item label="巡检频次">
              <el-tag :type="frequencyColorMap[detailData.frequency] || ''" effect="dark">
                {{ getDictLabel(frequencyOptions, detailData.frequency) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="巡检时段">{{ detailData.inspectionTimeStart }} - {{ detailData.inspectionTimeEnd }}</el-descriptions-item>
            <el-descriptions-item label="巡检路线">{{ detailData.routeName }}</el-descriptions-item>
            <el-descriptions-item label="负责人">{{ detailData.leaderName }}</el-descriptions-item>
            <el-descriptions-item label="执行人">{{ detailData.executorNames }}</el-descriptions-item>
            <el-descriptions-item label="关联设备">{{ detailData.equipmentNames }}</el-descriptions-item>
            <el-descriptions-item label="计划状态">
              <el-tag :type="planStatusColorMap[detailData.planStatus] || ''" effect="dark">
                {{ getDictLabel(planStatusOptions, detailData.planStatus) }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="开始日期">{{ detailData.startDate }}</el-descriptions-item>
            <el-descriptions-item label="结束日期">{{ detailData.endDate }}</el-descriptions-item>
            <el-descriptions-item label="下次执行时间">{{ detailData.nextExecuteTime || '-' }}</el-descriptions-item>
            <el-descriptions-item label="完成进度">{{ detailData.completedCount }}/{{ detailData.totalCount }}</el-descriptions-item>
            <el-descriptions-item label="备注" :span="2">{{ detailData.remark || '-' }}</el-descriptions-item>
          </el-descriptions>
        </el-tab-pane>
        <el-tab-pane label="巡检项目" name="items">
          <el-table :data="detailData?.inspectionItems || []" border style="width: 100%">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="itemName" label="项目名称" min-width="150" show-overflow-tooltip />
            <el-table-column prop="checkStandard" label="检查标准" min-width="200" show-overflow-tooltip />
            <el-table-column prop="passStandard" label="合格标准" min-width="200" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="执行记录" name="records">
          <el-table :data="recordList" border style="width: 100%" v-loading="recordLoading">
            <el-table-column type="index" label="序号" width="60" align="center" />
            <el-table-column prop="executorName" label="执行人" width="100" align="center" />
            <el-table-column prop="executeTime" label="执行时间" width="170" align="center" />
            <el-table-column prop="status" label="状态" width="100" align="center">
              <template #default="scope">
                <el-tag :type="scope.row.status === '1' ? 'success' : 'danger'" effect="dark">
                  {{ scope.row.status === '1' ? '正常' : '异常' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
          </el-table>
        </el-tab-pane>
      </el-tabs>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 新增/编辑计划弹窗 -->
    <el-dialog
      v-model="addDialogVisible"
      :title="addDialogTitle"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeAddDialog"
      destroy-on-close
    >
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="110px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划名称" prop="planName">
              <el-input v-model="addForm.planName" placeholder="请输入计划名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划类型" prop="planType">
              <el-select v-model="addForm.planType" placeholder="请选择计划类型" style="width: 100%">
                <el-option v-for="item in planTypeOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划区域" prop="buildingIds">
              <el-tree-select
                v-model="addForm.buildingIds"
                :data="buildingTreeData"
                :props="treeProps"
                placeholder="请选择计划区域"
                style="width: 100%"
                multiple
                show-checkbox
                check-strictly
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="巡检频次" prop="frequency">
              <el-select v-model="addForm.frequency" placeholder="请选择巡检频次" style="width: 100%">
                <el-option v-for="item in frequencyOptions" :key="item.value" :label="item.label" :value="item.value" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="巡检时段" prop="inspectionTimeRange">
              <el-time-picker
                v-model="addForm.inspectionTimeRange"
                is-range
                range-separator="至"
                start-placeholder="开始时间"
                end-placeholder="结束时间"
                format="HH:mm"
                value-format="HH:mm"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="巡检路线" prop="routeName">
              <el-input v-model="addForm.routeName" placeholder="请输入巡检路线名称" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人" prop="leaderId">
              <el-select v-model="addForm.leaderId" placeholder="请选择负责人" style="width: 100%" filterable>
                <el-option v-for="user in userList" :key="user.userId" :label="user.nickName || user.userName" :value="user.userId" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="执行人" prop="executorIds">
              <el-select v-model="addForm.executorIds" placeholder="请选择执行人" style="width: 100%" multiple filterable>
                <el-option v-for="user in userList" :key="user.userId" :label="user.nickName || user.userName" :value="user.userId" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="关联设备" prop="equipmentIds">
          <el-tree-select
            v-model="addForm.equipmentIds"
            :data="equipmentTreeData"
            :props="treeProps"
            placeholder="请选择关联设备"
            style="width: 100%"
            multiple
            show-checkbox
            check-strictly
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始日期" prop="startDate">
              <el-date-picker
                v-model="addForm.startDate"
                type="date"
                placeholder="请选择开始日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="结束日期" prop="endDate">
              <el-date-picker
                v-model="addForm.endDate"
                type="date"
                placeholder="请选择结束日期"
                value-format="YYYY-MM-DD"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" placeholder="请输入备注" :rows="2" />
        </el-form-item>

        <!-- 巡检项目 -->
        <el-divider content-position="left">巡检项目</el-divider>
        <div class="inspection-items">
          <div v-for="(item, index) in addForm.inspectionItems" :key="index" class="inspection-item-row">
            <el-row :gutter="10">
              <el-col :span="7">
                <el-form-item :prop="'inspectionItems.' + index + '.itemName'" :rules="[{ required: true, message: '请输入项目名称', trigger: 'blur' }]" label-width="0">
                  <el-input v-model="item.itemName" placeholder="项目名称" />
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item :prop="'inspectionItems.' + index + '.checkStandard'" :rules="[{ required: true, message: '请输入检查标准', trigger: 'blur' }]" label-width="0">
                  <el-input v-model="item.checkStandard" placeholder="检查标准" />
                </el-form-item>
              </el-col>
              <el-col :span="7">
                <el-form-item :prop="'inspectionItems.' + index + '.passStandard'" label-width="0">
                  <el-input v-model="item.passStandard" placeholder="合格标准" />
                </el-form-item>
              </el-col>
              <el-col :span="2">
                <el-button type="danger" circle @click="removeInspectionItem(index)" :disabled="addForm.inspectionItems.length <= 1">
                  <delete />
                </el-button>
              </el-col>
            </el-row>
          </div>
          <el-button type="primary" plain @click="addInspectionItem" style="width: 100%">
            <plus /> 添加巡检项目
          </el-button>
        </div>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeAddDialog">取消</el-button>
          <el-button type="primary" @click="submitAddForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Download, Refresh, Search, Clock, Loading, CircleCheck, VideoPause, Delete, Edit } from '@element-plus/icons-vue'
import {
  getInspectionPlanList,
  getInspectionPlanInfo,
  addInspectionPlan,
  updateInspectionPlan,
  deleteInspectionPlan,
  getInspectionPlanStatistics,
  getInspectionPlanRecords,
  getUserList
} from '@/api/inspection/plan'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])
const dateRange = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  planName: '',
  planType: '',
  planStatus: '',
  leaderName: '',
  beginTime: '',
  endTime: ''
})

// 统计数据
const statistics = reactive({
  pendingCount: 0,
  executingCount: 0,
  completedCount: 0,
  pausedCount: 0
})

// 字典选项
const planTypeOptions = ref(PlanTypeOptions)
const planStatusOptions = ref(PlanStatusOptions)
const frequencyOptions = ref(FrequencyOptions)

// 颜色映射
const planStatusColorMap = PlanStatusColorMap
const planTypeColorMap = PlanTypeColorMap
const frequencyColorMap = FrequencyColorMap

// 树形数据
const buildingTreeData = ref([])
const equipmentTreeData = ref([])
const userList = ref([])
const treeProps = ref({
  label: 'label',
  value: 'id',
  children: 'children'
})

// 详情弹窗
const detailDialogVisible = ref(false)
const detailTitle = ref('计划详情')
const detailData = ref(null)
const detailActiveTab = ref('info')
const recordList = ref([])
const recordLoading = ref(false)

// 新增/编辑弹窗
const addDialogVisible = ref(false)
const addDialogTitle = ref('新增计划')
const isAdd = ref(true)
const addFormRef = ref(null)

const addForm = reactive({
  planName: '',
  planType: '',
  buildingIds: [],
  frequency: '',
  inspectionTimeRange: [],
  inspectionTimeStart: '',
  inspectionTimeEnd: '',
  routeName: '',
  leaderId: '',
  executorIds: [],
  equipmentIds: [],
  inspectionItems: [
    { itemName: '', checkStandard: '', passStandard: '', sort: 1 }
  ],
  startDate: '',
  endDate: '',
  remark: ''
})

const addRules = reactive({
  planName: [{ required: true, message: '请输入计划名称', trigger: 'blur' }],
  planType: [{ required: true, message: '请选择计划类型', trigger: 'change' }],
  buildingIds: [{ required: true, type: 'array', min: 1, message: '请选择计划区域', trigger: 'change' }],
  frequency: [{ required: true, message: '请选择巡检频次', trigger: 'change' }],
  inspectionTimeRange: [{ required: true, type: 'array', min: 1, message: '请选择巡检时段', trigger: 'change' }],
  leaderId: [{ required: true, message: '请选择负责人', trigger: 'change' }],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }]
})

// 初始化
onMounted(async () => {
  await Promise.all([
    getBuildingTreeData(),
    getEquipmentTreeData(),
    getUserListData()
  ])
  await getStatistics()
  getList()
})

// 获取楼栋树
const getBuildingTreeData = async () => {
  try {
    const res = await getBuildingTree({})
    buildingTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取楼栋树失败:', error)
  }
}

// 获取设备树
const getEquipmentTreeData = async () => {
  try {
    const res = await getEquipmentTree({})
    equipmentTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取设备树失败:', error)
  }
}

// 获取用户列表
const getUserListData = async () => {
  try {
    const res = await getUserListApi({ status: '0' })
    userList.value = res.rows || res.data?.rows || res.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
  }
}

// 获取统计数据
const getStatistics = async () => {
  try {
    const res = await getInspectionPlanStatistics()
    const data = res.data || res
    statistics.pendingCount = data.pendingCount || 0
    statistics.executingCount = data.executingCount || 0
    statistics.completedCount = data.completedCount || 0
    statistics.pausedCount = data.pausedCount || 0
  } catch (error) {
    console.error('获取统计数据失败:', error)
  }
}

// 获取计划列表
const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    const res = await getInspectionPlanList(params)
    const response = res
    tableData.value = response.rows || response.data?.rows || []
    total.value = response.total || response.data?.total || 0
  } catch (error) {
    console.error('获取计划列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查询
const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

// 重置查询
const resetQuery = () => {
  queryParams.planName = ''
  queryParams.planType = ''
  queryParams.planStatus = ''
  queryParams.leaderName = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  dateRange.value = []
  handleQuery()
}

// 日期范围变化
const handleDateChange = (value) => {
  if (value && value.length === 2) {
    queryParams.beginTime = value[0]
    queryParams.endTime = value[1]
  } else {
    queryParams.beginTime = ''
    queryParams.endTime = ''
  }
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.planId)
}

// 刷新
const handleRefresh = () => {
  getStatistics()
  getList()
  ElMessage.success('刷新成功')
}

// 获取字典标签
const getDictLabel = (options, value) => {
  if (!options || !value) return ''
  const item = options.find(d => d.value === value)
  return item ? item.label : ''
}

// 权限判断
const canEdit = (row) => row.planStatus === '0' || row.planStatus === '3'
const canEnable = (row) => row.planStatus === '0' || row.planStatus === '3'
const canPause = (row) => row.planStatus === '1' || row.planStatus === '2'
const canComplete = (row) => row.planStatus === '1' || row.planStatus === '2'
const canDelete = (row) => row.planStatus === '0'

// 新增计划
const handleAdd = () => {
  isAdd.value = true
  addDialogTitle.value = '新增计划'
  resetAddForm()
  addDialogVisible.value = true
}

// 编辑计划
const handleEdit = async (row) => {
  try {
    const res = await getInspectionPlanInfo(row.planId)
    const data = res.data || res
    isAdd.value = false
    addDialogTitle.value = '编辑计划'
    
    // 填充表单数据
    addForm.planId = data.planId
    addForm.planName = data.planName
    addForm.planType = data.planaddForm.buildingIds = data.buildingIds || []
    addForm.frequency = data.frequency
    addForm.inspectionTimeRange = [data.inspectionTimeStart, data.inspectionTimeEnd]
    addForm.inspectionTimeStart = data.inspectionTimeStart
    addForm.inspectionTimeEnd = data.inspectionTimeEnd
    addForm.routeName = data.routeName
    addForm.leaderId = data.leaderId
    addForm.executorIds = data.executorIds || []
    addForm.equipmentIds = data.equipmentIds || []
    addForm.inspectionItems = data.inspectionItems || [{ itemName: '', checkStandard: '', passStandard: '', sort: 1 }]
    addForm.startDate = data.startDate
    addForm.endDate = data.endDate
    addForm.remark = data.remark
    
    addDialogVisible.value = true
  } catch (error) {
    console.error('获取计划详情失败:', error)
  }
}

// 重置新增表单
const resetAddForm = () => {
  addForm.planId = undefined
  addForm.planName = ''
  addForm.planType = ''
  addForm.buildingIds = []
  addForm.frequency = ''
  addForm.inspectionTimeRange = []
  addForm.inspectionTimeStart = ''
  addForm.inspectionTimeEnd = ''
  addForm.routeName = ''
  addForm.leaderId = ''
  addForm.executorIds = []
  addForm.equipmentIds = []
  addForm.inspectionItems = [{ itemName: '', checkStandard: '', passStandard: '', sort: 1 }]
  addForm.startDate = ''
  addForm.endDate = ''
  addForm.remark = ''
  nextTick(() => {
    if (addFormRef.value) {
      addFormRef.value.clearValidate()
    }
  })
}

// 关闭新增弹窗
const closeAddDialog = (done) => {
  resetAddForm()
  done()
}

// 添加巡检项目
const addInspectionItem = () => {
  addForm.inspectionItems.push({
    itemName: '',
    checkStandard: '',
    passStandard: '',
    sort: addForm.inspectionItems.length + 1
  })
}

// 删除巡检项目
const removeInspectionItem = (index) => {
  if (addForm.inspectionItems.length > 1) {
    addForm.inspectionItems.splice(index, 1)
  }
}

// 提交新增表单
const submitAddForm = async () => {
  if (!addFormRef.value) return
  try {
    await addFormRef.value.validate()
    
    // 处理巡检时段
    if (addForm.inspectionTimeRange && addForm.inspectionTimeRange.length === 2) {
      addForm.inspectionTimeStart = addForm.inspectionTimeRange[0]
      addForm.inspectionTimeEnd = addForm.inspectionTimeRange[1]
    }
    
    // 验证巡检项目
    const hasEmptyItem = addForm.inspectionItems.some(item => !item.itemName || !item.checkStandard)
    if (hasEmptyItem) {
      ElMessage.warning('请填写完整的巡检项目信息')
      return
    }
    
    const formData = { ...addForm }
    delete formData.inspectionTimeRange
    
    if (isAdd.value) {
      await addInspectionPlan(formData)
      ElMessage.success('新增计划成功')
    } else {
      await updateInspectionPlan(formData)
      ElMessage.success('修改计划成功')
    }
    addDialogVisible.value = false
    getList()
    getStatistics()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 详情
const handleDetail = async (row) => {
  try {
    const res = await getInspectionPlanInfo(row.planId)
    detailData.value = res.data || res
    detailActiveTab.value = 'info'
    detailDialogVisible.value = true
    // 加载执行记录
    loadRecords(row.planId)
  } catch (error) {
    console.error('获取详情失败:', error)
  }
}

// 加载执行记录
const loadRecords = async (planId) => {
  recordLoading.value = true
  try {
    const res = await getInspectionPlanRecords({ planId })
    recordList.value = res.rows || res.data?.rows || []
  } catch (error) {
    console.error('获取执行记录失败:', error)
  } finally {
    recordLoading.value = false
  }
}

// 删除计划
const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除计划"${row.planName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteInspectionPlan(row.planId)
      ElMessage.success('删除成功')
      getList()
      getStatistics()
    } catch (error) {
      console.error('删除失败:', error)
    }
  }).catch(() => {})
}

// 修改状态
const handleStatusChange = (row, status) => {
  const statusLabel = getDictLabel(planStatusOptions, status)
  ElMessageBox.confirm(`确定要将计划"${row.planName}"状态变更为"${statusLabel}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteInspectionPlan(row.planId, status)
      ElMessage.success('状态变更成功')
      getList()
      getStatistics()
    } catch (error) {
      console.error('状态变更失败:', error)
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
      &.danger { color: #F56C6C; }
    }
  }
  .stat-icon {
    font-size: 24px;
    &.warning { color: #E6A23C; }
    &.primary { color: #409EFF; }
    &.success { color: #67C23A; }
    &.danger { color: #F56C6C; }
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

.small-padding {
  :deep(.cell) { display: flex; align-items: center; gap: 4px; flex-wrap: wrap; }
  :deep(.el-table__cell) {
    padding: 5px 10px;
  }
}

.inspection-items {
  .inspection-item-row {
    margin-bottom: 10px;
    padding: 10px;
    background-color: #fafafa;
    border-radius: 4px;
    border: 1px dashed #dcdfe6;
    
    &:hover {
      border-color: #409EFF;
    }
  }
}
</style>
