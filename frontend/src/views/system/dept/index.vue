<template>
  <div class="app-container">
    <div class="page-header">
      <h1>部门管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>部门列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['system:dept:add']">
              <plus /> 新增
            </el-button>
            <el-button type="success" @click="handleExport" v-permission="['system:dept:export']">
              <download /> 导出
            </el-button>
            <el-button type="warning" @click="handleRefresh" v-permission="['system:dept:list']">
              <refresh /> 刷新
            </el-button>
            <el-button type="info" @click="expandAll" v-permission="['system:dept:list']">
              <full-screen :class="expanded ? 'rotate-180' : ''" /> 展开/折叠
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="部门名称" prop="deptName">
          <el-input v-model="queryParams.deptName" placeholder="请输入部门名称" clearable style="width: 200px" />
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

      <!-- 树形表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="deptId"
        tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-expand-all
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="deptId" label="部门ID" width="80" align="center" />
        <el-table-column prop="deptName" label="部门名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="leader" label="负责人" min-width="100" show-overflow-tooltip />
        <el-table-column prop="phone" label="联系电话" min-width="130" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="80" align="center" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link icon="Plus" @click="handleAddChild(scope.row)" v-permission="['system:dept:add']">新增下级</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="primary" link icon="Edit" @click="handleUpdate(scope.row)" v-permission="['system:dept:edit']">修改</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="success" link icon="SwitchButton" @click="handleToggleStatus(scope.row)" v-permission="['system:dept:edit']">
              {{ scope.row.status === '0' ? '停用' : '启用' }}
            </el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="danger" link icon="Delete" @click="handleDelete(scope.row)" v-permission="['system:dept:remove']">删除</el-button>
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

    <!-- 新增/编辑部门弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="部门ID" prop="deptId">
          <el-input v-model="form.deptId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="上级部门" prop="parentId" :rules="[{ required: true, message: '请选择上级部门', trigger: 'change' }]">
          <el-tree-select
            v-model="form.parentId"
            :props="deptTreeProps"
            :data="parentDeptTreeData"
            placeholder="请选择上级部门"
            style="width: 100%"
            check-strictly
          />
        </el-form-item>

        <el-form-item label="部门名称" prop="deptName" :rules="[{ required: true, message: '请输入部门名称', trigger: 'blur' }]">
          <el-input v-model="form.deptName" placeholder="请输入部门名称" />
        </el-form-item>

        <el-form-item label="负责人" prop="leader" :rules="[{ required: true, message: '请输入负责人', trigger: 'blur' }]">
          <el-input v-model="form.leader" placeholder="请输入负责人" />
        </el-form-item>

        <el-form-item label="联系电话" prop="phone" :rules="[{ required: true, message: '请输入联系电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }]">
          <el-input v-model="form.phone" placeholder="请输入联系电话" />
        </el-form-item>

        <el-form-item label="邮箱" prop="email" :rules="[{ required: true, message: '请输入邮箱', trigger: 'blur' }, { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }]">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>

        <el-form-item label="显示顺序" prop="orderNum" :rules="[{ required: true, message: '请输入显示顺序', trigger: 'blur' }, { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }]">
          <el-input-number v-model="form.orderNum" :min="1" :max="9999" style="width: 100%" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Refresh, FullScreen, Search, Edit, SwitchButton, Delete
} from '@element-plus/icons-vue'
import {
  getDeptList,
  getDeptTree,
  getDeptInfo,
  addDept,
  updateDept,
  deleteDept,
  exportDept
} from '@/api/system/dept'
import { usePermission } from '@/hooks/usePermission'
import type { Dept, DeptQueryParams, DeptFormData, DeptTreeNode } from '@/types/system/dept'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref<Dept[]>([])
const total = ref(0)
const selectionIds = ref<number[]>([])

const queryParams = reactive<DeptQueryParams>({
  pageNum: 1,
  pageSize: 10,
  deptName: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增部门')
const isAdd = ref(true)

const form = reactive<DeptFormData>({
  deptId: undefined,
  parentId: 0,
  deptName: '',
  orderNum: 1,
  leader: '',
  phone: '',
  email: '',
  status: '0'
})

const rules = reactive({
  deptName: [{ required: true, message: '请输入部门名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择上级部门', trigger: 'change' }],
  leader: [{ required: true, message: '请输入负责人', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入联系电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' },
    { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 上级部门树
const parentDeptTreeData = ref<DeptTreeNode[]>([])
const deptTreeProps = ref({
  label: 'deptName',
  value: 'deptId',
  children: 'children'
})

const formRef = ref()

// 获取部门列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getDeptList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取部门列表失败:', error)
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
  queryParams.deptName = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection: Dept[]) => {
  selectionIds.value = selection.map(item => item.deptId)
}

// 获取部门树（用于上级部门选择）
const getParentDeptTree = async () => {
  try {
    const res = await getDeptTree({})
    parentDeptTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取部门树失败:', error)
  }
}

// 新增
const handleAdd = async () => {
  isAdd.value = true
  dialogTitle.value = '新增部门'
  resetForm()
  await getParentDeptTree()
  dialogVisible.value = true
}

// 新增下级部门
const handleAddChild = async (row: Dept) => {
  isAdd.value = true
  dialogTitle.value = '新增下级部门'
  resetForm()
  await getParentDeptTree()
  form.parentId = row.deptId
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row: Dept) => {
  isAdd.value = false
  dialogTitle.value = '修改部门'
  resetForm()
  await getParentDeptTree()
  try {
    const res = await getDeptInfo(row.deptId)
    const data = res.data || res
    form.deptId = data.deptId
    form.parentId = data.parentId
    form.deptName = data.deptName
    form.orderNum = data.orderNum
    form.leader = data.leader
    form.phone = data.phone
    form.email = data.email
    form.status = data.status
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取部门信息失败:', error)
  }
}

// 删除
const handleDelete = (row: Dept) => {
  const deptIds = row.deptId ? row.deptId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除部门ID为"${deptIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDept(deptIds)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 状态修改
const handleToggleStatus = async (row: Dept) => {
  const newStatus = row.status === '0' ? '1' : '0'
  try {
    await updateDept({
      deptId: row.deptId,
      status: newStatus
    })
    ElMessage.success(newStatus === '0' ? '启用成功' : '停用成功')
    getList()
  } catch (error) {
    console.error('状态切换失败:', error)
  }
}

// 导出
const handleExport = async () => {
  try {
    loading.value = true
    const res = await exportDept(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `部门数据_${new Date().getTime()}.xlsx`
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

// 刷新
const handleRefresh = () => {
  getList()
  ElMessage.success('刷新成功')
}

// 展开/折叠
let expanded = true
const expandAll = () => {
  expanded = !expanded
  getList()
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
  form.deptId = undefined
  form.parentId = 0
  form.deptName = ''
  form.orderNum = 1
  form.leader = ''
  form.phone = ''
  form.email = ''
  form.status = '0'
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
      await addDept(form)
      ElMessage.success('新增成功')
    } else {
      await updateDept(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
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

.text-muted {
  color: #909399;
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

/* 树形选择器样式 */
:deep(.el-tree-select) {
  width: 100%;
}

:deep(.el-tree-select__popper) {
  min-width: 300px;
  max-height: 400px;
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