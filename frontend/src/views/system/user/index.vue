<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable style="width: 120px" />
      </el-form-item>
      <el-form-item label="角色">
        <el-select v-model="searchForm.roleId" placeholder="请选择" clearable style="width: 150px">
          <el-option v-for="r in roleList.filter(i => i.id != null)" :key="r.id" :label="r.roleName" :value="r.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 150px">
          <el-option label="启用" :value="1" /><el-option label="禁用" :value="0" />
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
          <el-button type="primary" @click="handleAdd" v-permission="'system:user:add'">新增用户</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="roleName" label="角色" width="120" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="320" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'system:user:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:user:delete'">删除</el-button>
            <el-button type="warning" size="small" @click="handleResetPassword(row)" v-permission="'system:user:edit'">重置密码</el-button>
            <el-button size="small" @click="handleToggleStatus(row)" v-permission="'system:user:edit'">
              {{ row.status === 1 ? '禁用' : '启用' }}
            </el-button>
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="form.realName" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="密码" :prop="isEdit ? '' : 'password'">
          <el-input v-model="form.password" placeholder="新增时必填" show-password />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="角色" prop="roleId">
          <el-select v-model="form.roleId" placeholder="请选择角色">
            <el-option v-for="r in roleList.filter(i => i.id != null)" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status">
            <el-option label="启用" :value="1" /><el-option label="禁用" :value="0" />
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
import { getUserPage, getUserById, addUser, updateUser, deleteUser, resetPassword, updateUserStatus } from '@/api/system/user'
import { getRoleList } from '@/api/system/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const roleList = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, username: '', roleId: '', status: '' })
const form = reactive({ id: null, username: '', realName: '', password: '', phone: '', roleId: null, status: 1 })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑用户' : '新增用户')

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  realName: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur', min: 6, message: '密码至少6位' }],
  roleId: [{ required: true, message: '请选择角色', trigger: 'change' }]
}

onMounted(async () => {
  fetchData()
  const res = await getRoleList()
  roleList.value = res.data
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getUserPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.username = ''; searchForm.roleId = ''; searchForm.status = ''; handleSearch() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
async function handleEdit(row) {
  isEdit.value = true
  loading.value = true
  try {
    const res = await getUserById(row.id)
    const detail = res.data
    Object.assign(form, {
      id: detail.id, username: detail.username || '', realName: detail.realName || '',
      password: '', phone: detail.phone || '',
      roleId: detail.roleId || null, status: detail.status ?? 1
    })
  } catch (e) {
    Object.assign(form, { ...row, password: '', roleId: row.roleId || null })
  } finally { loading.value = false }
  dialogVisible.value = true
}
function resetForm() { formRef.value?.resetFields(); Object.assign(form, { id: null, username: '', realName: '', password: '', phone: '', roleId: null, status: 1 }) }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateUser(form)
    else await addUser(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该用户吗？', '提示', { type: 'warning' })
  try { await deleteUser(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleResetPassword(row) {
  try {
    const { value: newPassword } = await ElMessageBox.prompt('请输入新密码', '重置密码', { type: 'warning', inputType: 'password' })
    await resetPassword({ id: row.id, newPassword })
    ElMessage.success('密码重置成功')
  } catch (e) { /* handled */ }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '禁用' : '启用'
  await ElMessageBox.confirm('确认' + action + '用户 "' + row.username + '" 吗？', '提示', { type: 'warning' })
  try {
    await updateUserStatus({ id: row.id, status: newStatus })
    ElMessage.success(action + '成功')
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
