<template>
  <div class="app-container">
    <div class="page-header">
      <h1>菜单管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>菜单列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['system:menu:add']">
              <plus /> 新增
            </el-button>
            
            <el-button type="warning" @click="handleRefresh" v-permission="['system:menu:list']">
              <refresh /> 刷新
            </el-button>
            <el-button type="info" @click="expandAll" v-permission="['system:menu:list']">
              <full-screen :class="expanded ? 'rotate-180' : ''" /> 展开/折叠
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="queryParams.menuName" placeholder="请输入菜单名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 200px">
            <el-option label="正常" value="0" />
            <el-option label="停用" value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="显示状态" prop="visible">
          <el-select v-model="queryParams.visible" placeholder="请选择显示状态" clearable style="width: 200px">
            <el-option label="显示" value="0" />
            <el-option label="隐藏" value="1" />
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
        row-key="menuId"
        tree-props="{ children: 'children', hasChildren: 'hasChildren' }"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-expand-all
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="menuId" label="菜单ID" width="80" align="center" />
        <el-table-column prop="menuName" label="菜单名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="menuType" label="类型" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getMenuTypeTagType(scope.row.menuType)" effect="plain">
              {{ getMenuTypeLabel(scope.row.menuType) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由地址" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuType !== 'F'">{{ scope.row.path }}</span>
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="component" label="组件路径" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuType !== 'F'">{{ scope.row.component }}</span>
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="perms" label="权限标识" min-width="150" show-overflow-tooltip>
          <template #default="scope">
            <span v-if="scope.row.menuType === 'F'">{{ scope.row.perms }}</span>
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="icon" label="图标" width="80" align="center">
          <template #default="scope">
            <i v-if="scope.row.icon" :class="scope.row.icon" style="font-size: 18px" />
            <span v-else class="text-muted">--</span>
          </template>
        </el-table-column>
        <el-table-column prop="visible" label="显示" width="80" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.visible === '0' ? 'success' : 'info'" effect="plain">
              {{ scope.row.visible === '0' ? '显示' : '隐藏' }}
            </el-tag>
          </template>
        </el-table-column>
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
            <el-button size="small" type="primary" link icon="Plus" @click="handleAddChild(scope.row)" v-permission="['system:menu:add']">新增子菜单</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="primary" link icon="Edit" @click="handleUpdate(scope.row)" v-permission="['system:menu:edit']">修改</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="success" link icon="SwitchButton" @click="handleToggleStatus(scope.row)" v-permission="['system:menu:edit']">
              {{ scope.row.status === '0' ? '停用' : '启用' }}
            </el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="danger" link icon="Delete" @click="handleDelete(scope.row)" v-permission="['system:menu:remove']">删除</el-button>
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

    <!-- 新增/编辑菜单弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="菜单ID" prop="menuId">
          <el-input v-model="form.menuId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="父级菜单" prop="parentId" :rules="[{ required: true, message: '请选择父级菜单', trigger: 'change' }]">
          <el-tree-select
            v-model="form.parentId"
            :props="menuTreeProps"
            :data="parentMenuTreeData"
            placeholder="请选择父级菜单"
            style="width: 100%"
            check-strictly
          />
        </el-form-item>

        <el-form-item label="菜单名称" prop="menuName" :rules="[{ required: true, message: '请输入菜单名称', trigger: 'blur' }]">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>

        <el-form-item label="菜单类型" prop="menuType" :rules="[{ required: true, message: '请选择菜单类型', trigger: 'change' }]">
          <el-radio-group v-model="form.menuType">
            <el-radio-button :label="'M'">目录</el-radio-button>
            <el-radio-button :label="'C'">菜单</el-radio-button>
            <el-radio-button :label="'F'">按钮</el-radio-button>
          </el-radio-group>
        </el-form-item>

        <!-- 目录/菜单显示字段 -->
        <div v-if="form.menuType !== 'F'">
          <el-form-item label="路由地址" prop="path" :rules="[{ required: true, message: '请输入路由地址', trigger: 'blur' }]">
            <el-input v-model="form.path" placeholder="请输入路由地址" />
          </el-form-item>

          <el-form-item label="组件路径" prop="component" :rules="[{ required: true, message: '请输入组件路径', trigger: 'blur' }]">
            <el-input v-model="form.component" placeholder="请输入组件路径" />
          </el-form-item>

          <el-form-item label="图标" prop="icon">
            <el-input v-model="form.icon" placeholder="请输入图标类名，如：Menu" prefix-icon="icon" />
          </el-form-item>

          <el-form-item label="是否缓存" prop="isCache">
            <el-radio-group v-model="form.isCache">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="是否内嵌" prop="isFrame">
            <el-radio-group v-model="form.isFrame">
              <el-radio :label="1">是</el-radio>
              <el-radio :label="0">否</el-radio>
            </el-radio-group>
          </el-form-item>

          <el-form-item label="显示状态" prop="visible" :rules="[{ required: true, message: '请选择显示状态', trigger: 'change' }]">
            <el-radio-group v-model="form.visible">
              <el-radio :label="'0'">显示</el-radio>
              <el-radio :label="'1'">隐藏</el-radio>
            </el-radio-group>
          </el-form-item>
        </div>

        <!-- 按钮显示字段 -->
        <div v-else>
          <el-form-item label="权限标识" prop="perms" :rules="[{ required: true, message: '请输入权限标识', trigger: 'blur' }]">
            <el-input v-model="form.perms" placeholder="请输入权限标识，如：system:user:add" />
          </el-form-item>
        </div>

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
  Plus, Download, Refresh, FullScreen, Fold, Search, Edit, SwitchButton, Delete
} from '@element-plus/icons-vue'
import {
  getMenuList,
  getMenuTree,
  getMenuInfo,
  addMenu,
  updateMenu,
  deleteMenu,
} from '@/api/system/menu'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])
const multiple = ref(true)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  menuName: '',
  status: '',
  visible: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增菜单')
