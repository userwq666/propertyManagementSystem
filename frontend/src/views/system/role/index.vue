<template>
  <div class="app-container">
    <div class="page-header">
      <h1>角色管理</h1>
      <el-button type="primary" @click="handleAdd"><el-icon><Plus /></el-icon> 新增角色</el-button>
    </div>

    <el-card>
      <el-form :model="queryParams" :inline="true" class="search-form" @keyup.enter="handleQuery">
        <el-form-item label="角色名称">
          <el-input v-model="queryParams.roleName" placeholder="角色名称" clearable style="width:160px" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="queryParams.roleKey" placeholder="权限标识" clearable style="width:160px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery"><el-icon><Search /></el-icon> 查询</el-button>
          <el-button @click="resetQuery"><el-icon><RefreshRight /></el-icon> 重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="tableData" border stripe style="width:100%">
        <el-table-column prop="id" label="ID" width="70" />
        <el-table-column prop="roleName" label="角色名称" width="140" />
        <el-table-column prop="roleKey" label="权限标识" width="140" />
        <el-table-column prop="remark" label="备注" min-width="160" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="success" size="small" link @click="handleAssignMenu(row)">分配菜单</el-button>
            <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-container">
        <el-pagination v-model:current-page="queryParams.pageNum" v-model:page-size="queryParams.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="loadData" @current-change="loadData" />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="480px" @close="resetForm">
      <el-form ref="formRef" :model="dialog.form" :rules="formRules" label-width="90px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="dialog.form.roleName" placeholder="角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="dialog.form.roleKey" :disabled="!dialog.isAdd" placeholder="权限标识" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="dialog.form.remark" type="textarea" :rows="3" placeholder="备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="dialog.loading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="menuDialog.visible" title="分配菜单" width="480px">
      <el-form label-width="90px">
        <el-form-item label="角色">{{ menuDialog.roleName }}</el-form-item>
        <el-form-item label="菜单权限">
          <el-tree ref="menuTreeRef" :data="menuTreeData" show-checkbox node-key="id" :props="{ label:'menuName', children:'children' }" :default-checked-keys="menuDialog.checkedKeys" default-expand-all />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="menuDialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="menuDialog.loading" @click="submitAssignMenu">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, RefreshRight } from '@element-plus/icons-vue'
import { getRoleList, getRoleInfo, addRole, updateRole, deleteRole, getRoleMenuIds, assignMenus } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)
const menuTreeRef = ref(null)
const menuTreeData = ref([])

const queryParams = reactive({ pageNum:1, pageSize:10, roleName:'', roleKey:'' })

const dialog = reactive({
  visible:false, title:'', isAdd:true, loading:false,
  form: { id:null, roleName:'', roleKey:'', remark:'' }
})

const menuDialog = reactive({
  visible:false, loading:false, roleId:null, roleName:'', checkedKeys:[]
})

const formRules = {
  roleName: [{ required:true, message:'请输入角色名称', trigger:'blur' }],
  roleKey: [{ required:true, message:'请输入权限标识', trigger:'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    const list = res.data||[]
    tableData.value = queryParams.roleName || queryParams.roleKey
      ? list.filter(r => (!queryParams.roleName||r.roleName.includes(queryParams.roleName)) && (!queryParams.roleKey||r.roleKey.includes(queryParams.roleKey)))
      : list
    total.value = tableData.value.length
  } catch { tableData.value = [] }
  loading.value = false
}

const loadMenuTree = async () => {
  try { const res = await getMenuTree({}); menuTreeData.value = res.data||[] } catch {}
}

const handleQuery = () => { queryParams.pageNum=1; loadData() }
const resetQuery = () => { Object.assign(queryParams,{ pageNum:1,pageSize:10,roleName:'',roleKey:'' }); loadData() }

const resetForm = () => {
  dialog.form = { id:null, roleName:'', roleKey:'', remark:'' }
  formRef.value?.resetFields()
}

const handleAdd = () => { dialog.title='新增角色'; dialog.isAdd=true; resetForm(); dialog.visible=true }

const handleEdit = async (row) => {
  dialog.title='编辑角色'; dialog.isAdd=false
  try {
    const res = await getRoleInfo(row.id)
    const d = res.data
    dialog.form = { id:d.id, roleName:d.roleName, roleKey:d.roleKey, remark:d.remark||'' }
    dialog.visible = true
  } catch {}
}

const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try {
    if (dialog.isAdd) { await addRole(dialog.form) } else { await updateRole(dialog.form) }
    ElMessage.success(dialog.isAdd?'新增成功':'修改成功')
    dialog.visible = false; loadData()
  } catch {}
  dialog.loading = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除角色 "${row.roleName}" 吗？`, '提示', { type:'warning' })
    await deleteRole(row.id)
    ElMessage.success('删除成功'); loadData()
  } catch {}
}

const handleAssignMenu = async (row) => {
  menuDialog.roleId = row.id; menuDialog.roleName = row.roleName; menuDialog.checkedKeys = []
  try {
    await loadMenuTree()
    const res = await getRoleMenuIds(row.id)
    menuDialog.checkedKeys = res.data||[]
    menuDialog.visible = true
  } catch {}
}

const submitAssignMenu = async () => {
  const keys = menuTreeRef.value?.getCheckedKeys()||[]
  const halfKeys = menuTreeRef.value?.getHalfCheckedKeys()||[]
  menuDialog.loading = true
  try {
    await assignMenus(menuDialog.roleId, [...keys, ...halfKeys])
    ElMessage.success('菜单分配成功')
    menuDialog.visible = false
  } catch {}
  menuDialog.loading = false
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.app-container { padding:20px; }
.page-header { display:flex; align-items:center; justify-content:space-between; margin-bottom:16px;
  h1 { font-size:20px; font-weight:600; color:#303133; margin:0; }
}
.search-form { margin-bottom:16px; }
.pagination-container { display:flex; justify-content:flex-end; margin-top:16px; }
</style>