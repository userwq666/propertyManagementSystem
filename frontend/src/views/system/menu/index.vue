<template>
  <div class="menu-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单管理</span>
          <el-button type="primary" @click="handleAdd">新增菜单</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe row-key="id" :tree-props="{ children: 'children' }">
        <el-table-column prop="menuName" label="菜单名称" width="200" />
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="path" label="路由地址" width="150" />
        <el-table-column prop="component" label="组件路径" width="150" />
        <el-table-column prop="perms" label="权限标识" width="150" />
        <el-table-column prop="menuType" label="类型" width="100">
          <template #default="{ row }">
            <el-tag v-if="row.menuType === 0" type="primary">目录</el-tag>
            <el-tag v-else-if="row.menuType === 1" type="success">菜单</el-tag>
            <el-tag v-else type="warning">按钮</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">
              {{ row.status === 1 ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="primary" link @click="handleAdd(row)">新增</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    
    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="菜单类型" prop="menuType">
          <el-radio-group v-model="form.menuType">
            <el-radio :label="0">目录</el-radio>
            <el-radio :label="1">菜单</el-radio>
            <el-radio :label="2">按钮</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="上级菜单" prop="parentId">
          <el-tree-select v-model="form.parentId" :data="menuTree" :props="{ label: 'menuName', value: 'id' }" placeholder="请选择上级菜单" clearable />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 2" label="路由地址" prop="path">
          <el-input v-model="form.path" placeholder="请输入路由地址" />
        </el-form-item>
        <el-form-item v-if="form.menuType === 1" label="组件路径" prop="component">
          <el-input v-model="form.component" placeholder="请输入组件路径" />
        </el-form-item>
        <el-form-item v-if="form.menuType !== 0" label="权限标识" prop="perms">
          <el-input v-model="form.perms" placeholder="请输入权限标识" />
        </el-form-item>
        <el-form-item label="显示排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">正常</el-radio>
            <el-radio :label="0">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getMenuList, addMenu, updateMenu, deleteMenu } from '../../../api/menu'

const loading = ref(false)
const tableData = ref([])
const menuTree = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const form = reactive({
  id: undefined,
  parentId: 0,
  menuName: '',
  path: '',
  component: '',
  perms: '',
  menuType: 0,
  sort: 0,
  status: 1
})

const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }]
}

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getMenuList()
    tableData.value = res.data
    menuTree.value = [{ id: 0, menuName: '主类目', children: res.data }]
  } finally {
    loading.value = false
  }
}

const handleAdd = (row) => {
  dialogTitle.value = '新增菜单'
  Object.assign(form, {
    id: undefined,
    parentId: row ? row.id : 0,
    menuName: '',
    path: '',
    component: '',
    perms: '',
    menuType: row ? (row.menuType === 0 ? 1 : 2) : 0,
    sort: 0,
    status: 1
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑菜单'
  Object.assign(form, { ...row })
  dialogVisible.value = true
}

const handleSubmit = () => {
  formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (form.id) {
          await updateMenu(form)
          ElMessage.success('编辑成功')
        } else {
          await addMenu(form)
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
  ElMessageBox.confirm(`确定要删除菜单"${row.menuName}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(row.id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '删除失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.menu-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
