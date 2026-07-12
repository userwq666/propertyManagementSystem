<template>
  <div class="role-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色管理</span>
          <el-button type="primary" @click="handleAdd">新增角色</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" width="150" />
        <el-table-column prop="roleKey" label="权限标识" width="150" />
        <el-table-column prop="remark" label="描述" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAssignMenu(row)">分配菜单</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="描述" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <!-- 分配菜单弹窗 -->
    <el-dialog v-model="menuDialogVisible" title="分配菜单" width="500px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        :props="{ label: 'menuName', children: 'children' }"
        show-checkbox
        node-key="id"
        :default-checked-keys="checkedMenuIds"
      />
      <template #footer>
        <el-button @click="menuDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleMenuSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, addRole, updateRole, deleteRole, assignMenus, getRoleMenuIds } from '../../../api/role'
import { getMenuTree } from '../../../api/menu'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const menuDialogVisible = ref(false)
const menuTreeRef = ref(null)
const menuTree = ref([])
const checkedMenuIds = ref([])
const currentRoleId = ref(null)

const form = reactive({
  id: undefined,
  roleName: '',
  roleKey: '',
  remark: ''
})

const rules = {
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限标识', trigger: 'blur' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  dialogTitle.value = '新增角色'
  Object.assign(form, {
    id: undefined,
    roleName: '',
    roleKey: '',
    remark: ''
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑角色'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateRole(form)
          ElMessage.success('编辑成功')
        } else {
          await addRole(form)
          ElMessage.success('新增成功')
        }
        dialogVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(error.message || '操作失败')
      }
    }
  })
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除角色"${row.roleName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

const handleAssignMenu = async (row) => {
  currentRoleId.value = row.id
  try {
    const [menuRes, roleMenuRes] = await Promise.all([
      getMenuTree(),
      getRoleMenuIds(row.id)
    ])
    menuTree.value = menuRes.data
    checkedMenuIds.value = roleMenuRes.data
    menuDialogVisible.value = true
  } catch (error) {
    ElMessage.error('获取菜单数据失败')
  }
}

const handleMenuSubmit = async () => {
  try {
    const checkedKeys = menuTreeRef.value.getCheckedKeys()
    const halfCheckedKeys = menuTreeRef.value.getHalfCheckedKeys()
    const menuIds = [...checkedKeys, ...halfCheckedKeys]
    await assignMenus(currentRoleId.value, menuIds)
    ElMessage.success('分配菜单成功')
    menuDialogVisible.value = false
  } catch (error) {
    ElMessage.error(error.message || '分配菜单失败')
  }
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.role-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
