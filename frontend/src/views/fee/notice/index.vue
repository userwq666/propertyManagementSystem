<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="通知类型">
        <el-select v-model="searchForm.noticeType" placeholder="请选择类型" clearable>
          <el-option label="缴费通知" :value="0" />
          <el-option label="催缴通知" :value="1" />
          <el-option label="欠费通知" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="发送状态">
        <el-select v-model="searchForm.sendStatus" placeholder="请选择状态" clearable>
          <el-option label="未发送" :value="0" />
          <el-option label="已发送" :value="1" />
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
          <el-button type="primary" @click="handleAdd" v-permission="'fee:notice:add'">新增通知</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="noticeTitle" label="标题" width="180" show-overflow-tooltip />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ noticeTypeLabel(row.noticeType) }}</template>
        </el-table-column>
        <el-table-column label="内容预览" show-overflow-tooltip>
          <template #default="{ row }">{{ row.noticeContent ? row.noticeContent.substring(0, 50) + '...' : '' }}</template>
        </el-table-column>
        <el-table-column label="发送状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.sendStatus === 0 ? 'info' : 'success'">{{ row.sendStatus === 0 ? '未发送' : '已发送' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sendTime" label="发送时间" width="180" />
        <el-table-column prop="creatorName" label="创建人" width="100" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="240" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'fee:notice:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'fee:notice:delete'">删除</el-button>
            <el-button
              v-if="row.sendStatus === 0"
              type="success"
              size="small"
              @click="handlePublish(row)"
             v-permission="'fee:notice:edit'">发布</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="通知标题" prop="noticeTitle">
          <el-input v-model="form.noticeTitle" placeholder="请输入通知标题" />
        </el-form-item>
        <el-form-item label="通知类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择类型" style="width: 100%">
            <el-option label="缴费通知" :value="0" />
            <el-option label="催缴通知" :value="1" />
            <el-option label="欠费通知" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="通知内容" prop="noticeContent">
          <el-input v-model="form.noticeContent" type="textarea" :rows="5" placeholder="请输入通知内容" />
        </el-form-item>
        <el-form-item label="发送范围" prop="sendScope">
          <el-select v-model="form.sendScope" placeholder="请选择发送范围" style="width: 100%">
            <el-option label="全部" :value="0" />
            <el-option label="指定楼栋" :value="1" />
          </el-select>
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
import { addFeeNotice, updateFeeNotice, deleteFeeNotice, getFeeNoticePage, publishFeeNotice } from '@/api/fee/notice'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = reactive({ pageNum: 1, pageSize: 10, noticeType: null, sendStatus: null })
const form = reactive({ id: null, noticeTitle: '', noticeContent: '', noticeType: 0, sendScope: 0, sendStatus: 0, creatorId: null })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑通知' : '新增通知')

const rules = {
  noticeTitle: [{ required: true, message: '请输入通知标题', trigger: 'blur' }],
  noticeContent: [{ required: true, message: '请输入通知内容', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择通知类型', trigger: 'change' }]
}

const noticeTypeMap = { 0: '缴费通知', 1: '催缴通知', 2: '欠费通知' }
function noticeTypeLabel(t) { return noticeTypeMap[t] || '未知' }

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeeNoticePage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.noticeType = null; searchForm.sendStatus = null; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.noticeType = 0; form.sendScope = 0; form.sendStatus = 0 }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateFeeNotice(form) }
    else { await addFeeNotice(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该通知吗？', '提示', { type: 'warning' })
  try {
    await deleteFeeNotice(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}

async function handlePublish(row) {
  await ElMessageBox.confirm('确定要发布该通知吗？发布后将无法撤回。', '提示', { type: 'warning' })
  try {
    await publishFeeNotice(row.id)
    ElMessage.success('发布成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
