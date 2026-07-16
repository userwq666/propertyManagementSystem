<template>
  <div class="app-container">
    <div class="page-header">
      <h1>用户管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>用户列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['system:user:add']">
              <plus /> 新增
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.userName" placeholder="用户名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="手机号码">
          <el-input v-model="queryParams.phonenumber" placeholder="手机号码" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 200px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
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

      <!-- 用户表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="userId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="userId" label="用户ID" width="80" align="center" />
        <el-table-column prop="userName" label="用户名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="nickName" label="昵称" min-width="100" show-overflow-tooltip />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="phonenumber" label="手机号码" min-width="130" />
        <el-table-column prop="sex" label="性别" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.sex === '0' ? 'info' : scope.row.sex === '1' ? 'success' : 'warning'">
              {{ sexOptions.find(item => item.value === scope.row.sex)?.label || '未知' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="dept.deptName" label="部门" min-width="120" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="240" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['system:user:edit']"
            >
              <edit /> 修改
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleAssignRole(scope.row)"
              v-permission="['system:user:assignRole']"
            >
              <user /> 分配角色
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleResetPwd(scope.row)"
              v-permission="['system:user:resetPwd']"
            >
              <lock /> 重置密码
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['system:user:remove']"
            >
              <delete /> 删除
            </el-button>
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

    <!-- 新增/编辑用户弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="用户名" prop="userName" :rules="[{ required: true, message: '请输入用户名', trigger: 'blur' }]">
          <el-input v-model="form.userName" :disabled="!isAdd" placeholder="请输入用户名" />
        </el-form-item>

        <el-form-item label="昵称" prop="nickName" :rules="[{ required: true, message: '请输入昵称', trigger: 'blur' }]">
          <el-input v-model="form.nickName" placeholder="请输入昵称" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email" :rules="[{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="手机号" prop="phonenumber" :rules="[{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]">
          <el-input v-model="form.phonenumber" placeholder="请输入手机号" />
        </el-form-item>

        <el-form-item label="性别" prop="sex" :rules="[{ required: true, message: '请选择性别', trigger: 'change' }]">
          <el-radio-group v-model="form.sex">
            <el-radio :label="'0'">男</el-radio>
            <el-radio :label="'1'">女</el-radio>
            <el-radio :label="'2'">未知</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="密码" prop="password" v-if="isAdd" :rules="[{ required: true, message: '请输入密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }]">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>

        <el-form-item label="确认密码" prop="confirmPassword" v-if="isAdd" :rules="[{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateConfirmPassword, trigger: 'blur' }]">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请再次输入密码" show-password />
        </el-form-item>

        <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
          <el-radio-group v-model="form.status">
            <el-radio :label="'0'">正常</el-radio>
            <el-radio :label="'1'">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog
      v-model="resetPwdDialogVisible"
      title="重置密码"
      width="400px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="resetPwdFormRef" :model="resetPwdForm" :rules="resetPwdRules" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="resetPwdForm.userName" disabled />
        </el-form-item>
        <el-form-item label="新密码" prop="password" :rules="[{ required: true, message: '请输入新密码', trigger: 'blur' }, { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }]">
          <el-input v-model="resetPwdForm.password" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword" :rules="[{ required: true, message: '请再次输入密码', trigger: 'blur' }, { validator: validateResetConfirmPassword, trigger: 'blur' }]">
          <el-input v-model="resetPwdForm.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="resetPwdDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitResetPwd">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog
      v-model="assignRoleDialogVisible"
      title="分配角色"
      width="600px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="assignRoleFormRef" :model="assignRoleForm" label-width="80px">
        <el-form-item label="用户名" prop="userName">
          <el-input v-model="assignRoleForm.userName" disabled />
        </el-form-item>
        <el-form-item label="角色" prop="roleIds" :rules="[{ required: true, message: '请至少选择一个角色', trigger: 'change' }]">
          <el-transfer
            v-model="assignRoleForm.roleIds"
            :data="roleOptions"
            :titles="['可选角色', '已选角色']"
            filterable
            filter-placeholder="请输入角色名称"
            :render-content="renderRoleContent"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignRoleDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssignRole">确定</el-button>
        </div>
      </template>
    </el-dialog>

    
  </div>
</template>

<script setup>
import { getRoleList } from '@/api/system/user'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  userName: '',
  phonenumber: '',
  status: '',
  deptId: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增用户')
const isAdd = ref(true)

const form = reactive({
  userId: '',
  deptId: '',
  userName: '',
  nickName: '',
  email: '',
  phonenumber: '',
  sex: '0',
  password: '',
  confirmPassword: '',
  status: '0',
  remark: '',
  roleIds: [],
  postIds: []
})

const rules = reactive({
  deptId: [{ required: true, message: '请选择部门', trigger: 'change' }],
  userName: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  nickName: [{ required: true, message: '请输入昵称', trigger: 'blur' }],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  phonenumber: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  sex: [{ required: true, message: '请选择性别', trigger: 'change' }],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const sexOptions = [
  { value: '0', label: '男' },
  { value: '1', label: '女' },
  { value: '2', label: '未知' }
]

// 部门树
const deptTreeProps = ref({
  label: 'deptName',
  value: 'deptId',
  children: 'children'
})

// 重置密码弹窗
const resetPwdDialogVisible = ref(false)
const resetPwdForm = reactive({
  userId: '',
  userName: '',
  password: '',
  confirmPassword: ''
})
const resetPwdRules = reactive({
  password: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '长度在 6 到 20 个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' }
  ]
})

// 分配角色弹窗
const assignRoleDialogVisible = ref(false)
const assignRoleForm = reactive({
  userId: '',
  userName: '',
  roleIds: []
})
const roleOptions = ref([])

// 导入弹窗

const formRef = ref(null)
const resetPwdFormRef = ref(null)
const assignRoleFormRef = ref(null)

// 验证确认密码
const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const validateResetConfirmPassword = (rule, value, callback) => {
  if (value !== resetPwdForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

// 角色渲染内容
const renderRoleContent = (option) => {
  return h('span', [
    h('el-tag', { size: 'small', type: 'info', style: 'margin-right: 8px' }, option.roleKey),
    option.roleName
  ])
}

// 获取用户列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getUserList(queryParams)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取用户列表失败:', error)
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
  queryParams.userName = ''
  queryParams.phonenumber = ''
  queryParams.status = ''
  queryParams.deptId = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.userId)
}

// 新增
const handleAdd = async () => {
  isAdd.value = true
  dialogTitle.value = '新增用户'
  resetForm()
  await getDeptTreeData()
  await getRoleList()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改用户'
  resetForm()
  await getDeptTreeData()
  await getRoleList()
  try {
    const res = await getUserInfo(row.userId)
    const data = res.data || res
    form.userId = data.userId
    form.deptId = data.deptId
    form.userName = data.userName
    form.nickName = data.nickName
    form.email = data.email
    form.phonenumber = data.phonenumber
    form.sex = data.sex
    form.status = data.status
    form.remark = data.remark
    form.roleIds = data.roleIds || []
    form.postIds = data.postIds || []
    dialogVisible.value = true
  } catch (error) {
    console.error('获取用户信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const userIds = row.userId ? row.userId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除用户ID为"${userIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(userIds)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 批量删除
const handleBatchDelete = () => {
  if (selectionIds.value.length === 0) {
    ElMessage.warning('请选择要删除的用户')
    return
  }
  handleDelete({ userId: selectionIds.value.join(',') })
}

// 重置密码
const handleResetPwd = (row) => {
  resetPwdForm.userId = row.userId
  resetPwdForm.userName = row.userName
  resetPwdForm.password = ''
  resetPwdForm.confirmPassword = ''
  resetPwdDialogVisible.value = true
}

// 分配角色
const handleAssignRole = async (row) => {
  assignRoleForm.userId = row.userId
  assignRoleForm.userName = row.userName
  assignRoleForm.roleIds = row.roleIds || []
  await getRoleList()
  assignRoleDialogVisible.value = true
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    await updateStatus(row.userId, row.status === '0' ? '1' : '0')
    ElMessage.success('修改状态成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 获取角色列表
const getRoleListData = async () => {
  try {
    const res = await getRoleList({})
    const list = res.rows || res.data?.rows || res.data || []
    roleOptions.value = list.map(item => ({
      ...item,
      key: item.roleId
    }))
  } catch (error) {
    console.error('获取角色列表失败:', error)
  }
}

// 关闭弹窗
const closeDialog = (done) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.userId = ''
  form.deptId = ''
  form.userName = ''
  form.nickName = ''
  form.email = ''
  form.phonenumber = ''
  form.sex = '0'
  form.password = ''
  form.confirmPassword = ''
  form.status = '0'
  form.remark = ''
  form.roleIds = []
  form.postIds = []
  nextTick(() => {
    if (formRef.value) {
      formRef.value.clearValidate()
    }
  })
}

// 提交表单
const submitForm = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    if (isAdd.value) {
      await addUser(form)
      ElMessage.success('新增成功')
    } else {
      await updateUser(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 提交重置密码
const submitResetPwd = async () => {
  if (!resetPwdFormRef.value) return
  try {
    await resetPwdFormRef.value.validate()
    await resetPassword(resetPwdForm.userId, resetPwdForm.password)
    ElMessage.success('重置密码成功')
    resetPwdDialogVisible.value = false
  } catch (error) {
    console.error('重置密码失败:', error)
  }
}

// 提交分配角色
const submitAssignRole = async () => {
  if (!assignRoleFormRef.value) return
  try {
    await assignRoleFormRef.value.validate()
    await updateUser({
      userId: assignRoleForm.userId,
      roleIds: assignRoleForm.roleIds
    })
    ElMessage.success('分配角色成功')
    assignRoleDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('分配角色失败:', error)
  }
}

// 初始化
onMounted(() => {
  getList()
})
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

/* 表格工具栏按钮组 */
.el-button-group {
  .el-button {
    margin-left: 8px;
    &:first-child {
      margin-left: 0;
    }
  }
}

/* 导入上传区域 */
.el-upload-dragger {
  width: 100%;
  min-height: 120px;
}

/* 角色穿梭框 */
:deep(.el-transfer-panel__list) {
  max-height: 300px;
}

/* 滚动条样式 */
:deep(.el-table__body-wrapper::-webkit-scrollbar) {
  width: 6px;
  height: 6px;
}

:deep(.el-table__body-wrapper::-webkit-scrollbar-thumb) {
  background-color: #c0c4cc;
  border-radius: 3px;
}
</style>