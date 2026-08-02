<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="姓名">
        <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable />
      </el-form-item>
      <el-form-item label="电话">
        <el-input v-model="searchForm.phone" placeholder="请输入电话" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'community:owner:add'">新增业主</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="username" label="关联账号" width="140" />
        <el-table-column prop="name" label="姓名" width="120" />
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="idCard" label="身份证号" width="200" />
        <el-table-column label="业主类型" width="100">
          <template #default="{ row }">
            <el-tag :type="ownerTypeTag(row.ownerType)">{{ ownerTypeLabel(row.ownerType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '正常' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="160" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'community:owner:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'community:owner:delete'">删除</el-button>
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
        <el-form-item label="关联用户" prop="userId">
          <el-select v-model="form.userId" placeholder="请选择关联用户" filterable style="width: 100%" @change="onUserSelect">
            <el-option v-for="u in ownerUserList" :key="u.id" :label="u.username + '（' + (u.realName || '未设置') + '）'" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="姓名" prop="name">
          <el-input v-model="form.name" placeholder="请输入姓名" />
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item label="身份证号" prop="idCard">
          <el-input v-model="form.idCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-form-item label="业主类型" prop="ownerType">
          <el-select v-model="form.ownerType" placeholder="请选择业主类型" style="width: 100%">
            <el-option label="本人" :value="1" />
            <el-option label="家属" :value="2" />
            <el-option label="租客" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status"><el-radio :value="1">正常</el-radio><el-radio :value="0">禁用</el-radio></el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
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
import { addOwner, updateOwner, deleteOwner, getOwnerPage } from '@/api/community/owner'
import { getOwnerUsers } from '@/api/system/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const ownerUserList = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, name: '', phone: '' })
const form = reactive({ id: null, userId: null, name: '', phone: '', idCard: '', ownerType: 1, status: 1, remark: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑业主' : '新增业主')

const ownerTypeMap = { 1: '本人', 2: '家属', 3: '租客' }
const ownerTypeTagMap = { 1: '', 2: 'warning', 3: 'info' }
function ownerTypeLabel(v) { return ownerTypeMap[v] || '未知' }
function ownerTypeTag(v) { return ownerTypeTagMap[v] || '' }

const rules = {
  userId: [{ required: true, message: '请选择关联用户', trigger: 'change' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入电话', trigger: 'blur' }],
  idCard: [{ required: true, message: '请输入身份证号', trigger: 'blur' }]
}

onMounted(() => { fetchData(); loadOwnerUsers() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getOwnerPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadOwnerUsers() {
  try {
    const res = await getOwnerUsers()
    ownerUserList.value = res.data || []
  } catch (e) { /* ignore */ }
}

function onUserSelect(userId) {
  if (!userId) return
  const user = ownerUserList.value.find(u => u.id === userId)
  if (user && user.realName) {
    form.name = user.realName
  }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.name = ''; searchForm.phone = ''; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.ownerType = 1; form.status = 1 }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateOwner(form) }
    else { await addOwner(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该业主吗？', '提示', { type: 'warning' })
  try {
    await deleteOwner(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
