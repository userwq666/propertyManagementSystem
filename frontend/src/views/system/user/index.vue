<template>
  <div class="app-container">
    <div class="page-header">
      <h1>用户管理</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增用户</el-button>
    </div>

    <el-card>
      <el-form :model="queryParams" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="用户名">
          <el-input v-model="queryParams.username" placeholder="用户名" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="全部" clearable style="width:120px">
            <el-option label="正常" :value="1" />
            <el-option label="禁用" :value="0" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="realName" label="真实姓名" width="100" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="userType" label="用户类型" width="100" align="center">
          <template #default="{ row }">
            <el-tag :type="userTypeColor(row.userType)" size="small">{{ userTypeLabel(row.userType) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80" align="center">
          <template #default="{ row }">
            <el-switch :model-value="row.status===1" @change="(v) => handleStatusChange(row, v)" />
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="角色" min-width="140">
          <template #default="{ row }">{{ (row.roles||[]).join(', ') }}</template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" size="small" link @click="handleResetPwd(row)">重置密码</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="handleQuery" @current-change="handleQuery" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="formRules" label-width="90px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="dialog.form.username" placeholder="登录用户名" :disabled="!dialog.isAdd" />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realName">
          <el-input v-model="dialog.form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="密码" :prop="dialog.isAdd?'password':''">
          <el-input v-model="dialog.form.password" type="password" placeholder="密码" show-password />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="dialog.form.phone" placeholder="手机号" />
        </el-form-item>
        <el-form-item label="用户类型" prop="userType">
          <el-select v-model="dialog.form.userType" placeholder="选择用户类型" style="width:100%">
            <el-option label="超级管理员" :value="1" />
            <el-option label="物业管理员" :value="2" />
            <el-option label="业主" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="dialog.form.status">
            <el-radio :value="1">正常</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="角色" prop="roleIds">
          <el-select v-model="dialog.form.roleIds" multiple placeholder="选择角色" style="width:100%">
            <el-option v-for="r in roleOptions" :key="r.id" :label="r.roleName" :value="r.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="dialog.loading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 重置密码弹窗 -->
    <el-dialog v-model="pwdDialog.visible" title="重置密码" width="400px">
      <el-form ref="pwdFormRef" :model="pwdDialog" :rules="pwdRules" label-width="90px">
        <el-form-item label="用户名">{{ pwdDialog.username }}</el-form-item>
        <el-form-item label="新密码" prop="password">
          <el-input v-model="pwdDialog.password" type="password" placeholder="新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="pwdDialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="pwdDialog.loading" @click="submitResetPwd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { getUserList, getUserInfo, addUser, updateUser, deleteUser, resetPassword, updateStatus, getRoleList } from '@/api/system/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const roleOptions = ref([])
const formRef = ref(null)
const pwdFormRef = ref(null)

const queryParams = reactive({ pageNum:1, pageSize:10, username:'', status:null })

const dialog = reactive({
  visible:false, title:'', isAdd:true, loading:false,
  form: { id:null, username:'', realName:'', password:'', phone:'', userType:3, status:1, roleIds:[] }
})

const pwdDialog = reactive({ visible:false, loading:false, userId:null, username:'', password:'' })

const formRules = {
  username: [{ required:true, message:'请输入用户名', trigger: ['blur','change'] }],
  realName: [{ required:true, message:'请输入真实姓名', trigger: ['blur','change'] }],
  password: [{ required:true, message:'请输入密码', trigger:'blur' }, { min:6, message:'密码至少6位', trigger: ['blur','change'] }],
  phone: [{ pattern:/^1[3-9]\d{9}$/, message:'手机号格式不正确', trigger: ['blur','change'] }],
  userType: [{ required:true, message:'请选择用户类型', trigger:'change' }]
}
const pwdRules = { password: [{ required:true, message:'请输入新密码', trigger:'blur' }, { min:6, message:'密码至少6位', trigger: ['blur','change'] }] }

const userTypeColor = (t) => ({1:'', 2:'success', 3:'warning'})[t]||''
const userTypeLabel = (t) => ({1:'超级管理员', 2:'物业管理员', 3:'业主'})[t]||'未知'

const loadData = async () => {
  loading.value = true
  try { const res = await getUserList(queryParams); tableData.value = res.data.records||[]; total.value = res.data.total||0 } catch { tableData.value = [] }
  loading.value = false
}
const loadRoles = async () => {
  try { const res = await getRoleList({}); roleOptions.value = res.data||[] } catch {}
}
const handleQuery = () => { queryParams.pageNum=1; loadData() }
const resetQuery = () => { Object.assign(queryParams,{ pageNum:1,pageSize:10,username:'',status:null }); loadData() }

const resetForm = () => {
  dialog.form = { id:null, username:'', realName:'', password:'', phone:'', userType:3, status:1, roleIds:[] }
  formRef.value?.resetFields()
}

const handleAdd = () => {
  dialog.title='新增用户'; dialog.isAdd=true; resetForm(); dialog.visible=true
}

const handleEdit = async (row) => {
  dialog.title='编辑用户'; dialog.isAdd=false
  try {
    const res = await getUserInfo(row.id)
    const u = res.data
    dialog.form = {
      id: u.id, username: u.username, realName: u.realName, password: '',
      phone: u.phone||'', userType: typeof u.userType==='number'?u.userType:(u.userType?.value||3),
      status: typeof u.status==='number'?u.status:(u.status?.value??1), roleIds: u.roleIds||[]
    }
    dialog.visible = true
  } catch {}
}

const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try {
    const data = { ...dialog.form }
    if (!data.password) delete data.password  // 编辑时不传空密码
    if (dialog.isAdd) { await addUser(data) } else { await updateUser(data) }
    ElMessage.success(dialog.isAdd?'新增成功':'修改成功')
    dialog.visible = false; loadData()
  } catch {}
  dialog.loading = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除用户 "${row.username}" 吗？`, '提示', { type:'warning' })
    await deleteUser(row.id)
    ElMessage.success('删除成功'); loadData()
  } catch {}
}

const handleStatusChange = async (row, v) => {
  try {
    await updateStatus(row.id, v?1:0)
    ElMessage.success(v?'已启用':'已禁用'); loadData()
  } catch {}
}

const handleResetPwd = (row) => {
  pwdDialog.userId = row.id; pwdDialog.username = row.username; pwdDialog.password = ''; pwdDialog.visible = true
}

const submitResetPwd = async () => {
  try { await pwdFormRef.value.validate() } catch { return }
  pwdDialog.loading = true
  try {
    await resetPassword(pwdDialog.userId, pwdDialog.password)
    ElMessage.success('密码重置成功'); pwdDialog.visible = false
  } catch {}
  pwdDialog.loading = false
}

onMounted(() => { loadData(); loadRoles() })
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;
  h1 { font-size:20px; font-weight:600; color:#303133; margin:0; }
}
.search-form { margin-bottom:16px; }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>