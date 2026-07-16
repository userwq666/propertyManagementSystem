<template>
  <div class="app-container">
    <div class="page-header">
      <h1>楼栋管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>楼栋列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['property:building:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['property:building:delete']">
              <Delete /> 批量删除
            </el-button>
            <el-button type="success" @click="handleExport" v-permission="['property:building:export']">
              <Download /> 导出
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="楼栋名称">
          <el-input v-model="queryParams.buildingName" placeholder="楼栋名称" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="楼栋编号">
          <el-input v-model="queryParams.buildingNo" placeholder="楼栋编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 200px">
            <el-option label="启用" value="0" />
            <el-option label="禁用" value="1" />
          </el-select>
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

      <!-- 楼栋表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="buildingId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="buildingId" label="楼栋ID" width="80" align="center" />
        <el-table-column prop="buildingName" label="楼栋名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="buildingNo" label="楼栋编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="floorCount" label="楼层数" width="80" align="center" />
        <el-table-column prop="unitCount" label="单元数" width="80" align="center" />
        <el-table-column prop="buildingArea" label="楼栋面积(㎡)" width="120" align="right" />
        <el-table-column prop="buildingAddress" label="楼栋地址" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '启用' : '禁用' }}
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
              v-permission="['property:building:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['property:building:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleViewRooms(scope.row)"
            >
              <House /> 查看房屋
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['property:building:edit']"
            >
              <SwitchButton /> {{ scope.row.status === '0' ? '禁用' : '启用' }}
            </el-button>
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

    <!-- 新增/编辑楼栋弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="楼栋ID" prop="buildingId">
          <el-input v-model="form.buildingId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="楼栋名称" prop="buildingName" :rules="[{ required: true, message: '请输入楼栋名称', trigger: 'blur' }]">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>

        <el-form-item label="楼栋编号" prop="buildingNo" :rules="[{ required: true, message: '请输入楼栋编号', trigger: 'blur' }]">
          <el-input v-model="form.buildingNo" placeholder="请输入楼栋编号" />
        </el-form-item>

        <el-form-item label="楼层数" prop="floorCount" :rules="[{ required: true, message: '请输入楼层数', trigger: 'blur' }, { type: 'number', min: 1, message: '楼层数必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.floorCount" :min="1" :max="100" style="width: 100%" />
        </el-form-item>

        <el-form-item label="单元数" prop="unitCount" :rules="[{ required: true, message: '请输入单元数', trigger: 'blur' }, { type: 'number', min: 1, message: '单元数必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.unitCount" :min="1" :max="50" style="width: 100%" />
        </el-form-item>

        <el-form-item label="楼栋面积(㎡)" prop="buildingArea" :rules="[{ required: true, message: '请输入楼栋面积', trigger: 'blur' }, { type: 'number', min: 0.01, message: '楼栋面积必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.buildingArea" :min="0.01" :step="0.01" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="楼栋地址" prop="buildingAddress">
          <el-input v-model="form.buildingAddress" placeholder="请输入楼栋地址" />
        </el-form-item>

        <el-form-item label="楼栋描述" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入楼栋描述" :rows="3" />
        </el-form-item>

        <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
          <el-radio-group v-model="form.status">
            <el-radio :label="'0'">启用</el-radio>
            <el-radio :label="'1'">禁用</el-radio>
          </el-radio-group>
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
  Plus, Download, Search, Refresh, Edit, Delete, House, SwitchButton
} from '@element-plus/icons-vue'
import {
  getBuildingList,
  getBuildingInfo,
  addBuilding,
  updateBuilding,
  deleteBuilding,
  changeBuildingStatus,
  exportBuilding
} from '@/api/community/building'
import { useRouter } from 'vue-router'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingName: '',
  buildingNo: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增楼栋')
const isAdd = ref(true)

const form = reactive({
  buildingId: '',
  buildingName: '',
  buildingNo: '',
  floorCount: 1,
  unitCount: 1,
  buildingArea: 0,
  buildingAddress: '',
  description: '',
  status: '0'
})

const rules = reactive({
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  buildingNo: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }],
  floorCount: [
    { required: true, message: '请输入楼层数', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层数必须大于0', trigger: 'blur' }
  ],
  unitCount: [
    { required: true, message: '请输入单元数', trigger: 'blur' },
    { type: 'number', min: 1, message: '单元数必须大于0', trigger: 'blur' }
  ],
  buildingArea: [
    { required: true, message: '请输入楼栋面积', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '楼栋面积必须大于0', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref(null)

// 获取楼栋列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getBuildingList(queryParams)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取楼栋列表失败:', error)
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
  queryParams.buildingName = ''
  queryParams.buildingNo = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.buildingId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增楼栋'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改楼栋'
  resetForm()
  try {
    const res = await getBuildingInfo(row.buildingId)
    const data = res.data || res
    form.buildingId = data.buildingId
    form.buildingName = data.buildingName
    form.buildingNo = data.buildingNo
    form.floorCount = data.floorCount
    form.unitCount = data.unitCount
    form.buildingArea = data.buildingArea
    form.buildingAddress = data.buildingAddress
    form.description = data.description
    form.status = data.status
    dialogVisible.value = true
  } catch (error) {
    console.error('获取楼栋信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const buildingIds = row.buildingId ? row.buildingId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除楼栋ID为"${buildingIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBuilding(buildingIds)
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
    ElMessage.warning('请选择要删除的楼栋')
    return
  }
  handleDelete({ buildingId: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === '0' ? '1' : '0'
    await deleteBuilding(row.buildingId, newStatus)
    ElMessage.success('修改状态成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 查看房屋 - 跳转到房屋页面并带楼栋参数
const handleViewRooms = (row) => {
  router.push({
    path: '/property/house',
    query: { buildingId: row.buildingId, buildingName: row.buildingName }
  })
}

// 导出
const handleExport = async () => {
  try {
    loading.value = true
    const res = await exportBuilding(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `楼栋数据_${new Date().getTime()}.xlsx`
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
const closeDialog = (done) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.buildingId = ''
  form.buildingName = ''
  form.buildingNo = ''
  form.floorCount = 1
  form.unitCount = 1
  form.buildingArea = 0
  form.buildingAddress = ''
  form.description = ''
  form.status = '0'
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
      await addBuilding(form)
      ElMessage.success('新增成功')
    } else {
      await updateBuilding(form)
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

/* 表格工具栏按钮组 */
.el-button-group {
  .el-button {
    margin-left: 8px;
    &:first-child {
      margin-left: 0;
    }
  }
}
</style>