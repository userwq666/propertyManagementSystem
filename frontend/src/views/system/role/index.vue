<template>
  <div class="app-container">
    <div class="page-header">
      <h1>角色管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>角色列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['system:role:add']">
              <plus /> 新增
            </el-button>
            <el-button type="danger" :disabled="multiple" @click="handleBatchDelete" v-permission="['system:role:remove']">
              <delete /> 删除
            </el-button>
            <el-button type="warning" @click="handleExport" v-permission="['system:role:export']">
              <download /> 导出
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="queryParams.roleName" placeholder="请输入角色名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="角色标识" prop="roleKey">
          <el-input v-model="queryParams.roleKey" placeholder="请输入角色标识" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
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

      <!-- 角色表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="roleId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="roleId" label="角色ID" width="80" align="center" />
        <el-table-column prop="roleName" label="角色名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="roleKey" label="角色标识" min-width="120" show-overflow-tooltip />
        <el-table-column prop="roleSort" label="显示顺序" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="240" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link icon="Edit" v-permission="['system:role:edit']" @click="handleUpdate(scope.row)">修改</el-button>
            <el-button size="small" type="warning" link icon="Setting" v-permission="['system:role:permission']" @click="handleAssignPerm(scope.row)">分配权限</el-button>
            <el-button size="small" type="danger" link icon="Delete" v-permission="['system:role:remove']" @click="handleDelete(scope.row)">删除</el-button>
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

    <!-- 新增/编辑角色弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="角色ID" prop="roleId">
          <el-input v-model="form.roleId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="角色名称" prop="roleName" :rules="[{ required: true, message: '请输入角色名称', trigger: 'blur' }]">
          <el-input v-model="form.roleName" placeholder="请输入角色名称" />
        </el-form-item>

        <el-form-item label="角色标识" prop="roleKey" :rules="[{ required: true, message: '请输入角色标识', trigger: 'blur' }]">
          <el-input v-model="form.roleKey" :disabled="!isAdd" placeholder="请输入角色标识" />
        </el-form-item>

        <el-form-item label="显示顺序" prop="roleSort" :rules="[{ required: true, message: '请输入显示顺序', trigger: 'blur' }, { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }]">
          <el-input-number v-model="form.roleSort" :min="1" :max="9999" style="width: 100%" />
        </el-form-item>

        <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
          <el-radio-group v-model="form.status">
            <el-radio :label="'0'">正常</el-radio>
            <el-radio :label="'1'">停用</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item label="菜单权限" prop="menuIds">
          <el-tree
            v-model="form.menuIds"
            :data="menuTreeData"
            :props="menuTreeProps"
            show-checkbox
            check-strictly
            default-expand-all
            highlight-current
          />
        </el-form-item>

        <el-form-item label="部门权限" prop="deptIds">
          <el-tree
            v-model="form.deptIds"
            :data="deptTreeData"
            :props="deptTreeProps"
            show-checkbox
            check-strictly
            default-expand-all
            highlight-current
          />
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

    <!-- 分配权限弹窗 -->
    <el-dialog
      v-model="assignPermDialogVisible"
      title="分配权限"
      width="500px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form ref="assignPermFormRef" :model="assignPermForm" label-width="80px">
        <el-form-item label="角色名称" prop="roleName">
          <el-input v-model="assignPermForm.roleName" disabled />
        </el-form-item>
        <el-form-item label="菜单权限" prop="menuIds" :rules="[{ required: true, message: '请至少选择一个菜单', trigger: 'change' }]">
          <el-tree
            v-model="assignPermForm.menuIds"
            :data="assignMenuTreeData"
            :props="menuTreeProps"
            show-checkbox
            check-strictly
            default-expand-all
            highlight-current
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignPermDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssignPerm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Delete, Search, Refresh, Edit, Setting
} from '@element-plus/icons-vue'
import {
  getRoleList,
  getRoleInfo,
  addRole,
  updateRole,
  deleteRole,
  getMenuTree,
  getDeptTree,
  exportRole,
  getRoleMenuTreeselect
} from '@/api/system/role'
import { usePermission } from '@/hooks/usePermission'
import type { Role, RoleQueryParams, RoleFormData, MenuTreeNode, DeptTreeNode } from '@/types/system/role'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref<Role[]>([])
const total = ref(0)
const selectionIds = ref<number[]>([])
const multiple = ref(true)

