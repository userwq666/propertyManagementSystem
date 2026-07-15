<template>
  <div class="app-container">
    <div class="page-header">
      <h1>字典数据管理</h1>
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/system/dict' }">字典管理</el-breadcrumb-item>
        <el-breadcrumb-item>字典数据</el-breadcrumb-item>
      </el-breadcrumb>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>字典数据列表 ({{ dictTypeLabel }})</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['system:dict:add']">
              <plus /> 新增
            </el-button>
            <el-button type="success" @click="handleExport" v-permission="['system:dict:export']">
              <download /> 导出
            </el-button>
            <el-button type="warning" @click="handleRefresh" v-permission="['system:dict:list']">
              <refresh /> 刷新
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="字典标签" prop="dictLabel">
          <el-input v-model="queryParams.dictLabel" placeholder="请输入字典标签" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="字典键值" prop="dictValue">
          <el-input v-model="queryParams.dictValue" placeholder="请输入字典键值" clearable style="width: 200px" />
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

      <!-- 字典数据表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        row-key="dictCode"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-sort="{ prop: 'dictSort', order: 'ascending' }"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="dictCode" label="字典编码" width="100" align="center" />
        <el-table-column prop="dictSort" label="字典排序" width="100" align="center" sortable />
        <el-table-column prop="dictLabel" label="字典标签" min-width="180" show-overflow-tooltip />
        <el-table-column prop="dictValue" label="字典键值" min-width="120" />
        <el-table-column prop="dictType" label="字典类型" min-width="120" show-overflow-tooltip />
        <el-table-column prop="cssClass" label="CSS类名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="listClass" label="列表样式" min-width="120" show-overflow-tooltip />
        <el-table-column prop="isDefault" label="是否默认" width="100" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.isDefault === 'Y' ? 'success' : 'info'">
              {{ scope.row.isDefault === 'Y' ? '是' : '否' }}
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
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="260" fixed="right">
          <template #default="scope">
            <el-button size="small" type="primary" link icon="Edit" @click="handleUpdate(scope.row)" v-permission="['system:dict:edit']">修改</el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="success" link icon="SwitchButton" @click="handleToggleStatus(scope.row)" v-permission="['system:dict:edit']">
              {{ scope.row.status === '0' ? '停用' : '启用' }}
            </el-button>
            <el-divider direction="vertical" />
            <el-button size="small" type="danger" link icon="Delete" @click="handleDelete(scope.row)" v-permission="['system:dict:remove']">删除</el-button>
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

    <!-- 新增/编辑字典数据弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="字典编码" prop="dictCode">
          <el-input v-model="form.dictCode" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="字典排序" prop="dictSort" :rules="[{ required: true, message: '请输入字典排序', trigger: 'blur' }, { type: 'number', message: '字典排序必须为数字', trigger: 'blur' }]">
          <el-input-number v-model="form.dictSort" :min="1" :max="9999" style="width: 100%" />
        </el-form-item>

        <el-form-item label="字典标签" prop="dictLabel" :rules="[{ required: true, message: '请输入字典标签', trigger: 'blur' }]">
          <el-input v-model="form.dictLabel" placeholder="请输入字典标签" />
        </el-form-item>

        <el-form-item label="字典键值" prop="dictValue" :rules="[{ required: true, message: '请输入字典键值', trigger: 'blur' }]">
          <el-input v-model="form.dictValue" placeholder="请输入字典键值" />
        </el-form-item>

        <el-form-item label="字典类型" prop="dictType">
          <el-input v-model="form.dictType" placeholder="字典类型" disabled />
        </el-form-item>

        <el-form-item label="CSS类名" prop="cssClass">
          <el-input v-model="form.cssClass" placeholder="请输入CSS类名" />
        </el-form-item>

        <el-form-item label="列表样式" prop="listClass">
          <el-input v-model="form.listClass" placeholder="请输入列表样式" />
        </el-form-item>

        <el-form-item label="是否默认" prop="isDefault" :rules="[{ required: true, message: '请选择是否默认', trigger: 'change' }]">
          <el-radio-group v-model="form.isDefault">
            <el-radio :label="'Y'">是</el-radio>
            <el-radio :label="'N'">否</el-radio>
          </el-radio-group>
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
import { ref, reactive, onMounted, nextTick, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Refresh, Search, Edit, SwitchButton, Delete
} from '@element-plus/icons-vue'
import {
  getDictDataList,
  getDictDataInfo,
  addDictData,
  updateDictData,
  deleteDictData,
  exportDictData,
  changeDictDataStatus
} from '@/api/system/dict'
import { usePermission } from '@/hooks/usePermission'
import type { DictData, DictDataQueryParams, DictDataFormData } from '@/types/system/dict'

