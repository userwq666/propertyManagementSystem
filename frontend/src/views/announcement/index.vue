<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="标题">
        <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="草稿" :value="0" /><el-option label="已发布" :value="1" /><el-option label="已撤回" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'announcement:list:add'">新增公告</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" class="announcement-table">
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="st(row.publishStatus)">{{ stText(row.publishStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ row.type===1?'通知':row.type===2?'公告':'活动' }}</template>
        </el-table-column>
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop" type="danger" size="small">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="creatorName" label="发布人" width="100" />
        <el-table-column prop="publishTime" label="发布时间" width="180" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="操作" min-width="400" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)" v-permission="'announcement:list:list'">详情</el-button>
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'announcement:list:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'announcement:list:delete'">删除</el-button>
            <el-button type="success" size="small" @click="handlePublish(row)" v-if="row.publishStatus!==1" v-permission="'announcement:list:edit'">发布</el-button>
            <el-button type="warning" size="small" @click="handleRevoke(row)" v-if="row.publishStatus===1" v-permission="'announcement:list:edit'">撤回</el-button>
            <el-button size="small" @click="handleTop(row)" v-permission="'announcement:list:edit'">{{ row.isTop ? '取消置顶' : '置顶' }}</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <!-- 详情 -->
    <el-dialog title="公告详情" v-model="detailDialogVisible" width="640px">
      <el-descriptions :column="2" border v-if="detailRow">
        <el-descriptions-item label="标题" :span="2">{{ detailRow.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ typeText(detailRow.type) }}</el-descriptions-item>
        <el-descriptions-item label="状态">{{ stText(detailRow.publishStatus) }}</el-descriptions-item>
        <el-descriptions-item label="发布人">{{ detailRow.creatorName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detailRow.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="置顶">{{ detailRow.isTop ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="预发布时间">{{ detailRow.scheduledPublishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="封面" :span="2">
          <el-image v-if="detailRow.coverImage" :src="detailRow.coverImage" fit="cover" style="max-width:100%;max-height:120px" />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="内容" :span="2" style="white-space:pre-wrap">{{ detailRow.content }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="700px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type"><el-option label="通知" :value="1" /><el-option label="公告" :value="2" /><el-option label="活动" :value="3" /></el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="6" />
        </el-form-item>
        <el-form-item label="封面图">
          <el-input v-model="form.coverImage" placeholder="封面图URL" />
        </el-form-item>
        <el-form-item label="发布设置">
          <div class="scheduled-row">
            <span>预发布</span>
            <el-switch v-model="form.isScheduled" :active-value="1" :inactive-value="0" />
            <span class="row-gap">置顶</span>
            <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
          </div>
        </el-form-item>
        <el-form-item v-if="form.isScheduled" label="预发布时间" prop="scheduledPublishTime">
          <el-date-picker v-model="form.scheduledPublishTime" type="datetime" placeholder="选择预发布时间" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" />
        </el-form-item>
        <el-form-item label="置顶过期">
          <el-date-picker v-model="form.topExpireTime" type="datetime" placeholder="选择日期" value-format="YYYY-MM-DDTHH:mm:ss" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addAnnouncement, updateAnnouncement, deleteAnnouncement, getAnnouncementPage, updateAnnouncementStatus, updateAnnouncementTop } from '@/api/announcement/index'

const loading = ref(false); const tableData = ref([]); const total = ref(0)
const dialogVisible = ref(false); const formRef = ref(null); const isEdit = ref(false)
const detailDialogVisible = ref(false)
const detailRow = ref(null)

const searchForm = reactive({ pageNum: 1, pageSize: 10, title: '', status: '' })
const form = reactive({ id: null, title: '', type: 1, content: '', coverImage: '', isScheduled: 0, scheduledPublishTime: '', isTop: 0, topExpireTime: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑公告' : '新增公告')
const st = (s) => ({ 0: 'info', 1: 'success', 2: 'warning' }[s] || 'info')
const stText = (s) => ({ 0: '草稿', 1: '已发布', 2: '已撤回' }[s] || '')
const typeText = (t) => ({ 1: '通知', 2: '公告', 3: '活动' }[t] || '')

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try { const res = await getAnnouncementPage({ ...searchForm }); tableData.value = res.data.records; total.value = res.data.total } finally { loading.value = false }
}
function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.title = ''; searchForm.status = ''; handleSearch() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, { ...row, isScheduled: row.scheduledPublishTime ? 1 : 0, scheduledPublishTime: row.scheduledPublishTime || '' }); dialogVisible.value = true }
function handleDetail(row) { detailRow.value = row; detailDialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); Object.assign(form, { id: null, title: '', type: 1, content: '', coverImage: '', isScheduled: 0, scheduledPublishTime: '', isTop: 0, topExpireTime: '' }) }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  if (form.isScheduled && !form.scheduledPublishTime) {
    ElMessage.warning('请选择预发布时间')
    return
  }
  if (form.scheduledPublishTime && form.topExpireTime && form.topExpireTime <= form.scheduledPublishTime) {
    ElMessage.warning('置顶过期时间必须晚于预发布时间')
    return
  }
  try {
    const payload = { ...form, scheduledPublishTime: form.isScheduled ? form.scheduledPublishTime : null }
    if (isEdit.value) await updateAnnouncement(payload)
    else await addAnnouncement(payload)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该公告吗？', '提示', { type: 'warning' })
  try { await deleteAnnouncement(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

async function handlePublish(row) {
  try { await updateAnnouncementStatus({ id: row.id, status: 1 }); ElMessage.success('发布成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleRevoke(row) {
  try { await updateAnnouncementStatus({ id: row.id, status: 2 }); ElMessage.success('已撤回'); fetchData() } catch (e) { /* handled */ }
}

async function handleTop(row) {
  try { await updateAnnouncementTop({ id: row.id, isTop: row.isTop ? 0 : 1 }); ElMessage.success('操作成功'); fetchData() } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>

<style scoped>
.scheduled-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.row-gap {
  margin-left: 16px;
}
.announcement-table :deep(.el-table__row) {
  height: 56px;
}
</style>
