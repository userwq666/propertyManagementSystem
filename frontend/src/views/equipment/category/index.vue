<template>
  <div class="app-container">
    <div class="page-header">
      <h1>设备分类管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>设备分类列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['equipment:category:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['equipment:category:delete']">
              <Delete /> 批量删除
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="分类名称">
          <el-input
            v-model="queryParams.categoryName"
            placeholder="分类名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleQuery">
            <Search /> 查询
          </el-button>
          <el-button @click="resetQuery">
            <Refresh /> 重置
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 设备分类表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="id" label="ID" width="80" align="center" />
        <el-table-column prop="categoryName" label="分类名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sort" label="排序" width="80" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === 0 ? 'success' : 'danger'">
              {{ scope.row.status === 0 ? '启用' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="280" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['equipment:category:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['equipment:category:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['equipment:category:edit']"
            >
              <SwitchButton /> {{ scope.row.status === 0 ? '停用' : '启用' }}
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="queryParams.pageNum"
          v-model:page-size="queryParams.pageSize"
          :page-sizes="[10, 20, 30, 50]"
          :total="total"
          layout="total, sizes, prev, pager, next, jumper"
          @current-change="getList"
          @size-change="getList"
        />
      </div>
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :before-close="closeDialog"
    >
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="dialog-form"
      >
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" maxlength="50" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" style="width: 200px" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search, Refresh, Edit, Delete, SwitchButton } from '@element-plus/icons-vue'
import {
  getCategoryPage,
  addCategory,
  updateCategory,
  deleteCategory
} from '@/api/equipment/category'

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  categoryName: ''
})

// 表单数据
const dialogVisible = ref(false)
const dialogTitle = ref('新增设备分类')
const isAdd = ref(true)
const formRef = ref(null)

const form = reactive({
  id: undefined,
  categoryName: '',
  sort: 0
})

// 表单校验规则
const rules = {
  categoryName: [
    { required: true, message: '请输入分类名称', trigger: 'blur' },
    { max: 50, message: '分类名称不能超过50个字符', trigger: 'blur' }
  ],
  sort: [
    { type: 'number', message: '排序必须为数字', trigger: 'blur' }
  ]
}

// 获取列表数据
const getList = async () => {
  loading.value = true
  try {
    const res = await getCategoryPage(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取设备分类列表失败:', error)
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
  queryParams.categoryName = ''
  handleQuery()
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增设备分类'
  resetForm()
  dialogVisible.value = true
}

// 修改
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改设备分类'
  resetForm()
  form.id = row.id
  form.categoryName = row.categoryName
  form.sort = row.sort
  dialogVisible.value = true
}

// 删除
const handleDelete = (row) => {
  const ids = row.id ? row.id : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除设备分类ID为"${ids}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteCategory(ids)
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
    ElMessage.warning('请选择要删除的设备分类')
    return
  }
  handleDelete({ id: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === 0 ? 1 : 0
    await updateCategory({ id: row.id, status: newStatus })
    ElMessage.success(newStatus === 0 ? '启用成功' : '停用成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 选择变化
const handleSelectionChange = (val) => {
  selectionIds.value = val.map(item => item.id)
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
  form.id = undefined
  form.categoryName = ''
  form.sort = 0
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
      await addCategory({ categoryName: form.categoryName, sort: form.sort })
      ElMessage.success('新增成功')
    } else {
      await updateCategory({ id: form.id, categoryName: form.categoryName, sort: form.sort })
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

.el-button-group {
  .el-button {
    margin-left: 8px;
    &:first-child {
      margin-left: 0;
    }
  }
}
</style>