const queryParams = reactive<RoleQueryParams>({
  pageNum: 1,
  pageSize: 10,
  roleName: '',
  roleKey: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增角色')
const isAdd = ref(true)

const form = reactive<RoleFormData>({
  roleId: undefined,
  roleName: '',
  roleKey: '',
  roleSort: 1,
  status: '0',
  menuIds: [],
  deptIds: [],
  remark: ''
})

const rules = reactive({
  roleName: [{ required: true, message: '请输入角色名称', trigger: 'blur' }],
  roleKey: [{ required: true, message: '请输入角色标识', trigger: 'blur' }],
  roleSort: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' },
    { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 菜单树
const menuTreeData = ref<MenuTreeNode[]>([])
const menuTreeProps = ref({
  label: 'menuName',
  value: 'menuId',
  children: 'children'
})

// 部门树
const deptTreeData = ref<DeptTreeNode[]>([])
const deptTreeProps = ref({
  label: 'deptName',
  value: 'deptId',
  children: 'children'
})

// 分配权限弹窗
const assignPermDialogVisible = ref(false)
const assignPermForm = reactive({
  roleId: 0,
  roleName: '',
  menuIds: [] as number[]
})

const assignMenuTreeData = ref<MenuTreeNode[]>([])

const formRef = ref()
const assignPermFormRef = ref()

// 获取角色列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getRoleList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取角色列表失败:', error)
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
  queryParams.roleName = ''
  queryParams.roleKey = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection: Role[]) => {
  selectionIds.value = selection.map(item => item.roleId)
  multiple.value = !selection.length
}

// 获取菜单树
const getMenuTreeData = async () => {
  try {
    const res = await getMenuTree({})
    menuTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
  }
}

// 获取部门树
const getDeptTreeData = async () => {
  try {
    const res = await getDeptTree({})
    deptTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

// 新增
const handleAdd = async () => {
  isAdd.value = true
  dialogTitle.value = '新增角色'
  resetForm()
  await getMenuTreeData()
  await getDeptTreeData()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row: Role) => {
  isAdd.value = false
  dialogTitle.value = '修改角色'
  resetForm()
  await getMenuTreeData()
  await getDeptTreeData()
  try {
    const res = await getRoleInfo(row.roleId)
    const data = res.data || res
    form.roleId = data.roleId
    form.roleName = data.roleName
    form.roleKey = data.roleKey
    form.roleSort = data.roleSort
    form.status = data.status
    form.menuIds = data.menuIds || []
    form.deptIds = data.deptIds || []
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取角色信息失败:', error)
  }
}

// 删除
const handleDelete = (row: Role) => {
  const roleIds = row.roleId ? row.roleId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除角色ID为"${roleIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRole(roleIds)
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
    ElMessage.warning('请选择要删除的角色')
    return
  }
  handleDelete({ roleId: selectionIds.value.join(',') } as Role)
}

// 分配权限
const handleAssignPerm = async (row: Role) => {
  assignPermForm.roleId = row.roleId
  assignPermForm.roleName = row.roleName
  assignPermForm.menuIds = []
  try {
    const res = await getRoleMenuTreeselect(row.roleId)
    const data = res.data || res
    assignMenuTreeData.value = data.menus || data.menuTree || []
    assignPermForm.menuIds = data.checkedKeys || data.menuIds || []
    assignPermDialogVisible.value = true
  } catch (error) {
    console.error('获取角色菜单失败:', error)
  }
}

// 导出
const handleExport = async () => {
  try {
    loading.value = true
    const res = await exportRole(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `角色数据_${new Date().getTime()}.xlsx`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
    ElMessage.success('导出成功')
  } catch (error) {
    console.error('导出失败:', error)
    ElMessage.error('导出失败')
  } finally {
    loading.value = false
  }
}

// 关闭弹窗
const closeDialog = (done: () => void) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.roleId = undefined
  form.roleName = ''
  form.roleKey = ''
  form.roleSort = 1
  form.status = '0'
  form.menuIds = []
  form.deptIds = []
  form.remark = ''
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
      await addRole(form)
      ElMessage.success('新增成功')
    } else {
      await updateRole(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 提交分配权限
const submitAssignPerm = async () => {
  if (!assignPermFormRef.value) return
  try {
    await assignPermFormRef.value.validate()
    await updateRole({
      roleId: assignPermForm.roleId,
      roleName: assignPermForm.roleName,
      roleKey: '',
      roleSort: 0,
      status: '0',
      menuIds: assignPermForm.menuIds,
      deptIds: [],
      remark: ''
    })
    ElMessage.success('分配权限成功')
    assignPermDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('分配权限失败:', error)
  }
}

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

/* 树形组件样式 */
:deep(.el-tree) {
  max-height: 300px;
  overflow: auto;
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