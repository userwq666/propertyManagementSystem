<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="业主">
        <el-select v-model="searchForm.ownerId" placeholder="请选择" clearable filterable>
          <el-option v-for="o in owners.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="类型">
        <el-select v-model="searchForm.type" placeholder="请选择" clearable>
          <el-option label="投诉" :value="1" /><el-option label="建议" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'complaint:list:add'">新增</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ownerName" label="业主" width="100" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.type==='suggestion'?'success':'danger'">{{ row.type==='suggestion'?'建议':'投诉' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="content" label="内容" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="st(row.status)">{{statusText(row.status)}}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100" />
        <el-table-column prop="handleContent" label="处理内容" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'complaint:list:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'complaint:list:delete'">删除</el-button>
            <el-button v-if="row.status===0" type="warning" size="small" @click="handleStatus(row)" v-permission="'complaint:list:edit'">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择" filterable>
            <el-option v-for="o in owners.filter(i => i.id != null)" :key="o.id" :label="o.name" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type">
            <el-option label="投诉" :value="1" /><el-option label="建议" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.images" placeholder="图片URL(多个逗号分隔)" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="处理" v-model="statusDialogVisible" width="500px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="statusForm.status">
            <el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理内容">
          <el-input v-model="statusForm.handleContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatus">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addComplaint, updateComplaint, deleteComplaint, getComplaintPage, updateComplaintStatus } from '@/api/complaint/suggest'
import { getOwnerPage } from '@/api/community/owner'

const loading = ref(false); const tableData = ref([]); const total = ref(0)
const dialogVisible = ref(false); const statusDialogVisible = ref(false)
const formRef = ref(null); const isEdit = ref(false)
const owners = ref([]); const currentRow = ref(null)

const searchForm = reactive({ pageNum: 1, pageSize: 10, ownerId: '', type: '', status: '' })
const form = reactive({ id: null, ownerId: null, houseId: null, type: 1, content: '', images: '' })
const statusForm = reactive({ status: 1, handleContent: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑' : '新增')
const st = (s) => ({ 0: 'info', 1: 'warning', 2: 'success' }[s] || 'info')
const statusText = (s) => ({ 0: '待处理', 1: '处理中', 2: '已完成' }[s] || '')

const rules = {
  ownerId: [{ required: true, message: '请选择业主', trigger: 'change' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

onMounted(async () => {
  fetchData()
  const oRes = await getOwnerPage({ pageNum: 1, pageSize: 200 })
  owners.value = oRes.data.records
})

async function fetchData() {
  loading.value = true
  try { const res = await getComplaintPage({ ...searchForm }); tableData.value = res.data.records; total.value = res.data.total } finally { loading.value = false }
}
function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.ownerId = ''; searchForm.type = ''; searchForm.status = ''; handleSearch() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.type = 1; form.houseId = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateComplaint(form)
    else await addComplaint(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除吗？', '提示', { type: 'warning' })
  try { await deleteComplaint(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

function handleStatus(row) { currentRow.value = row; statusForm.status = 1; statusForm.handleContent = ''; statusDialogVisible.value = true }
async function submitStatus() {
  try { await updateComplaintStatus({ id: currentRow.value.id, ...statusForm }); ElMessage.success('处理成功'); statusDialogVisible.value = false; fetchData() } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
