<template>
  <div class="app-container">
    <div class="page-header">
      <h1>公告管理</h1>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增公告</el-button>
    </div>

    <el-form :inline="true" :model="queryParams" class="search-form">
      <el-form-item label="标题">
        <el-input v-model="queryParams.title" placeholder="搜索标题" clearable @keyup.enter="handleQuery" style="width:180px" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
          <el-option label="已发布" :value="1" />
          <el-option label="草稿" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleQuery" :icon="Search">搜索</el-button>
        <el-button @click="resetQuery" :icon="RefreshRight">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table :data="tableData" v-loading="loading" border stripe style="width:100%">
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="title" label="标题" min-width="180" show-overflow-tooltip />
      <el-table-column prop="content" label="内容" min-width="200" show-overflow-tooltip>
        <template #default="{ row }">
          {{ stripHtml(row.content) }}
        </template>
      </el-table-column>
      <el-table-column prop="type" label="类型" width="100">
        <template #default="{ row }">
          <el-tag v-if="row.type === 'notice'" type="success">通知</el-tag>
          <el-tag v-else-if="row.type === 'announcement'" type="warning">公告</el-tag>
          <el-tag v-else-if="row.type === 'activity'" type="primary">活动</el-tag>
          <el-tag v-else>{{ row.type }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publishStatus" label="发布状态" width="90">
        <template #default="{ row }">
          <el-switch :model-value="row.publishStatus === 1" @change="(v) => handleStatusChange(row, v)" />
        </template>
      </el-table-column>
      <el-table-column prop="createTime" label="创建时间" width="160" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)" :icon="EditPen">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)" :icon="Delete">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        :total="total"
        @size-change="loadData"
        @current-change="loadData"
      />
    </div>

    <el-dialog :title="dialog.title" v-model="dialog.visible" width="600px" destroy-on-close>
      <el-form ref="formRef" :model="dialog.form" :rules="formRules" label-width="80px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="dialog.form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="dialog.form.type" style="width:100%">
            <el-option label="通知" value="notice" />
            <el-option label="公告" value="announcement" />
            <el-option label="活动" value="activity" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="dialog.form.content" type="textarea" :rows="6" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="发布状态" prop="publishStatus">
          <el-switch v-model="dialog.form.publishStatus" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitForm" :loading="dialog.loading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { Plus, Search, RefreshRight, EditPen, Delete } from "@element-plus/icons-vue"
import { getNoticeList, getNoticeInfo, addNotice, updateNotice, deleteNotice, updateNoticeStatus } from "@/api/announcement/notice"

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)

const queryParams = reactive({ pageNum: 1, pageSize: 10, title: "", status: null })

const dialog = reactive({
  visible: false, title: "", loading: false, isAdd: true,
  form: { id: null, title: "", content: "", type: "notice", publishStatus: 0 }
})

const formRules = {
  title: [{ required: true, message: "请输入标题", trigger: "blur" }],
  content: [{ required: true, message: "请输入内容", trigger: "blur" }],
  type: [{ required: true, message: "请选择类型", trigger: "change" }]
}

const stripHtml = (html) => {
  if (!html) return ""
  return html.replace(/<[^>]+>/g, "").substring(0, 80)
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList(queryParams)
    const data = res.data
    tableData.value = data.records || []
    total.value = data.total || 0
  } catch { tableData.value = [] }
  loading.value = false
}

const handleQuery = () => { queryParams.pageNum = 1; loadData() }
const resetQuery = () => { queryParams.title = ""; queryParams.status = null; handleQuery() }

const handleAdd = () => {
  dialog.title = "新增公告"
  dialog.isAdd = true
  dialog.form = { id: null, title: "", content: "", type: "notice", publishStatus: 0 }
  dialog.visible = true
}

const handleEdit = async (row) => {
  dialog.title = "编辑公告"
  dialog.isAdd = false
  try {
    const res = await getNoticeInfo(row.id)
    const d = res.data
    dialog.form = {
      id: d.id, title: d.title, content: d.content || "",
      type: d.type || "notice", publishStatus: d.publishStatus ?? 0
    }
    dialog.visible = true
  } catch { ElMessage.error("获取公告详情失败") }
}

const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try {
    if (dialog.isAdd) await addNotice(dialog.form)
    else await updateNotice(dialog.form)
    ElMessage.success(dialog.isAdd ? "新增成功" : "修改成功")
    dialog.visible = false
    loadData()
  } catch {}
  dialog.loading = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除公告 "${row.title}" 吗？`, "提示", { type: "warning" })
    await deleteNotice(row.id)
    ElMessage.success("删除成功")
    loadData()
  } catch {}
}

const handleStatusChange = async (row, v) => {
  try {
    await updateNoticeStatus(row.id, v ? 1 : 0)
    ElMessage.success(v ? "已发布" : "已下架")
    loadData()
  } catch {}
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.app-container { padding: 20px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
  h1 { font-size: 20px; font-weight: 600; color: #303133; margin: 0; }
}
.search-form { margin-bottom: 16px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
