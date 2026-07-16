<template>
  <div class="app-container">
    <div class="page-header">
      <h1>收费项目管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>收费项目列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['fee:item:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['fee:item:delete']">
              <Delete /> 批量删除
            </el-button>
            <el-button type="success" @click="handleExport" v-permission="['fee:item:export']">
              <Download /> 导出
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="90px">
        <el-form-item label="项目名称">
          <el-input
            v-model="queryParams.itemName"
            placeholder="项目名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="收费类型">
          <el-select
            v-model="queryParams.itemType"
            placeholder="收费类型"
            clearable
            style="width: 200px"
          >
            <el-option
              v-for="item in itemTypeOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="收费周期">
          <el-select
            v-model="queryParams.chargeCycle"
            placeholder="收费周期"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in chargeCycleOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select
            v-model="queryParams.status"
            placeholder="状态"
            clearable
            style="width: 160px"
          >
            <el-option
              v-for="item in statusOptions"
              :key="item.dictValue"
              :label="item.dictLabel"
              :value="item.dictValue"
            />
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

      <!-- 收费项目表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="itemId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="itemId" label="项目ID" width="80" align="center" />
        <el-table-column prop="itemName" label="项目名称" min-width="150" show-overflow-tooltip />
        <el-table-column prop="itemTypeLabel" label="收费类型" width="100" align="center" />
        <el-table-column prop="chargeCycleLabel" label="收费周期" width="100" align="center" />
        <el-table-column prop="chargeAmount" label="收费金额" width="100" align="right" >
          <template #default="scope">
            <span class="amount-text">{{ scope.row.chargeAmount }}</span>
            <span class="unit-text">{{ scope.row.chargeUnit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="billingMethodLabel" label="计费方式" width="100" align="center" />
        <el-table-column prop="applicableObjectLabel" label="适用对象" width="100" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.statusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="320" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['fee:item:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['fee:item:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleViewStandard(scope.row)"
            >
              <Document /> 收费标准
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['fee:item:edit']"
            >
              <SwitchButton />
              {{ scope.row.status === '0' ? '禁用' : '启用' }}
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

    <!-- 新增/编辑收费项目弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="800px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="110px" class="dialog-form">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目ID" prop="itemId">
              <el-input v-model="form.itemId" disabled placeholder="自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="项目名称" prop="itemName" :rules="[{ required: true, message: '请输入项目名称', trigger: 'blur' }]">
              <el-input v-model="form.itemName" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费类型" prop="itemType" :rules="[{ required: true, message: '请选择收费类型', trigger: 'change' }]">
              <el-select v-model="form.itemType" placeholder="请选择收费类型" style="width: 100%" clearable>
                <el-option
                  v-for="item in itemTypeOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费周期" prop="chargeCycle" :rules="[{ required: true, message: '请选择收费周期', trigger: 'change' }]">
              <el-select v-model="form.chargeCycle" placeholder="请选择收费周期" style="width: 100%" clearable>
                <el-option
                  v-for="item in chargeCycleOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费金额" prop="chargeAmount" :rules="[
              { required: true, message: '请输入收费金额', trigger: 'blur' },
              { type: 'number', min: 0, message: '金额必须大于等于0', trigger: 'blur' }
            ]">
              <el-input-number
                v-model="form.chargeAmount"
                :precision="2"
                :min="0"
                :step="0.01"
                placeholder="请输入收费金额"
                style="width: 100%"
                controls-position="right"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="收费单位" prop="chargeUnit" :rules="[{ required: true, message: '请选择收费单位', trigger: 'change' }]">
              <el-select v-model="form.chargeUnit" placeholder="请选择收费单位" style="width: 100%" clearable filterable>
                <el-option
                  v-for="item in chargeUnitOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计费方式" prop="billingMethod" :rules="[{ required: true, message: '请选择计费方式', trigger: 'change' }]">
              <el-select v-model="form.billingMethod" placeholder="请选择计费方式" style="width: 100%" clearable>
                <el-option
                  v-for="item in billingMethodOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="适用对象" prop="applicableObject" :rules="[{ required: true, message: '请选择适用对象', trigger: 'change' }]">
              <el-select v-model="form.applicableObject" placeholder="请选择适用对象" style="width: 100%" clearable>
                <el-option
                  v-for="item in applicableObjectOptions"
                  :key="item.dictValue"
                  :label="item.dictLabel"
                  :value="item.dictValue"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status" :rules="[{ required: true, message: '请选择状态', trigger: 'change' }]">
              <el-radio-group v-model="form.status">
                <el-radio :label="'0'">启用</el-radio>
                <el-radio :label="'1'">禁用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeDialog">取消</el-button>
          <el-button type="primary" @click="submitForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 收费标准关联弹窗 -->
    <el-dialog
      v-model="standardDialogVisible"
      :title="standardDialogTitle"
      width="1000px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <div class="standard-container">
        <el-form :model="standardQueryParams" :inline="true" class="search-form" label-width="90px">
          <el-form-item label="关联项目">
            <el-input v-model="standardQueryParams.itemName" disabled :style="{width: '300px'}" />
          </el-form-item>
          <el-form-item label="计费方式">
            <el-select
              v-model="standardQueryParams.billingMethod"
              placeholder="计费方式"
              clearable
              style="width: 200px"
            >
              <el-option
                v-for="item in billingMethodOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态">
            <el-select
              v-model="standardQueryParams.status"
              placeholder="状态"
              clearable
              style="width: 160px"
            >
              <el-option
                v-for="item in statusOptions"
                :key="item.dictValue"
                :label="item.dictLabel"
                :value="item.dictValue"
              />
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="getStandardList">
              <Search /> 查询
            </el-button>
            <el-button @click="resetStandardQuery">
              <Refresh /> 重置
            </el-button>
          </el-form-item>
        </el-form>

        <el-table
          v-loading="standardLoading"
          :data="standardTableData"
          :total="standardTotal"
          row-key="standardId"
          border
          style="width: 100%"
        >
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column prop="standardId" label="标准ID" width="80" align="center" />
          <el-table-column prop="standardName" label="标准名称" min-width="150" show-overflow-tooltip />
          <el-table-column prop="billingMethodLabel" label="计费方式" width="100" align="center" />
          <el-table-column prop="price" label="单价(元)" width="100" align="right" />
          <el-table-column prop="unit" label="计费单位" width="100" align="center" />
          <el-table-column prop="areaRange" label="面积区间" width="120" align="center" show-overflow-tooltip />
          <el-table-column prop="status" label="状态" width="90" align="center">
            <template #default="scope">
              <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
                {{ scope.row.status === '0' ? '启用' : '禁用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" min-width="150" show-overflow-tooltip />
          <el-table-column prop="createTime" label="创建时间" width="180" />
          <el-table-column label="操作" align="center" width="200" fixed="right">
            <template #default="scope">
              <el-button
                size="small"
                type="primary"
                @click="handleViewStandardDetail(scope.row)"
              >
                <View /> 查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-container">
          <el-pagination
            v-model:current-page="standardQueryParams.pageNum"
            v-model:page-size="standardQueryParams.pageSize"
            :total="standardTotal"
            :page-sizes="[10, 20, 50, 100]"
            layout="total, sizes, prev, pager, next, jumper"
            @current-change="getStandardList"
          />
        </div>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="standardDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 收费标准详情弹窗 -->
    <el-dialog
      v-model="standardDetailVisible"
      :title="standardDetailTitle"
      width="800px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-form :model="standardDetail" label-width="110px" class="dialog-form" disabled>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标准ID">
              <el-input v-model="standardDetail.standardId" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联项目">
              <el-input v-model="standardDetail.itemName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="标准名称">
              <el-input v-model="standardDetail.standardName" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计费方式">
              <el-input v-model="standardDetail.billingMethodLabel" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="单价(元)">
              <el-input v-model="standardDetail.price" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计费单位">
              <el-input v-model="standardDetail.unit" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="面积区间">
              <el-input v-model="standardDetail.areaRange" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态">
              <el-input :value="standardDetail.status === '0' ? '启用' : '禁用'" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注">
              <el-input v-model="standardDetail.remark" type="textarea" :rows="3" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建人">
              <el-input v-model="standardDetail.createBy" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-input v-model="standardDetail.createTime" disabled />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="standardDetailVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Search, Refresh, Edit, Delete, Document, SwitchButton, View
} from '@element-plus/icons-vue'
import {
  getChargeItemList,
  getChargeItemInfo,
  addChargeItem,
  updateChargeItem,
  deleteChargeItem,
  changeChargeItemStatus,
  exportChargeItem,
  getChargeStandardList
} from '@/api/fee/item'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  itemName: '',
  itemType: '',
  chargeCycle: '',
  status: '',
  beginTime: '',
  endTime: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增收费项目')
const isAdd = ref(true)

const form = reactive({
  itemId: undefined,
  itemName: '',
  itemType: '',
  chargeCycle: '',
  chargeAmount: 0,
  chargeUnit: '',
  billingMethod: '',
  applicableObject: '',
  status: '0',
  remark: ''
})

const rules = reactive({
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  itemType: [{ required: true, message: '请选择收费类型', trigger: 'change' }],
  chargeCycle: [{ required: true, message: '请选择收费周期', trigger: 'change' }],
  chargeAmount: [
    { required: true, message: '请输入收费金额', trigger: 'blur' },
    { type: 'number', min: 0, message: '金额必须大于等于0', trigger: 'blur' }
  ],
  chargeUnit: [{ required: true, message: '请选择收费单位', trigger: 'change' }],
  billingMethod: [{ required: true, message: '请选择计费方式', trigger: 'change' }],
  applicableObject: [{ required: true, message: '请选择适用对象', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref()

// 字典选项
const itemTypeOptions = ref([
  { dictValue: '1', dictLabel: '物业费' },
  { dictValue: '2', dictLabel: '车位费' },
  { dictValue: '3', dictLabel: '水电费' },
  { dictValue: '4', dictLabel: '暖气费' },
  { dictValue: '5', dictLabel: '维修基金' },
  { dictValue: '6', dictLabel: '其他' }
])

const chargeCycleOptions = ref([
  { dictValue: '1', dictLabel: '月' },
  { dictValue: '2', dictLabel: '季' },
  { dictValue: '3', dictLabel: '半年' },
  { dictValue: '4', dictLabel: '年' },
  { dictValue: '5', dictLabel: '一次性' }
])

const billingMethodOptions = ref([
  { dictValue: '1', dictLabel: '按套' },
  { dictValue: '2', dictLabel: '按面积' },
  { dictValue: '3', dictLabel: '按车位' },
  { dictValue: '4', dictLabel: '固定金额' }
])

const applicableObjectOptions = ref([
  { dictValue: '1', dictLabel: '全体' },
  { dictValue: '2', dictLabel: '住宅' },
  { dictValue: '3', dictLabel: '商铺' },
  { dictValue: '4', dictLabel: '车库' },
  { dictValue: '5', dictLabel: '办公' }
])

const chargeUnitOptions = ref([
  { dictValue: '元/月', dictLabel: '元/月' },
  { dictValue: '元/季', dictLabel: '元/季' },
  { dictValue: '元/半年', dictLabel: '元/半年' },
  { dictValue: '元/年', dictLabel: '元/年' },
  { dictValue: '元/次', dictLabel: '元/次' },
  { dictValue: '元/㎡', dictLabel: '元/㎡' },
  { dictValue: '元/车位', dictLabel: '元/车位' },
  { dictValue: '元/套', dictLabel: '元/套' }
])

const statusOptions = ref([
  { dictValue: '0', dictLabel: '启用' },
  { dictValue: '1', dictLabel: '禁用' }
])

// 标签显示用 computed
const itemTypeLabel = computed(() => {
  const map = Object.fromEntries(itemTypeOptions.value.map(o => [o.dictValue, o.dictLabel]))
  return (val) => map[val] || val
})

const chargeCycleLabel = computed(() => {
  const map = Object.fromEntries(chargeCycleOptions.value.map(o => [o.dictValue, o.dictLabel]))
  return (val) => map[val] || val
})

const billingMethodLabel = computed(() => {
  const map = Object.fromEntries(billingMethodOptions.value.map(o => [o.dictValue, o.dictLabel]))
  return (val) => map[val] || val
})

const applicableObjectLabel = computed(() => {
  const map = Object.fromEntries(applicableObjectOptions.value.map(o => [o.dictValue, o.dictLabel]))
  return (val) => map[val] || val
})

// 收费标准相关
const standardDialogVisible = ref(false)
const standardDialogTitle = ref('收费标准列表')
const currentChargeItem = ref(null)

const standardLoading = ref(false)
const standardTableData = ref([])
const standardTotal = ref(0)

const standardQueryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  itemId: undefined | undefined,
  itemName: '',
  billingMethod: '',
  status: ''
})

// 标准详情弹窗
const standardDetailVisible = ref(false)
const standardDetailTitle = ref('收费标准详情')
const standardDetail = reactive({
  standardId: undefined,
  itemName: '',
  standardName: '',
  billingMethodLabel: '',
  price: '',
  unit: '',
  areaRange: '',
  status: '',
  remark: '',
  createBy: '',
  createTime: ''
})

// 获取收费项目列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getChargeItemList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
    // 填充标签
    tableData.value.forEach(item => {
      item.itemTypeLabel = itemTypeLabel.value(item.itemType)
      item.chargeCycleLabel = chargeCycleLabel.value(item.chargeCycle)
      item.billingMethodLabel = billingMethodLabel.value(item.billingMethod)
      item.applicableObjectLabel = applicableObjectLabel.value(item.applicableObject)
      item.statusLabel = item.status === '0' ? '启用' : '禁用'
    })
  } catch (error) {
    console.error('获取收费项目列表失败:', error)
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
  queryParams.itemName = ''
  queryParams.itemType = ''
  queryParams.chargeCycle = ''
  queryParams.status = ''
  queryParams.beginTime = ''
  queryParams.endTime = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.itemId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增收费项目'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改收费项目'
  resetForm()
  try {
    const res = await getChargeItemInfo(row.itemId)
    const data = res.data || res
    form.itemId = data.itemId
    form.itemName = data.itemName
    form.itemType = data.itemform.chargeCycle = data.chargeCycle
    form.chargeAmount = data.chargeAmount
    form.chargeUnit = data.chargeUnit
    form.billingMethod = data.billingMethod
    form.applicableObject = data.applicableObject
    form.status = data.status
    form.remark = data.remark || ''
    dialogVisible.value = true
  } catch (error) {
    console.error('获取收费项目信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const itemIds = row.itemId ? row.itemId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除收费项目ID为"${itemIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteChargeItem(itemIds)
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
    ElMessage.warning('请选择要删除的收费项目')
    return
  }
  handleDelete({ itemId: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === '0' ? '1' : '0'
    await deleteChargeItem(row.itemId, newStatus)
    ElMessage.success(newStatus === '0' ? '启用成功' : '禁用成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 查看收费标准
const handleViewStandard = (row) => {
  currentChargeItem.value = row
  standardDialogTitle.value = `收费标准列表 - ${row.itemName}`
  standardQueryParams.itemId = row.itemId
  standardQueryParams.itemName = row.itemName
  standardQueryParams.billingMethod = ''
  standardQueryParams.status = ''
  standardQueryParams.pageNum = 1
  standardDialogVisible.value = true
  getStandardList()
}

// 获取收费标准列表
const getStandardList = async () => {
  standardLoading.value = true
  try {
    const res = await getChargeStandardList(standardQueryParams)
    const data = res.data || res
    const list = data.rows || data.list || []
    // 填充计费方式标签
    list.forEach(item => {
      item.billingMethodLabel = billingMethodLabel.value(item.billingMethod)
    })
    standardTableData.value = list
    standardTotal.value = data.total || 0
  } catch (error) {
    console.error('获取收费标准列表失败:', error)
  } finally {
    standardLoading.value = false
  }
}

const resetStandardQuery = () => {
  standardQueryParams.billingMethod = ''
  standardQueryParams.status = ''
  standardQueryParams.pageNum = 1
  getStandardList()
}

// 查看收费标准详情
const handleViewStandardDetail = async (row) => {
  standardDetailTitle.value = `收费标准详情 - ${row.standardName}`
  standardDetail.standardId = row.standardId
  standardDetail.itemName = currentChargeItem.value?.itemName || ''
  standardDetail.standardName = row.standardName
  standardDetail.billingMethodLabel = billingMethodLabel.value(row.billingMethod)
  standardDetail.price = row.price
  standardDetail.unit = row.unit
  standardDetail.areaRange = row.areaRange || ''
  standardDetail.status = row.status
  standardDetail.remark = row.remark || ''
  standardDetail.createBy = row.createBy || ''
  standardDetail.createTime = row.createTime || ''
  standardDetailVisible.value = true
}

// 导出
const handleExport = async () => {
  try {
    loading.value = true
    const res = await getChargeItemList(queryParams)
    const blob = new Blob([res], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `收费项目数据_${new Date().getTime()}.xlsx`
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
  form.itemId = undefined
  form.itemName = ''
  form.itemType = ''
  form.chargeCycle = ''
  form.chargeAmount = 0
  form.chargeUnit = ''
  form.billingMethod = ''
  form.applicableObject = ''
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
      await addChargeItem(form)
      ElMessage.success('新增成功')
    } else {
      await updateChargeItem(form)
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

.amount-text {
  font-weight: 500;
  color: #303133;
}

.unit-text {
  margin-left: 4px;
  color: #909399;
  font-size: 12px;
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

/* 标准容器 */
.standard-container {
  max-height: 70vh;
  overflow-y: auto;
  .search-form {
    margin-bottom: 16px;
  }
}

/* 响应式布局 */
@media (max-width: 1200px) {
  .dialog-form .el-col {
    :nth-child(n) {
      flex: 0 0 100%;
      max-width: 100%;
    }
  }
}
</style>