<template>
  <div class="app-container">
    <div class="page-header">
      <h1>车位管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>车位列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['property:parking:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['property:parking:delete']">
              <Delete /> 批量删除
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="车位编号">
          <el-input v-model="queryParams.parkingNo" placeholder="车位编号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-tree-select
            v-model="queryParams.buildingId"
            :props="treeProps"
            :data="buildingTreeData"
            placeholder="请选择楼栋"
            clearable
            style="width: 200px"
            check-strictly
            show-all-levels
          />
        </el-form-item>
        <el-form-item label="车位类型">
          <el-select v-model="queryParams.parkingType" placeholder="车位类型" clearable style="width: 180px">
            <el-option label="地上" value="1" />
            <el-option label="地下" value="2" />
            <el-option label="机械" value="3" />
            <el-option label="临时" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="车位状态">
          <el-select v-model="queryParams.parkingStatus" placeholder="车位状态" clearable style="width: 180px">
            <el-option label="空闲" value="1" />
            <el-option label="已租" value="2" />
            <el-option label="自用" value="3" />
            <el-option label="维修" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="关联房屋">
          <el-tree-select
            v-model="queryParams.houseId"
            :props="houseTreeProps"
            :data="houseTreeData"
            placeholder="请选择房屋"
            clearable
            style="width: 220px"
            check-strictly
            show-all-levels
          />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 150px">
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

      <!-- 车位表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="parkingId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="parkingId" label="车位ID" width="80" align="center" />
        <el-table-column prop="parkingNo" label="车位编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="buildingName" label="楼栋名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="unitNo" label="单元号" width="80" align="center" />
        <el-table-column prop="parkingNum" label="车位号" width="100" align="center" />
        <el-table-column prop="parkingTypeLabel" label="车位类型" width="100" align="center" />
        <el-table-column prop="parkingStatusLabel" label="车位状态" width="100" align="center">
          <template #default="scope">
            <el-tag :type="getParkingStatusTagType(scope.row.parkingStatus)">
              {{ scope.row.parkingStatusLabel }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="monthlyRent" label="月租金(元)" width="110" align="right" />
        <el-table-column prop="deposit" label="押金(元)" width="110" align="right" />
        <el-table-column prop="houseNo" label="关联房屋" min-width="120" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="关联业主" width="100" align="center" />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="130" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="360" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['property:parking:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['property:parking:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleBindOwner(scope.row)"
            >
              <UserFilled /> {{ scope.row.ownerName ? '换绑业主' : '绑定业主' }}
            </el-button>
            <el-button
              size="small"
              type="info"
              @click="handleViewRent(scope.row)"
            >
              <Document /> 租赁记录
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['property:parking:edit']"
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

    <!-- 新增/编辑车位弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="车位ID" prop="parkingId">
          <el-input v-model="form.parkingId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="车位编号" prop="parkingNo" :rules="[{ required: true, message: '请输入车位编号', trigger: 'blur' }]">
          <el-input v-model="form.parkingNo" placeholder="请输入车位编号" />
        </el-form-item>

        <el-form-item label="楼栋选择" prop="buildingId" :rules="[{ required: true, message: '请选择楼栋', trigger: 'change' }]">
          <el-tree-select
            v-model="form.buildingId"
            :props="treeProps"
            :data="buildingTreeData"
            placeholder="请选择楼栋"
            style="width: 100%"
            check-strictly
            show-all-levels
          />
        </el-form-item>

        <el-form-item label="单元号" prop="unitNo" :rules="[{ required: true, message: '请输入单元号', trigger: 'blur' }]">
          <el-input v-model="form.unitNo" placeholder="请输入单元号" />
        </el-form-item>

        <el-form-item label="车位号" prop="parkingNum" :rules="[{ required: true, message: '请输入车位号', trigger: 'blur' }]">
          <el-input v-model="form.parkingNum" placeholder="请输入车位号" />
        </el-form-item>

        <el-form-item label="车位类型" prop="parkingType" :rules="[{ required: true, message: '请选择车位类型', trigger: 'change' }]">
          <el-select v-model="form.parkingType" placeholder="请选择车位类型" style="width: 100%">
            <el-option label="地上" value="1" />
            <el-option label="地下" value="2" />
            <el-option label="机械" value="3" />
            <el-option label="临时" value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="车位状态" prop="parkingStatus" :rules="[{ required: true, message: '请选择车位状态', trigger: 'change' }]">
          <el-select v-model="form.parkingStatus" placeholder="请选择车位状态" style="width: 100%">
            <el-option label="空闲" value="1" />
            <el-option label="已租" value="2" />
            <el-option label="自用" value="3" />
            <el-option label="维修" value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="月租金(元)" prop="monthlyRent" :rules="[{ required: true, message: '请输入月租金', trigger: 'blur' }, { type: 'number', min: 0, message: '月租金不能小于0', trigger: 'blur' }]">
          <el-input-number v-model="form.monthlyRent" :min="0" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="押金(元)" prop="deposit" :rules="[{ type: 'number', min: 0, message: '押金不能小于0', trigger: 'blur' }]">
          <el-input-number v-model="form.deposit" :min="0" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="关联房屋" prop="houseId">
          <el-tree-select
            v-model="form.houseId"
            :props="houseTreeProps"
            :data="houseTreeData"
            placeholder="请选择房屋"
            style="width: 100%"
            check-strictly
            show-all-levels
            clearable
          />
        </el-form-item>

        <el-form-item label="关联业主" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="绑定业主后自动填入" disabled />
        </el-form-item>

        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" :rows="3" />
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

    <!-- 绑定/换绑业主弹窗 -->
    <el-dialog
      v-model="ownerDialogVisible"
      :title="ownerDialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeOwnerDialog"
      destroy-on-close
    >
      <el-form ref="ownerFormRef" :model="ownerForm" :rules="ownerRules" label-width="100px" class="dialog-form">
        <el-form-item label="车位信息" prop="parkingInfo">
          <el-input v-model="ownerForm.parkingInfo" disabled />
        </el-form-item>

        <el-form-item label="业主姓名" prop="ownerName" :rules="[{ required: true, message: '请输入业主姓名', trigger: 'blur' }]">
          <el-input v-model="ownerForm.ownerName" placeholder="请输入业主姓名" />
        </el-form-item>

        <el-form-item label="业主电话" prop="ownerPhone" :rules="[{ required: true, message: '请输入业主电话', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }]">
          <el-input v-model="ownerForm.ownerPhone" placeholder="请输入业主电话" />
        </el-form-item>

        <el-form-item label="证件号码" prop="idCard">
          <el-input v-model="ownerForm.idCard" placeholder="请输入证件号码" />
        </el-form-item>

        <el-form-item label="租赁开始日期" prop="startDate" :rules="[{ required: true, message: '请选择开始日期', trigger: 'change' }]">
          <el-date-picker v-model="ownerForm.startDate" type="date" placeholder="选择开始日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="租赁结束日期" prop="endDate" :rules="[{ required: true, message: '请选择结束日期', trigger: 'change' }]">
          <el-date-picker v-model="ownerForm.endDate" type="date" placeholder="选择结束日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>

        <el-form-item label="月租金(元)" prop="rentAmount" :rules="[{ required: true, message: '请输入月租金', trigger: 'blur' }, { type: 'number', min: 0.01, message: '月租金必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="ownerForm.rentAmount" :min="0.01" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="押金(元)" prop="deposit" :rules="[{ type: 'number', min: 0, message: '押金不能小于0', trigger: 'blur' }]">
          <el-input-number v-model="ownerForm.deposit" :min="0" :step="100" :precision="2" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeOwnerDialog">取消</el-button>
          <el-button type="primary" @click="submitOwnerForm">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 租赁记录弹窗 -->
    <el-dialog
      v-model="rentDialogVisible"
      :title="rentDialogTitle"
      width="900px"
      :close-on-click-modal="false"
      destroy-on-close
    >
      <el-table
        v-loading="rentLoading"
        :data="rentTableData"
        border
        style="width: 100%"
        row-key="rentId"
      >
        <el-table-column prop="rentId" label="记录ID" width="80" align="center" />
        <el-table-column prop="parkingNo" label="车位编号" min-width="120" />
        <el-table-column prop="buildingName" label="楼栋" min-width="120" />
        <el-table-column prop="ownerName" label="业主姓名" width="100" align="center" />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="130" align="center" />
        <el-table-column prop="startDate" label="开始日期" width="120" align="center" />
        <el-table-column prop="endDate" label="结束日期" width="120" align="center" />
        <el-table-column prop="rentAmount" label="月租金(元)" width="110" align="right" />
        <el-table-column prop="deposit" label="押金(元)" width="100" align="right" />
        <el-table-column prop="payStatusLabel" label="缴费状态" width="100" align="center" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
      </el-table>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="rentDialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Search, Refresh, Edit, Delete, UserFilled, SwitchButton, Document
} from '@element-plus/icons-vue'
import {
  getParkingList,
  getParkingInfo,
  addParking,
  updateParking,
  deleteParking,
  bindParkingOwner,
  unbindParkingOwner,
  getParkingRentList,
} from '@/api/community/parking'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

// 树形选择器数据
const buildingTreeData = ref([])
const houseTreeData = ref([])
const treeProps = {
  children: 'children',
  label: 'buildingName',
  value: 'buildingId'
}
const houseTreeProps = {
  children: 'children',
  label: 'label',
  value: 'value'
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  parkingNo: '',
  buildingId: undefined,
  parkingType: '',
  parkingStatus: '',
  houseId: undefined,
  ownerName: '',
  ownerPhone: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增车位')
const isAdd = ref(true)

const form = reactive({
  parkingId: '',
  parkingNo: '',
  buildingId: undefined,
  unitNo: '',
  parkingNum: '',
  parkingType: '1',
  parkingStatus: '1',
  monthlyRent: 0,
  deposit: 0,
  houseId: undefined,
  ownerId: undefined,
  status: '0',
  remark: ''
})

const rules = reactive({
  parkingNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  unitNo: [{ required: true, message: '请输入单元号', trigger: 'blur' }],
  parkingNum: [{ required: true, message: '请输入车位号', trigger: 'blur' }],
  parkingType: [{ required: true, message: '请选择车位类型', trigger: 'change' }],
  parkingStatus: [{ required: true, message: '请选择车位状态', trigger: 'change' }],
  monthlyRent: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { type: 'number', min: 0, message: '月租金不能小于0', trigger: 'blur' }
  ],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref(null)

// 绑定业主弹窗
const ownerDialogVisible = ref(false)
const ownerDialogTitle = ref('绑定业主')
const currentParking = ref(null)
const ownerIsAdd = ref(true)

const ownerForm = reactive({
  parkingId: undefined,
  parkingInfo: '',
  ownerName: '',
  ownerPhone: '',
  idCard: '',
  startDate: '',
  endDate: '',
  rentAmount: 0,
  deposit: 0
})

const ownerRules = reactive({
  ownerName: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }],
  ownerPhone: [
    { required: true, message: '请输入业主电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  startDate: [{ required: true, message: '请选择开始日期', trigger: 'change' }],
  endDate: [{ required: true, message: '请选择结束日期', trigger: 'change' }],
  rentAmount: [
    { required: true, message: '请输入月租金', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '月租金必须大于0', trigger: 'blur' }
  ]
})

const ownerFormRef = ref(null)

// 租赁记录弹窗
const rentDialogVisible = ref(false)
const rentDialogTitle = ref('租赁记录')
const rentLoading = ref(false)
const rentTableData = ref([])

const getParkingStatusTagType = (status) => {
  const typeMap = {
    '1': 'success',  // 空闲
    '2': 'warning',  // 已租
    '3': 'info',     // 自用
    '4': 'danger'    // 维修
  }
  return typeMap[status] || 'info'
}

// 获取楼栋树数据
const loadBuildingTree = async () => {
  try {
    const res = await getParkingList({})
    buildingTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取楼栋树失败:', error)
  }
}

// 获取房屋树数据
const loadHouseTree = async () => {
  try {
    const res = await getParkingList({})
    houseTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取房屋树失败:', error)
  }
}

// 获取车位列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getParkingList(queryParams)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取车位列表失败:', error)
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
  queryParams.parkingNo = ''
  queryParams.buildingId = undefined
  queryParams.parkingType = ''
  queryParams.parkingStatus = ''
  queryParams.houseId = undefined
  queryParams.ownerName = ''
  queryParams.ownerPhone = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.parkingId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增车位'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改车位'
  resetForm()
  try {
    const res = await getParkingInfo(row.parkingId)
    const data = res.data || res
    form.parkingId = data.parkingId
    form.parkingNo = data.parkingNo
    form.buildingId = data.buildingId
    form.unitNo = data.unitNo
    form.parkingNum = data.parkingNum
    form.parkingType = data.parkingType
    form.parkingStatus = data.parkingStatus
    form.monthlyRent = data.monthlyRent
    form.deposit = data.deposit
    form.houseId = data.houseId
    form.ownerId = data.ownerId
    form.status = data.status
    form.remark = data.remark
    dialogVisible.value = true
  } catch (error) {
    console.error('获取车位信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const parkingIds = row.parkingId ? row.parkingId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除车位ID为"${parkingIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteParking(parkingIds)
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
    ElMessage.warning('请选择要删除的车位')
    return
  }
  handleDelete({ parkingId: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === '0' ? '1' : '0'
    await deleteParking(row.parkingId, newStatus)
    ElMessage.success('修改状态成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 绑定/换绑业主
const handleBindOwner = (row) => {
  currentParking.value = row
  ownerIsAdd.value = !row.ownerName
  ownerDialogTitle.value = row.ownerName ? '换绑业主' : '绑定业主'
  
  ownerForm.parkingId = row.parkingId
  ownerForm.parkingInfo = `${row.buildingName}-${row.unitNo}单元-${row.parkingNo}`
  ownerForm.ownerName = ''
  ownerForm.ownerPhone = ''
  ownerForm.idCard = ''
  ownerForm.startDate = ''
  ownerForm.endDate = ''
  ownerForm.rentAmount = row.monthlyRent
  ownerForm.deposit = row.deposit
  
  ownerDialogVisible.value = true
  nextTick(() => {
    if (ownerFormRef.value) {
      ownerFormRef.value.clearValidate()
    }
  })
}

// 提交绑定业主表单
const submitOwnerForm = async () => {
  if (!ownerFormRef.value) return
  try {
    await ownerFormRef.value.validate()
    const data = {
      parkingId: ownerForm.parkingId,
      ownerName: ownerForm.ownerName,
      ownerPhone: ownerForm.ownerPhone,
      idCard: ownerForm.idCard,
      startDate: ownerForm.startDate,
      endDate: ownerForm.endDate,
      rentAmount: ownerForm.rentAmount,
      deposit: ownerForm.deposit
    }
    await bindParkingOwner(data)
    ElMessage.success(ownerIsAdd.value ? '绑定成功' : '换绑成功')
    ownerDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 查看租赁记录
const handleViewRent = async (row) => {
  currentParking.value = row
  rentDialogTitle.value = `${row.buildingName}-${row.unitNo}单元-${row.parkingNo} 租赁记录`
  rentLoading.value = true
  try {
    const res = await getParkingRentList({ parkingId: row.parkingId })
    rentTableData.value = res.rows || res.data?.rows || []
  } catch (error) {
    console.error('获取租赁记录失败:', error)
  } finally {
    rentLoading.value = false
  }
  rentDialogVisible.value = true
}



// 关闭弹窗
const closeDialog = (done) => {
  if (formRef.value) {
    formRef.value.resetFields()
  }
  done()
}

const closeOwnerDialog = (done) => {
  if (ownerFormRef.value) {
    ownerFormRef.value.resetFields()
  }
  done()
}

// 重置表单
const resetForm = () => {
  form.parkingId = ''
  form.parkingNo = ''
  form.buildingId = undefined
  form.unitNo = ''
  form.parkingNum = ''
  form.parkingType = '1'
  form.parkingStatus = '1'
  form.monthlyRent = 0
  form.deposit = 0
  form.houseId = undefined
  form.ownerId = undefined
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
      await addParking(form)
      ElMessage.success('新增成功')
    } else {
      await updateParking(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 初始化
onMounted(async () => {
  await Promise.all([loadBuildingTree(), loadHouseTree()])
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