const { hasPermission } = usePermission()
const route = useRoute()
const router = useRouter()

// 响应式数据
const loading = ref(false)
const tableData = ref<DictData[]>([])
const total = ref(0)
const selectionIds = ref<number[]>([])

const queryParams = reactive<DictDataQueryParams>({
  pageNum: 1,
  pageSize: 10,
  dictLabel: '',
  dictValue: '',
  status: '',
  dictType: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增字典数据')
const isAdd = ref(true)

const form = reactive<DictDataFormData>({
  dictCode: undefined,
  dictSort: 1,
  dictLabel: '',
  dictValue: '',
  dictType: '',
  cssClass: '',
  listClass: '',
  isDefault: 'N',
  status: '0',
  remark: ''
})

const rules = reactive({
  dictSort: [
    { required: true, message: '请输入字典排序', trigger: 'blur' },
    { type: 'number', message: '字典排序必须为数字', trigger: 'blur' }
  ],
  dictLabel: [{ required: true, message: '请输入字典标签', trigger: 'blur' }],
  dictValue: [{ required: true, message: '请输入字典键值', trigger: 'blur' }],
  isDefault: [{ required: true, message: '请选择是否默认', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref()

// 从路由参数获取字典类型
const dictTypeParam = ref('')
const dictTypeLabel = ref('')

// 获取字典数据列表
const getList = async () => {
  if (!dictTypeParam.value) return
  
  loading.value = true
  try {
    queryParams.dictType = dictTypeParam.value
    const res = await getDictDataList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取字典数据列表失败:', error)
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
  queryParams.dictLabel = ''
  queryParams.dictValue = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection: DictData[]) => {
  selectionIds.value = selection.map(item => item.dictCode)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增字典数据'
  resetForm()
  form.dictType = dictTypeParam.value
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row: DictData) => {
  isAdd.value = false
  dialogTitle.value = '修改字典数据'
  resetForm()
  try {
    const res = await getDictDataInfo(row.dictCode)
    const data = res.data || res
    form.dictCode = data.dictCode
    form.dictSort = data.dictSort
    form.dictLabel = data.dictLabel
    form.dictValue = data.dictValue
    form.dictType = data.dictType
    form.cssClass = data.cssClass
    form.listClass = data.listClass
    form.isDefault = data.isDefault
    form.status = data.status
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取字典数据信息失败:', error)
  }
}

// 删除
const handleDelete = (row: DictData) => {
  const dictCodes = row.dictCode ? row.dictCode : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除字典编码为"${dictCodes}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteDictData(dictCodes)
      ElMessage.success('删除成功')
      getList()
    } catch (error) {
      console.error('删除失败:', error)
    }
  })
}

// 状态修改
const handleToggleStatus = async (row: DictData) => {
  const newStatus = row.status === '0' ? '1' : '0'
  try {
    await changeDictDataStatus(row.dictCode, newStatus)
    ElMessage.success(newStatus === '0' ? '启用成功' : '停用成功')
    getList()
  } catch (error) {
    console.error('状态切换失败:', error)
  }
}

// 导出
const handleExport = async () => {
  if (!dictTypeParam.value) {
    ElMessage.warning('请先选择字典类型')
    return
  }
  try {
    loading.value = true
    const res = await exportDictData({ ...queryParams, dictType: dictTypeParam.value })
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `字典数据_${dictTypeParam.value}_${new Date().getTime()}.xlsx`
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

// 关闭弹窗
const closeDialog = (done: () => void) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.dictCode = undefined
  form.dictSort = 1
  form.dictLabel = ''
  form.dictValue = ''
  form.dictType = dictTypeParam.value
  form.cssClass = ''
  form.listClass = ''
  form.isDefault = 'N'
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
      await addDictData(form)
      ElMessage.success('新增成功')
    } else {
      await updateDictData(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 监听路由参数变化
const loadDictType = () => {
  const type = route.params.dictType as string
  if (type) {
    dictTypeParam.value = type
    dictTypeLabel.value = type
    queryParams.dictType = type
    getList()
  }
}

// 监听路由变化
watch(() => route.params.dictType, (newVal) => {
  if (newVal) {
    dictTypeParam.value = newVal as string
    dictTypeLabel.value = newVal as string
    queryParams.dictType = newVal as string
    getList()
  }
})

// 初始化
onMounted(() => {
  loadDictType()
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
    margin-bottom: 10px;
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