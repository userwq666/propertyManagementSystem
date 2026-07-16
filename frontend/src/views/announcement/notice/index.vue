<template>
  <div class="app-container">
    <page-header title="公告通知管理" :breadcrumbs="breadcrumbs">
      <template #actions>
        <el-button type="primary" @click="handleExport" v-permission="'announcement:notice:export'">
          <el-icon><Download /></el-icon>
          导出
        </el-button>
      </template>
    </page-header>

    <el-card class="stat-cards" shadow="never">
      <el-row :gutter="20">
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon published"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.publishedCount || 0 }}</span>
              <span class="stat-label">已发布</span>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon reads"><el-icon><View /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.totalReadCount || 0 }}</span>
              <span class="stat-label">阅读总数</span>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon rate"><el-icon><TrendCharts /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ (stats.avgReadRate || 0) }}%</span>
              <span class="stat-label">平均阅读率</span>
            </div>
          </div>
        </el-col>
        <el-col :xs="24" :sm="12" :md="6">
          <div class="stat-item">
            <div class="stat-icon top"><el-icon><Top /></el-icon></div>
            <div class="stat-info">
              <span class="stat-value">{{ stats.topCount || 0 }}</span>
              <span class="stat-label">置顶数</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <el-card shadow="never">
      <el-form :model="queryParams" ref="queryFormRef" :inline="true" class="search-form">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="queryParams.title" placeholder="请输入公告标题" clearable @keyup.enter="handleQuery" />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="queryParams.type" placeholder="请选择公告类型" clearable>
            <el-option v-for="item in NOTICE_TYPE_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择发布状态" clearable>
            <el-option v-for="item in NOTICE_STATUS_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="发布人" prop="createBy">
          <el-input v-model="queryParams.createBy" placeholder="请输入发布人" clearable />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="dateRange" type="daterange" range-separator="至" start-placeholder="开始日期" end-placeholder="结束日期" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="置顶状态" prop="isTop">
          <el-select v-model="queryParams.isTop" placeholder="请选择置顶状态" clearable>
            <el-option v-for="item in NOTICE_IS_TOP_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon>搜索</el-button>
          <el-button @click="resetQuery"><el-icon><Refresh /></el-icon>重置</el-button>
        </el-form-item>
      </el-form>

      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" plain @click="handleAdd" v-permission="'announcement:notice:add'">
            <el-icon><Plus /></el-icon>新增
          </el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="danger" plain :disabled="multiple" @click="handleDelete" v-permission="'announcement:notice:delete'">
            <el-icon><Delete /></el-icon>删除
          </el-button>
        </el-col>
      </el-row>

      <el-table v-loading="loading" :data="noticeList" @selection-change="handleSelectionChange" border>
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="公告标题" prop="title" show-overflow-tooltip min-width="180" />
        <el-table-column label="公告类型" prop="type" width="110" align="center">
          <template #default="scope">
            <el-tag :type="NOTICE_TYPE_MAP[scope.row.type]">{{ scope.row.typeLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布人" prop="createBy" width="100" align="center" />
        <el-table-column label="发布时间" prop="createTime" width="160" align="center" />
        <el-table-column label="阅读数" prop="readCount" width="80" align="center" />
        <el-table-column label="置顶" prop="isTop" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isTop === '1' ? 'danger' : 'info'" size="small">
              {{ scope.row.isTop === '1' ? '是' : '否' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="发布状态" prop="status" width="100" align="center">
          <template #default="scope">
            <el-tag :type="NOTICE_STATUS_MAP[scope.row.status]">{{ scope.row.statusLabel }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="有效期" width="200" align="center">
          <template #default="scope">
            <span v-if="scope.row.startDate && scope.row.endDate">{{ scope.row.startDate }} ~ {{ scope.row.endDate }}</span>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" align="center" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link @click="handleEdit(scope.row)" v-permission="'announcement:notice:edit'">编辑</el-button>
            <el-button v-if="scope.row.status === '0' || scope.row.status === '1' || scope.row.status === '3'" size="small" type="success" link @click="handlePublish(scope.row)" v-permission="'announcement:notice:publish'">发布</el-button>
            <el-button v-if="scope.row.status === '2'" size="small" type="warning" link @click="handleWithdraw(scope.row)" v-permission="'announcement:notice:withdraw'">撤回</el-button>
            <el-button size="small" type="info" link @click="handleDetail(scope.row)">详情</el-button>
            <el-button size="small" type="danger" link @click="handleDelete(scope.row)" v-permission="'announcement:notice:delete'">删除</el-button>
            <el-button size="small" :type="scope.row.isTop === '1' ? 'info' : 'primary'" link @click="handleToggleTop(scope.row)">
              {{ scope.row.isTop === '1' ? '取消置顶' : '置顶' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="800px" destroy-on-close @close="resetForm">
      <el-form ref="noticeFormRef" :model="noticeForm" :rules="noticeRules" label-width="100px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" maxlength="100" show-word-limit />
        </el-form-item>
        <el-form-item label="公告类型" prop="type">
          <el-select v-model="noticeForm.type" placeholder="请选择公告类型" style="width: 100%">
            <el-option v-for="item in NOTICE_TYPE_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
          </el-select>
        </el-form-item>
        <el-form-item label="公告内容" prop="content">
          <div class="editor-container">
            <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" style="border-bottom: 1px solid #ccc" />
            <Editor v-model="noticeForm.content" :defaultConfig="editorConfig" style="height: 320px; overflow-y: hidden" @onCreated="handleEditorCreated" />
          </div>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="发布范围" prop="publishScope">
              <el-select v-model="noticeForm.publishScope" placeholder="请选择发布范围" style="width: 100%">
                <el-option v-for="item in PUBLISH_SCOPE_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="置顶设置" prop="isTop">
              <el-select v-model="noticeForm.isTop" placeholder="请选择" style="width: 100%">
                <el-option v-for="item in NOTICE_IS_TOP_OPTIONS" :key="item.dictValue" :label="item.dictLabel" :value="item.dictValue" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item v-if="noticeForm.publishScope === '2'" label="选择楼栋" prop="buildingIds">
          <el-select v-model="noticeForm.buildingIds" multiple placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="item in buildingList" :key="item.buildingId" :label="item.buildingName" :value="item.buildingId" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="有效期开始" prop="startDate">
              <el-date-picker v-model="noticeForm.startDate" type="datetime" placeholder="选择开始时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="有效期结束" prop="endDate">
              <el-date-picker v-model="noticeForm.endDate" type="datetime" placeholder="选择结束时间" value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="附件">
          <el-upload ref="uploadRef" :action="uploadUrl" :headers="uploadHeaders" :on-success="handleUploadSuccess" :on-remove="handleUploadRemove" :file-list="fileList" :limit="1">
            <el-button type="primary" plain><el-icon><Upload /></el-icon>点击上传</el-button>
            <template #tip>
              <div class="el-upload__tip">支持 PDF、Word、图片格式，单个文件不超过10MB</div>
            </template>
          </el-upload>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog v-model="detailVisible" title="公告详情" width="800px" destroy-on-close>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="公告标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="公告类型">
          <el-tag :type="NOTICE_TYPE_MAP[detailData.type]">{{ detailData.typeLabel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布状态">
          <el-tag :type="NOTICE_STATUS_MAP[detailData.status]">{{ detailData.statusLabel }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布人">{{ detailData.createBy }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="阅读数">{{ detailData.readCount }}</el-descriptions-item>
        <el-descriptions-item label="置顶">
          <el-tag :type="detailData.isTop === '1' ? 'danger' : 'info'" size="small">
            {{ detailData.isTop === '1' ? '是' : '否' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="发布范围">{{ detailData.publishScopeLabel }}</el-descriptions-item>
        <el-descriptions-item v-if="detailData.buildingNames && detailData.buildingNames.length" label="指定楼栋">
          {{ detailData.buildingNames.join('、') }}
        </el-descriptions-item>
        <el-descriptions-item label="有效期" :span="2">
          {{ detailData.startDate }} ~ {{ detailData.endDate }}
        </el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">公告内容</el-divider>
      <div class="notice-content" v-html="detailData.content"></div>

      <el-divider content-position="left">阅读统计</el-divider>
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="read-stat-card">
            <div class="stat-num">{{ detailData.readCount || 0 }}</div>
            <div class="stat-desc">已读人数</div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="read-stat-card unread">
            <div class="stat-num">{{ (detailData.totalTarget || 0) - (detailData.readCount || 0) }}</div>
            <div class="stat-desc">未读人数</div>
          </div>
        </el-col>
      </el-row>

      <el-divider content-position="left">阅读趋势</el-divider>
      <div ref="readTrendChartRef" style="height: 300px; width: 100%"></div>

      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete, Download, Upload, CircleCheck, View, TrendCharts, Top } from '@element-plus/icons-vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
import '@wangeditor/editor/dist/css/style.css'
import * as echarts from 'echarts'
import PageHeader from '@/components/PageHeader/index.vue'
import Pagination from '@/components/Pagination/index.vue'
import { getToken } from '@/utils/auth'
import {
  getNoticeList,
  getNoticeInfo,
  addNotice,
  updateNotice,
  deleteNotice,
  publishNotice,
  withdrawNotice,
  toggleTopNotice,
  getNoticeStats,
  getNoticeReadTrend,
  exportNotice
} from '@/api/announcement/notice'
import { getBuildingList } from '@/api/community/building'

const breadcrumbs = [
  { path: '/announcement', name: '公告通知' },
  { path: '/announcement/notice', name: '公告管理' }
]

const loading = ref(false)
const submitLoading = ref(false)
const total = ref(0)
const noticeList = ref([])
const multiple = ref(true)
const ids = ref([])
const dateRange = ref([])

const stats = ref({
  publishedCount: 0,
  totalReadCount: 0,
  avgReadRate: 0,
  topCount: 0
})

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  title: '',
  type: '',
  status: '',
  createBy: '',
  isTop: ''
})

const queryFormRef = ref(null)

const dialogVisible = ref(false)
const dialogTitle = ref('')
const noticeFormRef = ref(null)
const noticeForm = reactive({
  noticeId: undefined,
  title: '',
  type: '',
  content: '',
  publishScope: '1',
  buildingIds: [],
  isTop: '0',
  startDate: '',
  endDate: '',
  attachmentUrl: '',
  attachmentName: '',
  remark: ''
})

const noticeRules = {
  title: [{ required: true, message: '请输入公告标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择公告类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入公告内容', trigger: 'blur' }],
  publishScope: [{ required: true, message: '请选择发布范围', trigger: 'change' }],
  isTop: [{ required: true, message: '请选择置顶设置', trigger: 'change' }]
}

const detailVisible = ref(false)
const detailData = ref({})
const readTrendChartRef = ref(null)
let readTrendChart = null

const buildingList = ref([])
const fileList = ref([])
const uploadUrl = ref('/api/common/upload')
const uploadHeaders = computed(() => ({ Authorization: 'Bearer ' + getToken() }))
const uploadRef = ref(null)

const editorRef = ref(null)
const toolbarConfig = {
  toolbarKeys: [
    'bold',
    'italic',
    'underline',
    'through',
    'headerSelect',
    'fontSize',
    'color',
    'bgColor',
    'justifyCenter',
    'justifyRight',
    'uploadImage',
    'insertLink',
    'bulletedList',
    'numberedList'
  ]
}
const editorConfig = {
  placeholder: '请输入公告内容...',
  MENU_CONF: {
    uploadImage: {
      server: '/api/common/upload',
      fieldName: 'file',
      maxFileSize: 10 * 1024 * 1024,
      allowedFileTypes: ['image/*'],
      meta: { token: getToken() },
      customInsert(res, insertFn) {
        if (res.code === 200) {
          insertFn(res.data.url, res.data.name, res.data.url)
        } else {
          ElMessage.error('上传失败')
        }
      }
    }
  }
}

const handleEditorCreated = (editor) => {
  editorRef.value = editor
}

const getList = async () => {
  loading.value = true
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await getNoticeList(params)
    noticeList.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

const getStatsData = async () => {
  try {
    const res = await getNoticeList()
    stats.value = res.data || {}
  } catch (e) {
    console.error('获取统计失败', e)
  }
}

const getBuildingData = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data || []
  } catch (e) {
    console.error('获取楼栋失败', e)
  }
}

const handleQuery = () => {
  queryParams.pageNum = 1
  getList()
}

const resetQuery = () => {
  dateRange.value = []
  queryFormRef.value?.resetFields()
  handleQuery()
}

const handleSelectionChange = (selection) => {
  ids.value = selection.map(item => item.noticeId)
  multiple.value = !selection.length
}

const resetForm = () => {
  noticeForm.noticeId = undefined
  noticeForm.title = ''
  noticeForm.type = ''
  noticeForm.content = ''
  noticeForm.publishScope = '1'
  noticeForm.buildingIds = []
  noticeForm.isTop = '0'
  noticeForm.startDate = ''
  noticeForm.endDate = ''
  noticeForm.attachmentUrl = ''
  noticeForm.attachmentName = ''
  noticeForm.remark = ''
  fileList.value = []
  noticeFormRef.value?.resetFields()
}

const handleAdd = () => {
  resetForm()
  dialogTitle.value = '新增公告'
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  resetForm()
  dialogTitle.value = '编辑公告'
  try {
    const res = await getNoticeInfo(row.noticeId)
    const data = res.data
    Object.assign(noticeForm, {
      noticeId: data.noticeId,
      title: data.title,
      type: data.type,
      content: data.content,
      publishScope: data.publishScope,
      buildingIds: data.buildingIds || [],
      isTop: data.isTop,
      startDate: data.startDate,
      endDate: data.endDate,
      attachmentUrl: data.attachmentUrl,
      attachmentName: data.attachmentName,
      remark: data.remark
    })
    if (data.attachmentUrl) {
      fileList.value = [{ name: data.attachmentName, url: data.attachmentUrl }]
    }
    dialogVisible.value = true
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

const submitForm = async () => {
  const valid = await noticeFormRef.value?.validate().catch(() => false)
  if (!valid) return

  submitLoading.value = true
  try {
    const data = { ...noticeForm }
    if (data.noticeId) {
      await updateNotice(data)
      ElMessage.success('更新成功')
    } else {
      await addNotice(data)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    getList()
    getStatsData()
  } catch (e) {
    ElMessage.error('操作失败')
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = (row) => {
  const noticeIds = row.noticeId ? [row.noticeId] : ids.value
  ElMessageBox.confirm('确认要删除选中的公告通知吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await deleteNotice(noticeIds.join(','))
    ElMessage.success('删除成功')
    getList()
    getStatsData()
  }).catch(() => {})
}

const handlePublish = (row) => {
  ElMessageBox.confirm(`确认要发布公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    await publishNotice(row.noticeId)
    ElMessage.success('发布成功')
    getList()
    getStatsData()
  }).catch(() => {})
}

const handleWithdraw = (row) => {
  ElMessageBox.confirm(`确认要撤回公告"${row.title}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    await withdrawNotice(row.noticeId)
    ElMessage.success('撤回成功')
    getList()
    getStatsData()
  }).catch(() => {})
}

const handleToggleTop = async (row) => {
  const action = row.isTop === '1' ? '取消置顶' : '置顶'
  try {
    await updateNotice(row.noticeId)
    ElMessage.success(`${action}成功`)
    getList()
    getStatsData()
  } catch (e) {
    ElMessage.error(`${action}失败`)
  }
}

const handleDetail = async (row) => {
  try {
    const res = await getNoticeInfo(row.noticeId)
    detailData.value = res.data || {}
    detailVisible.value = true
    await nextTick()
    initReadTrendChart(row.noticeId)
  } catch (e) {
    ElMessage.error('获取详情失败')
  }
}

const initReadTrendChart = async (noticeId) => {
  if (!readTrendChartRef.value) return
  if (readTrendChart) {
    readTrendChart.dispose()
  }
  readTrendChart = echarts.init(readTrendChartRef.value)
  try {
    const res = await getNoticeList(noticeId)
    const trendData = res.data || []
    const dates = trendData.map(item => item.date)
    const readCounts = trendData.map(item => item.readCount)
    const unreadCounts = trendData.map(item => item.unreadCount)
    readTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['已读', '未读'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: dates },
      yAxis: { type: 'value' },
      series: [
        { name: '已读', type: 'bar', data: readCounts, itemStyle: { color: '#67c23a' } },
        { name: '未读', type: 'bar', data: unreadCounts, itemStyle: { color: '#909399' } }
      ]
    })
  } catch (e) {
    readTrendChart.setOption({
      tooltip: { trigger: 'axis' },
      legend: { data: ['已读', '未读'] },
      grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'category', data: ['暂无数据'] },
      yAxis: { type: 'value' },
      series: [
        { name: '已读', type: 'bar', data: [0], itemStyle: { color: '#67c23a' } },
        { name: '未读', type: 'bar', data: [0], itemStyle: { color: '#909399' } }
      ]
    })
  }
}

const handleUploadSuccess = (response) => {
  if (response.code === 200) {
    noticeForm.attachmentUrl = response.data.url
    noticeForm.attachmentName = response.data.name
    ElMessage.success('上传成功')
  } else {
    ElMessage.error('上传失败')
  }
}

const handleUploadRemove = () => {
  noticeForm.attachmentUrl = ''
  noticeForm.attachmentName = ''
}

const handleExport = async () => {
  try {
    const params = { ...queryParams }
    if (dateRange.value && dateRange.value.length === 2) {
      params.beginTime = dateRange.value[0]
      params.endTime = dateRange.value[1]
    }
    const res = await getNoticeList(params)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = '公告通知列表.xlsx'
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (e) {
    ElMessage.error('导出失败')
  }
}

const handleResizeCharts = () => {
  readTrendChart?.resize()
}

onMounted(() => {
  getList()
  getStatsData()
  getBuildingData()
  window.addEventListener('resize', handleResizeCharts)
})

onBeforeUnmount(() => {
  readTrendChart?.dispose()
  window.removeEventListener('resize', handleResizeCharts)
  const editor = editorRef.value
  if (editor) {
    editor.destroy()
  }
})
</script>

<style lang="scss" scoped>
.app-container {
  padding: 0;
}

.stat-cards {
  margin-bottom: 16px;

  .stat-item {
    display: flex;
    align-items: center;
    gap: 16px;
    padding: 16px;
    border-radius: 8px;
    background: #f5f7fa;
    transition: all 0.3s;

    &:hover {
      background: #ecf5ff;
    }
  }

  .stat-icon {
    width: 48px;
    height: 48px;
    border-radius: 12px;
    display: flex;
    align-items: center;
    justify-content: center;
    flex-shrink: 0;

    .el-icon {
      font-size: 24px;
      color: #fff;
    }

    &.published { background: linear-gradient(135deg, #67c23a, #85ce61); }
    &.reads { background: linear-gradient(135deg, #409eff, #66b1ff); }
    &.rate { background: linear-gradient(135deg, #e6a23c, #ebb563); }
    &.top { background: linear-gradient(135deg, #f56c6c, #f89898); }
  }

  .stat-info {
    display: flex;
    flex-direction: column;

    .stat-value {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
      line-height: 1.2;
    }

    .stat-label {
      font-size: 13px;
      color: #909399;
      margin-top: 4px;
    }
  }
}

.search-form {
  :deep(.el-form-item) {
    margin-bottom: 16px;
  }
}

.mb8 {
  margin-bottom: 8px;
}

.editor-container {
  border: 1px solid #ccc;
  border-radius: 4px;
  width: 100%;

  :deep(.w-e-text-container) {
    min-height: 320px;
  }
}

.notice-content {
  padding: 16px;
  min-height: 100px;
  line-height: 1.8;
  color: #303133;
  background: #fafafa;
  border-radius: 4px;
  word-break: break-all;

  :deep(img) {
    max-width: 100%;
  }
}

.read-stat-card {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
  background: linear-gradient(135deg, #67c23a, #85ce61);

  &.unread {
    background: linear-gradient(135deg, #909399, #b1b3b8);
  }

  .stat-num {
    font-size: 32px;
    font-weight: 600;
    color: #fff;
  }

  .stat-desc {
    font-size: 14px;
    color: rgba(255, 255, 255, 0.85);
    margin-top: 8px;
  }
}
</style>
