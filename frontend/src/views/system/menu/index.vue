<template>
  <div>
    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd(null)" v-permission="'system:menu:add'">新增根菜单</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading" row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" min-width="180" />
        <el-table-column prop="path" label="路径" width="160" />
        <el-table-column prop="component" label="组件" width="180" />
        <el-table-column prop="perms" label="权限标识" width="180" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.menuType === 0 ? 'primary' : row.menuType === 1 ? 'success' : 'info'">
              {{ row.menuType === 0 ? '目录' : row.menuType === 1 ? '菜单' : '按钮' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '禁用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.menuType !== 2" type="primary" size="small" @click="handleAdd(row)" v-permission="'system:menu:add'">新增子菜单</el-button>
            <el-button size="small" @click="handleEdit(row)" v-permission="'system:menu:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:menu:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="treeData"
            :props="{ label: 'menuName', value: 'id', children: 'children', disabled: (node) => node.id === 0 }"
            placeholder="不选则为根菜单"
            check-strictly
            clearable
            style="width:100%"
          />
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="form.menuType" style="width:100%">
            <el-option label="目录" :value="0" />
            <el-option label="菜单" :value="1" />
            <el-option label="按钮" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="form.menuType !== 2" label="路径">
          <el-input v-model="form.path" placeholder="请输入路径" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 1" label="组件">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item label="权限标识">
          <el-input v-model="form.perms" placeholder="如: system:user:list" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width:100%" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="form.status" style="width:100%">
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
import { getMenuTree, addMenu, updateMenu, deleteMenu } from '@/api/system/menu'

const loading = ref(false)
const tableData = ref([])
const treeData = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const form = reactive({ id: null, parentId: null, menuName: '', menuType: 1, path: '', component: '', perms: '', sort: 0, status: 1 })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑菜单' : '新增菜单')
const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getMenuTree()
    tableData.value = res.data
    treeData.value = [{ id: 0, menuName: '根节点', children: Array.isArray(res.data) ? res.data : [] }]
  } finally { loading.value = false }
}

function handleAdd(parent) {
  isEdit.value = false
  resetForm()
  form.parentId = parent ? parent.id : null
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    id: row.id,
    parentId: row.parentId || null,
    menuName: row.menuName,
    menuType: row.menuType ?? 1,
    path: row.path || '',
    component: row.component || '',
    perms: row.perms || '',
    sort: row.sort || 0,
    status: row.status ?? 1
  })
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.resetFields()
  Object.assign(form, { id: null, parentId: null, menuName: '', menuType: 1, path: '', component: '', perms: '', sort: 0, status: 1 })
}

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    const submitData = { ...form, parentId: form.parentId || null }
    if (isEdit.value) await updateMenu(submitData)
    else await addMenu(submitData)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ } finally { submitting.value = false }
}
async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该菜单吗？子菜单也会一并删除。', '提示', { type: 'warning' })
  try { await deleteMenu(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) {}
}
</script>