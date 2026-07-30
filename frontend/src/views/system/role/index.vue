<template>
  <div>
    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'system:role:add'">新增角色</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" />
        <el-table-column prop="roleKey" label="权限标识" min-width="120" />
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="240" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'system:role:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:role:delete'">删除</el-button>
            <el-button type="warning" size="small" @click="handleAssignMenus(row)" v-permission="'system:role:edit'">分配菜单</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="form.roleName" placeholder="请输入角色名�? />
        </el-form-item>
        <el-form-item label="权限标识" prop="roleKey">
          <el-input v-model="form.roleKey" placeholder="请输入权限标�? />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备�? />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="分配菜单" v-model="menuDialogVisible" width="450px">
      <el-tree
        ref="menuTreeRef"
        :data="menuTree"
        show-checkbox
        node-key="id"
        default-expand-all
        :props="{ label: 'menuName', children: 'children' }"
      />
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="menuDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitMenuAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoleList, getRoleById, addRole, updateRole, deleteRole, getRoleMenus, assignMenus } from '@/api/system/role'
import { getMenuTree } from '@/api/system/menu'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const menuDialogVisible = ref(false)
const formRef = ref(null)
const menuTreeRef = ref(null)
const isEdit = ref(false)
const currentRoleId = ref(null)
const menuTree = ref([])

const form = reactive({ id: null, roleName: '', roleKey: '', remark: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const rules = {
  roleName: [{ required: true, message: '请输入角色名�?, trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入权限标�?, trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getRoleList()
    tableData.value = res.data
  } finally { loading.value = false }
}

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateRole(form)
    else await addRole(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该角色吗�?, '提示', { type: 'warning' })
  try { await deleteRole(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

async function handleAssignMenus(row) {
  currentRoleId.value = row.id
  const res = await getMenuTree()
  menuTree.value = res.data
  menuDialogVisible.value = true
  await nextTick()
  try {
    const menuRes = await getRoleMenus(row.id)
    menuTreeRef.value.setCheckedKeys(menuRes.data)
  } catch (e) { /* handled */ }
}

async function submitMenuAssign() {
  const checkedKeys = menuTreeRef.value.getCheckedKeys()
  const halfKeys = menuTreeRef.value.getHalfCheckedKeys()
  const menuIds = [...checkedKeys, ...halfKeys]
  try {
    await assignMenus(currentRoleId.value, menuIds)
    ElMessage.success('菜单分配成功')
    menuDialogVisible.value = false
  } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>