const isAdd = ref(true)

const form = reactive({
  menuId: undefined,
  menuName: '',
  parentId: 0,
  orderNum: 1,
  path: '',
  component: '',
  query: '',
  isFrame: 1,
  isCache: 0,
  menuType: 'C',
  visible: '0',
  status: '0',
  perms: '',
  icon: '',
  remark: ''
})

const rules = reactive({
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
  parentId: [{ required: true, message: '请选择父级菜单', trigger: 'change' }],
  menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
  orderNum: [
    { required: true, message: '请输入显示顺序', trigger: 'blur' },
    { type: 'number', message: '显示顺序必须为数字', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

// 父级菜单树
const parentMenuTreeData = ref([])
const menuTreeProps = ref({
  label: 'menuName',
  value: 'menuId',
  children: 'children'
})

const formRef = ref()

// 获取菜单列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getMenuList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取菜单列表失败:', error)
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
  queryParams.menuName = ''
  queryParams.status = ''
  queryParams.visible = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.menuId)
  multiple.value = !selection.length
}

// 获取菜单树（用于父级菜单选择）
const getParentMenuTree = async () => {
  try {
    const res = await getMenuTree({})
    parentMenuTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取菜单树失败:', error)
  }
}

// 菜单类型标签类型
const getMenuTypeTagType = (type) => {
  const map = {
    M: 'primary',
    C: 'success',
    F: 'warning'
  }
  return map[type] || 'info'
}

// 菜单类型标签文本
const getMenuTypeLabel = (type) => {
  const map = {
    M: '目录',
    C: '菜单',
    F: '按钮'
  }
  return map[type] || '未知'
}

// 新增
const handleAdd = async () => {
  isAdd.value = true
  dialogTitle.value = '新增菜单'
  resetForm()
  await getParentMenuTree()
  dialogVisible.value = true
}

// 新增子菜单
const handleAddChild = async (row) => {
  isAdd.value = true
  dialogTitle.value = '新增子菜单'
  resetForm()
  await getParentMenuTree()
  form.parentId = row.menuId
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改菜单'
  resetForm()
  await getParentMenuTree()
  try {
    const res = await getMenuInfo(row.menuId)
    const data = res.data || res
    form.menuId = data.menuId
    form.menuName = data.menuName
    form.parentId = data.parentId
    form.orderNum = data.orderNum
    form.path = data.path
    form.component = data.component
    form.query = data.query
    form.isFrame = data.isFrame
    form.isCache = data.isCache
    form.menuType = data.menuform.visible = data.visible
    form.status = data.status
    form.perms = data.perms
    form.icon = data.icon
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取菜单信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const menuIds = row.menuId ? row.menuId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除菜单ID为"${menuIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteMenu(menuIds)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 切换状态
const handleToggleStatus = async (row) => {
  const newStatus = row.status === '0' ? '1' : '0'
  try {
    await updateMenu({
      menuId: row.menuId,
      status: newStatus
    })
    ElMessage.success(newStatus === '0' ? '启用成功' : '停用成功')
    getList()
  } catch (error) {
    console.error('状态切换失败:', error)
  }
}

// 导出// 刷新
const handleRefresh = () => {
  getList()
  ElMessage.success('刷新成功')
}

// 展开/折叠
let expanded = true
const expandAll = () => {
  expanded = !expanded
  // el-table 的 default-expand-all 只有初始生效，动态控制需要用 reserve-selection 和 toggleRowExpansion
  // 这里简单刷新列表实现展开/折叠
  getList()
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
  form.menuId = undefined
  form.menuName = ''
  form.parentId = 0
  form.orderNum = 1
  form.path = ''
  form.component = ''
  form.query = ''
  form.isFrame = 1
  form.isCache = 0
  form.menuType = 'C'
  form.visible = '0'
  form.status = '0'
  form.perms = ''
  form.icon = ''
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
      await addMenu(form)
      ElMessage.success('新增成功')
    } else {
      await updateMenu(form)
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