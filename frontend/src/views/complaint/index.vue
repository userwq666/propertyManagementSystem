<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item v-if="hasOwnerPerm" label="业主">
        <el-select v-model="searchForm.ownerId" placeholder="请选择业主" clearable filterable>
          <el-option v-for="o in owners.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchForm.type" placeholder="请选择" clearable>
          <el-option label="投诉" :value="1" /><el-option label="建议" :value="2" /><el-option label="咨询" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待受理" :value="0" /><el-option label="已受理" :value="1" /><el-option label="处理中" :value="2" /><el-option label="已回复" :value="3" /><el-option label="已完成" :value="4" /><el-option label="已撤销" :value="5" />
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
          <el-button type="primary" @click="handleAdd" v-permission="'complaint:list:add'">新增</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="complaintNo" label="单号" width="150" />
        <el-table-column label="类型" width="90">
          <template #default="{ row }">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column label="投诉人" width="100">
          <template #default="{ row }">{{ row.creatorName || row.ownerName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="roomNo" label="房屋" width="90" />
        <el-table-column label="优先级" width="90">
          <template #default="{ row }">{{ priorityText(row.priority) }}</template>
        </el-table-column>
        <el-table-column prop="category" label="分类" width="110" />
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column prop="handlerName" label="处理人" width="90" />
        <el-table-column prop="createTime" label="提交时间" width="170" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="操作" min-width="320" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button v-if="row.status===0" type="primary" size="small" @click="handleEdit(row)" v-permission="'complaint:list:edit'">编辑</el-button>
            <el-button v-if="row.status===0" type="warning" size="small" @click="openHandle(row, 1)" v-permission="'complaint:list:edit'">受理</el-button>
            <el-button v-if="row.status===1" type="warning" size="small" @click="openHandle(row, 2)" v-permission="'complaint:list:edit'">处理</el-button>
            <el-button v-if="row.status===1 || row.status===2" type="success" size="small" @click="openHandle(row, 3)" v-permission="'complaint:list:edit'">回复</el-button>
            <el-button v-if="row.status===3 && row.creatorId === userId" type="success" size="small" @click="openEvaluate(row)" v-permission="'complaint:list:add'">确认评价</el-button>
            <el-button v-if="row.status===3 && row.creatorId !== userId" type="success" size="small" @click="confirmComplete(row)" v-permission="'complaint:list:edit'">确认</el-button>
            <el-button v-if="row.status===0 || row.status===1" size="small" @click="confirmCancel(row)" v-permission="'complaint:list:add'">撤销</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'complaint:list:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" style="width:100%">
            <el-option label="投诉" :value="1" /><el-option label="建议" :value="2" /><el-option label="咨询" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="分类" prop="category">
          <el-select v-model="form.category" style="width:100%" placeholder="请选择分类">
            <el-option label="环境卫生" value="环境卫生" /><el-option label="噪音扰民" value="噪音扰民" /><el-option label="车辆管理" value="车辆管理" /><el-option label="服务态度" value="服务态度" /><el-option label="设施损坏" value="设施损坏" /><el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入投诉/建议内容" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.images" placeholder="图片URL(多个逗号分隔)" />
        </el-form-item>
        <el-form-item label="匿名">
          <el-switch v-model="anonymous" active-text="匿名提交" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="处理投诉" v-model="handleDialogVisible" width="500px">
      <el-form :model="handleForm" label-width="100px">
        <el-form-item label="目标状态">
          <el-tag>{{ statusText(handleForm.status) }}</el-tag>
        </el-form-item>
        <el-form-item v-if="handleForm.status===1" label="处理人">
          <el-select v-model="handleForm.handlerId" placeholder="请选择处理人" style="width:100%">
            <el-option v-for="u in workers" :key="u.id" :label="u.realName + '（' + u.username + '）'" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="handleForm.status===3" label="回复内容">
          <el-input v-model="handleForm.handleContent" type="textarea" :rows="4" placeholder="请填写回复内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="handleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitHandle">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="确认并评价" v-model="evaluateDialogVisible" width="460px">
      <el-form :model="evaluateForm" label-width="90px">
        <el-form-item label="评分">
          <el-rate v-model="evaluateForm.score" :max="5" show-text />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="evaluateForm.content" type="textarea" :rows="3" placeholder="选填" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="evaluateDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitEvaluate">确认提交</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="投诉详情" v-model="detailVisible" width="640px">
      <el-descriptions v-if="detailRow" :column="2" border>
        <el-descriptions-item label="单号">{{ detailRow.complaintNo }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTag(detailRow.status)">{{ statusText(detailRow.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="业主">{{ detailRow.ownerName }}</el-descriptions-item>
        <el-descriptions-item label="房屋">{{ detailRow.roomNo || '-' }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ typeText(detailRow.type) }}</el-descriptions-item>
        <el-descriptions-item label="优先级">{{ priorityText(detailRow.priority) }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detailRow.category || '-' }}</el-descriptions-item>
        <el-descriptions-item label="匿名">{{ detailRow.isAnonymous ? '是' : '否' }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detailRow.content }}</el-descriptions-item>
        <el-descriptions-item label="图片" :span="2">{{ detailRow.images || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailRow.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ detailRow.handleTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理内容" :span="2">{{ detailRow.handleContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分">{{ detailRow.evaluateScore ? detailRow.evaluateScore + ' 分' : '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价时间">{{ detailRow.evaluateTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评价内容" :span="2">{{ detailRow.evaluateContent || '-' }}</el-descriptions-item>
        <el-descriptions-item label="提交时间" :span="2">{{ detailRow.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addComplaint, updateComplaint, deleteComplaint, getComplaintPage, getComplaintById, updateComplaintStatus, evaluateComplaint } from '@/api/complaint/suggest'
import { getOwnerPage } from '@/api/community/owner'
import { getUserPage } from '@/api/system/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const hasOwnerPerm = computed(() => userStore.hasPermission('community:owner:list'))
const userId = computed(() => userStore.userInfo.id || userStore.userInfo.userId)

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const handleDialogVisible = ref(false)
const evaluateDialogVisible = ref(false)
const detailVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const owners = ref([])
const workers = ref([])
const currentRow = ref(null)
const detailRow = ref(null)
const anonymous = ref(false)

const searchForm = reactive({ pageNum: 1, pageSize: 10, ownerId: '', type: '', status: '' })
const form = reactive({ id: null, type: 1, category: '', content: '', images: '', isAnonymous: 0 })
const handleForm = reactive({ status: 1, handlerId: null, handleContent: '' })
const evaluateForm = reactive({ score: 5, content: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑' : '新增投诉')

const typeText = (t) => ({ 1: '投诉', 2: '建议', 3: '咨询' }[t] || '')
const statusText = (s) => ({ 0: '待受理', 1: '已受理', 2: '处理中', 3: '已回复', 4: '已完成', 5: '已撤销' }[s] || '')
const statusTag = (s) => ({ 0: 'danger', 1: 'warning', 2: 'warning', 3: 'primary', 4: 'success', 5: 'info' }[s] || 'info')
const priorityText = (p) => ({ 1: '普通', 2: '重要', 3: '紧急' }[p] || '')

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  category: [{ required: true, message: '请选择分类', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(async () => {
  fetchData()
  if (hasOwnerPerm.value) {
    const oRes = await getOwnerPage({ pageNum: 1, pageSize: 200 }, { silent: true })
    owners.value = oRes.data?.records || []
  }
  const wRes = await getUserPage({ pageNum: 1, pageSize: 200 }, { silent: true })
  workers.value = wRes.data?.records || []
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getComplaintPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.ownerId = ''; searchForm.type = ''; searchForm.status = ''; handleSearch() }

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row, isAnonymous: row.isAnonymous || 0 })
  anonymous.value = !!row.isAnonymous
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  form.id = null
  form.type = 1
  form.category = ''
  form.content = ''
  form.images = ''
  form.isAnonymous = 0
  anonymous.value = false
}

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  form.isAnonymous = anonymous.value ? 1 : 0
  try {
    if (isEdit.value) await updateComplaint(form)
    else await addComplaint(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '提交成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}

async function handleDetail(row) {
  try {
    const res = await getComplaintById(row.id)
    detailRow.value = res.data
    detailVisible.value = true
  } catch (e) { /* handled */ }
}

function openHandle(row, status) {
  currentRow.value = row
  handleForm.status = status
  handleForm.handlerId = null
  handleForm.handleContent = ''
  handleDialogVisible.value = true
}

async function submitHandle() {
  try {
    await updateComplaintStatus({ id: currentRow.value.id, ...handleForm })
    ElMessage.success('操作成功')
    handleDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function confirmComplete(row) {
  await ElMessageBox.confirm('确认该投诉已完成？', '提示', { type: 'warning' })
  try {
    await updateComplaintStatus({ id: row.id, status: 4 })
    ElMessage.success('已确认完成')
    fetchData()
  } catch (e) { /* handled */ }
}

function openEvaluate(row) {
  evaluateForm.score = 5
  evaluateForm.content = ''
  currentRow.value = row
  evaluateDialogVisible.value = true
}

async function submitEvaluate() {
  try {
    await evaluateComplaint({ id: currentRow.value.id, score: evaluateForm.score, content: evaluateForm.content })
    ElMessage.success('评价成功，投诉已完成')
    evaluateDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function confirmCancel(row) {
  await ElMessageBox.confirm('确认撤销该投诉？', '提示', { type: 'warning' })
  try {
    await updateComplaintStatus({ id: row.id, status: 5 })
    ElMessage.success('已撤销')
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该投诉？', '提示', { type: 'warning' })
  try {
    await deleteComplaint(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
