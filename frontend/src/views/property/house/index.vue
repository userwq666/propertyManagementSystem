<template>
  <div class="app-container">
    <div class="page-header">
      <h1>房屋管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>房屋列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['property:house:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['property:house:delete']">
              <Delete /> 批量删除
            </el-button>
            <el-button type="success" @click="handleExport" v-permission="['property:house:export']">
              <Download /> 导出
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="房屋编号">
          <el-input v-model="queryParams.houseNo" placeholder="房屋编号" clearable style="width: 200px" />
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
        <el-form-item label="房屋类型">
          <el-select v-model="queryParams.houseType" placeholder="房屋类型" clearable style="width: 180px">
            <el-option label="住宅" value="1" />
            <el-option label="商铺" value="2" />
            <el-option label="车库" value="3" />
            <el-option label="办公" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="装修状态">
          <el-select v-model="queryParams.decorationStatus" placeholder="装修状态" clearable style="width: 180px">
            <el-option label="毛坯" value="1" />
            <el-option label="简装" value="2" />
            <el-option label="精装" value="3" />
            <el-option label="豪装" value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主姓名">
          <el-input v-model="queryParams.ownerName" placeholder="业主姓名" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="业主电话">
          <el-input v-model="queryParams.ownerPhone" placeholder="业主电话" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.status" placeholder="状态" clearable style="width: 180px">
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

      <!-- 房屋表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="houseId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="houseId" label="房屋ID" width="80" align="center" />
        <el-table-column prop="houseNo" label="房屋编号" min-width="120" show-overflow-tooltip />
        <el-table-column prop="buildingName" label="楼栋名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="unitNo" label="单元号" width="80" align="center" />
        <el-table-column prop="roomNo" label="房间号" width="80" align="center" />
        <el-table-column prop="floorNum" label="楼层" width="70" align="center" />
        <el-table-column prop="houseTypeLabel" label="房屋类型" width="100" align="center" />
        <el-table-column prop="buildArea" label="建筑面积(㎡)" width="120" align="right" />
        <el-table-column prop="innerArea" label="套内面积(㎡)" width="120" align="right" />
        <el-table-column prop="orientation" label="朝向" width="80" align="center" />
        <el-table-column prop="decorationStatusLabel" label="装修状态" width="100" align="center" />
        <el-table-column prop="houseStructure" label="户型结构" min-width="100" show-overflow-tooltip />
        <el-table-column prop="ownerName" label="业主姓名" width="100" align="center" />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="130" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="300" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['property:house:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['property:house:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleBindOwner(scope.row)"
            >
              <UserFilled /> 绑定业主
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['property:house:edit']"
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

    <!-- 新增/编辑房屋弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="700px"
      :close-on-click-modal="false"
      :before-close="closeDialog"
      destroy-on-close
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" class="dialog-form">
        <el-form-item label="房屋ID" prop="houseId">
          <el-input v-model="form.houseId" disabled placeholder="自动生成" />
        </el-form-item>

        <el-form-item label="房屋编号" prop="houseNo" :rules="[{ required: true, message: '请输入房屋编号', trigger: 'blur' }]">
          <el-input v-model="form.houseNo" placeholder="请输入房屋编号" />
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

        <el-form-item label="房间号" prop="roomNo" :rules="[{ required: true, message: '请输入房间号', trigger: 'blur' }]">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>

        <el-form-item label="楼层" prop="floorNum" :rules="[{ required: true, message: '请输入楼层', trigger: 'blur' }, { type: 'number', min: 1, message: '楼层必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.floorNum" :min="1" :max="100" style="width: 100%" />
        </el-form-item>

        <el-form-item label="房屋类型" prop="houseType" :rules="[{ required: true, message: '请选择房屋类型', trigger: 'change' }]">
          <el-select v-model="form.houseType" placeholder="请选择房屋类型" style="width: 100%">
            <el-option label="住宅" value="1" />
            <el-option label="商铺" value="2" />
            <el-option label="车库" value="3" />
            <el-option label="办公" value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="建筑面积(㎡)" prop="buildArea" :rules="[{ required: true, message: '请输入建筑面积', trigger: 'blur' }, { type: 'number', min: 0.01, message: '建筑面积必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.buildArea" :min="0.01" :step="0.01" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="套内面积(㎡)" prop="innerArea" :rules="[{ required: true, message: '请输入套内面积', trigger: 'blur' }, { type: 'number', min: 0.01, message: '套内面积必须大于0', trigger: 'blur' }]">
          <el-input-number v-model="form.innerArea" :min="0.01" :step="0.01" :precision="2" style="width: 100%" />
        </el-form-item>

        <el-form-item label="朝向" prop="orientation">
          <el-select v-model="form.orientation" placeholder="请选择朝向" style="width: 100%" clearable>
            <el-option label="东" value="东" />
            <el-option label="南" value="南" />
            <el-option label="西" value="西" />
            <el-option label="北" value="北" />
            <el-option label="东南" value="东南" />
            <el-option label="西南" value="西南" />
            <el-option label="东北" value="东北" />
            <el-option label="西北" value="西北" />
            <el-option label="东西" value="东西" />
            <el-option label="南北" value="南北" />
          </el-select>
        </el-form-item>

        <el-form-item label="装修状态" prop="decorationStatus" :rules="[{ required: true, message: '请选择装修状态', trigger: 'change' }]">
          <el-select v-model="form.decorationStatus" placeholder="请选择装修状态" style="width: 100%">
            <el-option label="毛坯" value="1" />
            <el-option label="简装" value="2" />
            <el-option label="精装" value="3" />
            <el-option label="豪装" value="4" />
          </el-select>
        </el-form-item>

        <el-form-item label="户型结构" prop="houseStructure" :rules="[{ required: true, message: '请输入户型结构', trigger: 'blur' }]">
          <el-input v-model="form.houseStructure" placeholder="例如：2室1厅1卫" />
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

    <!-- 绑定业主弹窗 -->
    <el-dialog
      v-model="ownerDialogVisible"
      :title="ownerDialogTitle"
      width="600px"
      :close-on-click-modal="false"
      :before-close="closeOwnerDialog"
      destroy-on-close
    >
      <el-form ref="ownerFormRef" :model="ownerForm" :rules="ownerRules" label-width="100px" class="dialog-form">
        <el-form-item label="房屋信息" prop="houseInfo">
          <el-input v-model="ownerForm.houseInfo" disabled />
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

        <el-form-item label="关系类型" prop="relationType">
          <el-select v-model="ownerForm.relationType" placeholder="请选择关系类型" style="width: 100%" clearable>
            <el-option label="本人" value="本人" />
            <el-option label="配偶" value="配偶" />
            <el-option label="子女" value="子女" />
            <el-option label="父母" value="父母" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>

        <el-form-item label="是否主业主" prop="isMain">
          <el-radio-group v-model="ownerForm.isMain">
            <el-radio :label="'Y'">是</el-radio>
            <el-radio :label="'N'">否</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeOwnerDialog">取消</el-button>
          <el-button type="primary" @click="submitOwnerForm">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Search, Refresh, Edit, Delete, UserFilled, SwitchButton
} from '@element-plus/icons-vue'
import {
  getHouseList,
  getHouseInfo,
  addHouse,
  updateHouse,
  deleteHouse,
  bindHouseOwner,
  unbindHouseOwner,
} from '@/api/community/house'
import { useRouter, useRoute } from 'vue-router'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()
const router = useRouter()
const route = useRoute()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

// 楼栋树数据
const buildingTreeData = ref([])
const treeProps = {
  children: 'children',
  label: 'buildingName',
  value: 'buildingId'
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  houseNo: '',
  buildingId: undefined,
  houseType: '',
  decorationStatus: '',
  ownerName: '',
  ownerPhone: '',
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增房屋')
const isAdd = ref(true)

const form = reactive({
  houseId: '',
  houseNo: '',
  buildingId: undefined,
  unitNo: '',
  roomNo: '',
  floorNum: 1,
  houseType: '1',
  buildArea: 0,
  innerArea: 0,
  orientation: '',
  decorationStatus: '1',
  houseStructure: '',
  status: '0'
})

const rules = reactive({
  houseNo: [{ required: true, message: '请输入房屋编号', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  unitNo: [{ required: true, message: '请输入单元号', trigger: 'blur' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  floorNum: [
    { required: true, message: '请输入楼层', trigger: 'blur' },
    { type: 'number', min: 1, message: '楼层必须大于0', trigger: 'blur' }
  ],
  houseType: [{ required: true, message: '请选择房屋类型', trigger: 'change' }],
  buildArea: [
    { required: true, message: '请输入建筑面积', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '建筑面积必须大于0', trigger: 'blur' }
  ],
  innerArea: [
    { required: true, message: '请输入套内面积', trigger: 'blur' },
    { type: 'number', min: 0.01, message: '套内面积必须大于0', trigger: 'blur' }
  ],
  decorationStatus: [{ required: true, message: '请选择装修状态', trigger: 'change' }],
  houseStructure: [{ required: true, message: '请输入户型结构', trigger: 'blur' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref(null)

// 绑定业主弹窗
const ownerDialogVisible = ref(false)
const ownerDialogTitle = ref('绑定业主')
const currentHouse = ref(null)
const ownerIsAdd = ref(true)

const ownerForm = reactive({
  houseId: undefined,
  houseInfo: '',
  ownerName: '',
  ownerPhone: '',
  idCard: '',
  relationType: '本人',
  isMain: 'Y'
})

const ownerRules = reactive({
  ownerName: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }],
  ownerPhone: [
    { required: true, message: '请输入业主电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ]
})

const ownerFormRef = ref(null)

// 获取楼栋树数据
const loadBuildingTree = async () => {
  try {
    const res = await getHouseList({})
    buildingTreeData.value = res.data || res || []
  } catch (error) {
    console.error('获取楼栋树失败:', error)
  }
}

// 获取房屋列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getHouseList(queryParams)
    tableData.value = res.rows || res.data?.rows || []
    total.value = res.total || res.data?.total || 0
  } catch (error) {
    console.error('获取房屋列表失败:', error)
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
  queryParams.houseNo = ''
  queryParams.buildingId = undefined
  queryParams.houseType = ''
  queryParams.decorationStatus = ''
  queryParams.ownerName = ''
  queryParams.ownerPhone = ''
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.houseId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增房屋'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改房屋'
  resetForm()
  try {
    const res = await getHouseInfo(row.houseId)
    const data = res.data || res
    form.houseId = data.houseId
    form.houseNo = data.houseNo
    form.buildingId = data.buildingId
    form.unitNo = data.unitNo
    form.roomNo = data.roomNo
    form.floorNum = data.floorNum
    form.houseType = data.houseType
    form.buildArea = data.buildArea
    form.innerArea = data.innerArea
    form.orientation = data.orientation
    form.decorationStatus = data.decorationStatus
    form.houseStructure = data.houseStructure
    form.status = data.status
    dialogVisible.value = true
  } catch (error) {
    console.error('获取房屋信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const houseIds = row.houseId ? row.houseId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除房屋ID为"${houseIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteHouse(houseIds)
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
    ElMessage.warning('请选择要删除的房屋')
    return
  }
  handleDelete({ houseId: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === '0' ? '1' : '0'
    await deleteHouse(row.houseId, newStatus)
    ElMessage.success('修改状态成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 绑定业主
const handleBindOwner = (row) => {
  currentHouse.value = row
  ownerIsAdd.value = true
  ownerDialogTitle.value = '绑定业主'
  resetOwnerForm()
  ownerForm.houseId = row.houseId
  ownerForm.houseInfo = `${row.buildingName} ${row.unitNo}单元 ${row.roomNo}室 (${row.houseNo})`
  ownerDialogVisible.value = true
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
  form.houseId = ''
  form.houseNo = ''
  form.buildingId = undefined
  form.unitNo = ''
  form.roomNo = ''
  form.floorNum = 1
  form.houseType = '1'
  form.buildArea = 0
  form.innerArea = 0
  form.orientation = ''
  form.decorationStatus = '1'
  form.houseStructure = ''
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
      await addHouse(form)
      ElMessage.success('新增成功')
    } else {
      await updateHouse(form)
      ElMessage.success('修改成功')
    }
    dialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 关闭绑定业主弹窗
const closeOwnerDialog = (done) => {
  if (ownerFormRef.value) {
    ownerFormRef.value.resetFields()
  }
  done()
}

// 重置绑定业主表单
const resetOwnerForm = () => {
  ownerForm.houseId = undefined
  ownerForm.houseInfo = ''
  ownerForm.ownerName = ''
  ownerForm.ownerPhone = ''
  ownerForm.idCard = ''
  ownerForm.relationType = '本人'
  ownerForm.isMain = 'Y'
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
    if (ownerIsAdd.value) {
      await bindHouseOwner(ownerForm)
      ElMessage.success('绑定成功')
    } else {
      await unbindHouseOwner(ownerForm)
      ElMessage.success('解绑成功')
    }
    ownerDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('提交失败:', error)
  }
}

// 初始化
onMounted(async () => {
  await loadBuildingTree()
  // 支持从楼栋页面跳转过来时自动带入楼栋ID查询
  if (route.query.buildingId) {
    queryParams.buildingId = Number(route.query.buildingId)
  }
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

/* 树形选择器宽度适配 */
:deep(.el-tree-select__input) {
  width: 100%;
}

:deep(.el-tree-select__tags) {
  max-width: 100%;
}
</style>