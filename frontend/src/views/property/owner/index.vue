<template>
  <div class="app-container">
    <div class="page-header">
      <h1>业主管理</h1>
    </div>

    <el-card>
      <template #header>
        <div class="card-header">
          <span>业主列表</span>
          <el-button-group>
            <el-button type="primary" @click="handleAdd" v-permission="['property:owner:add']">
              <Plus /> 新增
            </el-button>
            <el-button type="danger" @click="handleBatchDelete" v-permission="['property:owner:delete']">
              <Delete /> 批量删除
            </el-button>
          </el-button-group>
        </div>
      </template>

      <!-- 查询表单 -->
      <el-form :model="queryParams" :inline="true" class="search-form" label-width="80px">
        <el-form-item label="业主姓名">
          <el-input v-model="queryParams.ownerName" placeholder="业主姓名" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="业主电话">
          <el-input v-model="queryParams.ownerPhone" placeholder="业主电话" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="身份证号">
          <el-input v-model="queryParams.idCard" placeholder="身份证号" clearable style="width: 200px" />
        </el-form-item>
        <el-form-item label="关联房屋">
          <el-tree-select
            v-model="queryParams.houseId"
            :props="houseTreeProps"
            :data="houseTreeData"
            placeholder="请选择房屋"
            clearable
            style="width: 200px"
            check-strictly
            show-all-levels
          />
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

      <!-- 业主表格 -->
      <el-table
        v-loading="loading"
        :data="tableData"
        :total="total"
        row-key="ownerId"
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column prop="ownerId" label="业主ID" width="80" align="center" />
        <el-table-column prop="ownerName" label="业主姓名" min-width="120" show-overflow-tooltip />
        <el-table-column prop="ownerPhone" label="业主电话" min-width="130" align="center" />
        <el-table-column prop="idCard" label="身份证号" min-width="180" show-overflow-tooltip />
        <el-table-column prop="genderLabel" label="性别" width="80" align="center" />
        <el-table-column prop="age" label="年龄" width="80" align="center" />
        <el-table-column prop="idTypeLabel" label="证件类型" width="100" align="center" />
        <el-table-column prop="email" label="邮箱" min-width="180" show-overflow-tooltip />
        <el-table-column prop="emergencyContact" label="紧急联系人" width="120" align="center" />
        <el-table-column prop="emergencyPhone" label="紧急电话" min-width="130" align="center" />
        <el-table-column prop="status" label="状态" width="90" align="center">
          <template #default="scope">
            <el-tag :type="scope.row.status === '0' ? 'success' : 'danger'">
              {{ scope.row.status === '0' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" align="center" width="400" fixed="right">
          <template #default="scope">
            <el-button
              size="small"
              type="primary"
              @click="handleUpdate(scope.row)"
              v-permission="['property:owner:edit']"
            >
              <Edit /> 编辑
            </el-button>
            <el-button
              size="small"
              type="danger"
              @click="handleDelete(scope.row)"
              v-permission="['property:owner:delete']"
            >
              <Delete /> 删除
            </el-button>
            <el-button
              size="small"
              type="success"
              @click="handleBindHouse(scope.row)"
            >
              <House /> 房屋绑定
            </el-button>
            <el-button
              size="small"
              type="info"
              @click="handleIdCard(scope.row)"
            >
              <Document /> 证件管理
            </el-button>
            <el-button
              size="small"
              type="warning"
              @click="handleStatusChange(scope.row)"
              v-permission="['property:owner:edit']"
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

    <!-- 新增/编辑业主弹窗 -->
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
            <el-form-item label="业主ID" prop="ownerId">
              <el-input v-model="form.ownerId" disabled placeholder="自动生成" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主姓名" prop="ownerName" :rules="[{ required: true, message: '请输入业主姓名', trigger: 'blur' }]">
              <el-input v-model="form.ownerName" placeholder="请输入业主姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="业主电话" prop="ownerPhone" :rules="[
              { required: true, message: '请输入业主电话', trigger: 'blur' },
              { pattern: /^1[3-9]\\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
            ]">
              <el-input v-model="form.ownerPhone" placeholder="请输入业主电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证号" prop="idCard" :rules="[
              { required: true, message: '请输入身份证号', trigger: 'blur' },
              { pattern: /(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}[\\dXx]$)/, message: '请输入正确的身份证号', trigger: 'blur' }
            ]">
              <el-input v-model="form.idCard" placeholder="请输入身份证号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="性别" prop="gender" :rules="[{ required: true, message: '请选择性别', trigger: 'change' }]">
              <el-select v-model="form.gender" placeholder="请选择性别" style="width: 100%">
                <el-option label="男" value="1" />
                <el-option label="女" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="出生日期" prop="birthDate">
              <el-date-picker
                v-model="form.birthDate"
                type="date"
                placeholder="请选择出生日期"
                style="width: 100%"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急联系人" prop="emergencyContact">
              <el-input v-model="form.emergencyContact" placeholder="请输入紧急联系人" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="紧急电话" prop="emergencyPhone">
              <el-input v-model="form.emergencyPhone" placeholder="请输入紧急电话" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件类型" prop="idType">
              <el-select v-model="form.idType" placeholder="请选择证件类型" style="width: 100%" clearable>
                <el-option label="身份证" value="身份证" />
                <el-option label="护照" value="护照" />
                <el-option label="军官证" value="军官证" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="证件号码" prop="idNumber">
              <el-input v-model="form.idNumber" placeholder="请输入证件号码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证件照片-正面" prop="idCardFrontUrl">
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :on-change="handleIdCardFrontChange"
                :show-file-list="false"
                accept="image/*"
              >
                <img v-if="form.idCardFrontUrl" :src="form.idCardFrontUrl" class="avatar-preview" />
                <i v-else class="el-icon-plus avatar-uploader-icon" />
              </el-upload>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="证件照片-反面" prop="idCardBackUrl">
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :on-change="handleIdCardBackChange"
                :show-file-list="false"
                accept="image/*"
              >
                <img v-if="form.idCardBackUrl" :src="form.idCardBackUrl" class="avatar-preview" />
                <i v-else class="el-icon-plus avatar-uploader-icon" />
              </el-upload>
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

    <!-- 房屋绑定弹窗 -->
    <el-dialog
      v-model="houseDialogVisible"
      :title="houseDialogTitle"
      width="900px"
      :close-on-click-modal="false"
      :before-close="closeHouseDialog"
      destroy-on-close
    >
      <div class="house-bind-container">
        <el-form ref="houseFormRef" :model="houseForm" label-width="100px" class="dialog-form">
          <el-form-item label="业主信息" prop="ownerInfo">
            <el-input v-model="houseForm.ownerInfo" disabled />
          </el-form-item>
          <el-form-item label="已绑定房屋">
            <el-table
              :data="boundHouses"
              border
              style="width: 100%"
              row-key="houseId"
            >
              <el-table-column prop="houseNo" label="房屋编号" min-width="120" />
              <el-table-column prop="buildingName" label="楼栋名称" min-width="120" />
              <el-table-column prop="unitNo" label="单元号" width="80" align="center" />
              <el-table-column prop="roomNo" label="房间号" width="80" align="center" />
              <el-table-column prop="relationType" label="关系类型" width="100" align="center" />
              <el-table-column prop="isMain" label="主业主" width="80" align="center">
                <template #default="scope">
                  <el-tag :type="scope.row.isMain === 'Y' ? 'success' : 'info'">
                    {{ scope.row.isMain === 'Y' ? '是' : '否' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="120" align="center">
                <template #default="scope">
                  <el-button
                    size="small"
                    type="danger"
                    link
                    @click="unbindHouse(scope.row)"
                  >
                    解绑
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-form-item>
          <el-form-item label="选择房屋" prop="houseIds">
            <el-transfer
              v-model="houseForm.houseIds"
              :data="availableHouses"
              :titles="['可选房屋', '已选房屋']"
              :button-texts="['添加', '移除']"
              filterable
              filter-placeholder="输入房屋编号搜索"
              :render-content="renderTransferContent"
              @change="handleTransferChange"
            />
          </el-form-item>
          <el-form-item label="关系类型" prop="relationType">
            <el-select v-model="houseForm.relationType" placeholder="请选择关系类型" style="width: 200px" clearable>
              <el-option label="本人" value="本人" />
              <el-option label="配偶" value="配偶" />
              <el-option label="子女" value="子女" />
              <el-option label="父母" value="父母" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="是否主业主" prop="isMain">
            <el-radio-group v-model="houseForm.isMain">
              <el-radio :label="'Y'">是</el-radio>
              <el-radio :label="'N'">否</el-radio>
            </el-radio-group>
          </el-form-item>
        </el-form>
      </div>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeHouseDialog">取消</el-button>
          <el-button type="primary" @click="submitHouseForm">确定绑定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 证件管理弹窗 -->
    <el-dialog
      v-model="idCardDialogVisible"
      :title="idCardDialogTitle"
      width="800px"
      :close-on-click-modal="false"
      :before-close="closeIdCardDialog"
      destroy-on-close
    >
      <el-form ref="idCardFormRef" :model="idCardForm" label-width="100px" class="dialog-form">
        <el-form-item label="业主信息" prop="ownerInfo">
          <el-input v-model="idCardForm.ownerInfo" disabled />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="身份证正面">
              <div class="id-card-preview">
                <img v-if="idCardForm.idCardFrontUrl" :src="idCardForm.idCardFrontUrl" class="preview-img" />
                <div v-else class="preview-placeholder">暂无图片</div>
              </div>
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :on-change="handleIdCardFrontUpload"
                :show-file-list="false"
                accept="image/*"
              >
                <el-button size="small" type="primary">{{ idCardForm.idCardFrontUrl ? '重新上传' : '上传正面' }}</el-button>
              </el-upload>
              <el-button v-if="idCardForm.idCardFrontUrl" size="small" type="danger" @click="handleDeleteIdCard('front')" style="margin-top: 8px">删除</el-button>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="身份证反面">
              <div class="id-card-preview">
                <img v-if="idCardForm.idCardBackUrl" :src="idCardForm.idCardBackUrl" class="preview-img" />
                <div v-else class="preview-placeholder">暂无图片</div>
              </div>
              <el-upload
                class="avatar-uploader"
                action="#"
                :auto-upload="false"
                :on-change="handleIdCardBackUpload"
                :show-file-list="false"
                accept="image/*"
              >
                <el-button size="small" type="primary">{{ idCardForm.idCardBackUrl ? '重新上传' : '上传反面' }}</el-button>
              </el-upload>
              <el-button v-if="idCardForm.idCardBackUrl" size="small" type="danger" @click="handleDeleteIdCard('back')" style="margin-top: 8px">删除</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="closeIdCardDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewVisible" :title="previewTitle" width="800px">
      <img v-if="previewImageUrl" :src="previewImageUrl" style="width: 100%; height: auto;" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  Plus, Download, Search, Refresh, Edit, Delete, House, SwitchButton, Document
} from '@element-plus/icons-vue'
import {
  getOwnerList,
  getOwnerInfo,
  addOwner,
  updateOwner,
  deleteOwner,
  getOwnerHouseList,
  bindOwnerHouse,
  unbindOwnerHouse,
  uploadIdCard,
  getIdCardInfo,
  deleteIdCard
} from '@/api/community/owner'
import { usePermission } from '@/hooks/usePermission'

const { hasPermission } = usePermission()

// 响应式数据
const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectionIds = ref([])

// 房屋树数据
const houseTreeData = ref([])
const houseTreeProps = {
  children: 'children',
  label: 'label',
  value: 'value'
}

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  ownerName: '',
  ownerPhone: '',
  idCard: '',
  houseId: undefined,
  status: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('新增业主')
const isAdd = ref(true)

const form = reactive({
  ownerId: undefined,
  ownerName: '',
  ownerPhone: '',
  idCard: '',
  gender: '1',
  birthDate: '',
  email: '',
  emergencyContact: '',
  emergencyPhone: '',
  idType: '',
  idNumber: '',
  idCardFrontUrl: '',
  idCardBackUrl: '',
  status: '0',
  remark: ''
})

const rules = reactive({
  ownerName: [{ required: true, message: '请输入业主姓名', trigger: 'blur' }],
  ownerPhone: [
    { required: true, message: '请输入业主电话', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  idCard: [
    { required: true, message: '请输入身份证号', trigger: 'blur' },
    { pattern: /(^\d{15}$)|(^\d{18}$)|(^\d{17}[\dXx]$)/, message: '请输入正确的身份证号', trigger: 'blur' }
  ],
  gender: [{ required: true, message: '请选择性别', trigger: 'change' }],
  status: [{ required: true, message: '请选择状态', trigger: 'change' }]
})

const formRef = ref()

// 房屋绑定弹窗
const houseDialogVisible = ref(false)
const houseDialogTitle = ref('房屋绑定')
const currentOwner = ref(null)

const houseForm = reactive({
  ownerId: undefined | undefined,
  ownerInfo: '',
  houseIds: [],
  relationType: '本人',
  isMain: 'Y'
})

const boundHouses = ref([])
const availableHouses = ref([])
const houseFormRef = ref()

// 证件管理弹窗
const idCardDialogVisible = ref(false)
const idCardDialogTitle = ref('证件管理')
const idCardForm = reactive({
  ownerId: undefined | undefined,
  ownerInfo: '',
  idCardFrontUrl: '',
  idCardBackUrl: ''
})
const idCardFormRef = ref()

// 图片预览
const previewVisible = ref(false)
const previewTitle = ref('预览')
const previewImageUrl = ref('')

// 获取房屋树数据
const loadHouseTree = async () => {
  try {
    const res = await getOwnerList()
    houseTreeData.value = formatHouseTree(res.data || res || [])
  } catch (error) {
    console.error('获取房屋树失败:', error)
  }
}

const formatHouseTree = (data) => {
  return data.map(item => ({
    houseId: item.houseId,
    houseNo: item.houseNo,
    buildingName: item.buildingName,
    unitNo: item.unitNo,
    roomNo: item.roomNo,
    label: `${item.buildingName} ${item.unitNo}单元 ${item.roomNo}室 (${item.houseNo})`,
    value: item.houseId,
    children: item.children ? formatHouseTree(item.children) : undefined
  }))
}

// 获取业主列表
const getList = async () => {
  loading.value = true
  try {
    const res = await getOwnerList(queryParams)
    const data = res.data || res
    tableData.value = data.rows || data.list || []
    total.value = data.total || 0
  } catch (error) {
    console.error('获取业主列表失败:', error)
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
  queryParams.ownerName = ''
  queryParams.ownerPhone = ''
  queryParams.idCard = ''
  queryParams.houseId = undefined
  queryParams.status = ''
  handleQuery()
}

// 表格选择
const handleSelectionChange = (selection) => {
  selectionIds.value = selection.map(item => item.ownerId)
}

// 新增
const handleAdd = () => {
  isAdd.value = true
  dialogTitle.value = '新增业主'
  resetForm()
  dialogVisible.value = true
}

// 编辑
const handleUpdate = async (row) => {
  isAdd.value = false
  dialogTitle.value = '修改业主'
  resetForm()
  try {
    const res = await getOwnerInfo(row.ownerId)
    const data = res.data || res
    form.ownerId = data.ownerId
    form.ownerName = data.ownerName
    form.ownerPhone = data.ownerPhone
    form.idCard = data.idCard
    form.gender = data.gender
    form.birthDate = data.birthDate || ''
    form.email = data.email || ''
    form.emergencyContact = data.emergencyContact || ''
    form.emergencyPhone = data.emergencyPhone || ''
    form.idType = data.idType || ''
    form.idNumber = data.idNumber || ''
    form.idCardFrontUrl = data.idCardFrontUrl || ''
    form.idCardBackUrl = data.idCardBackUrl || ''
    form.status = data.status
    form.remark = data.remark || ''
    dialogVisible.value = true
  } catch (error) {
    console.error('获取业主信息失败:', error)
  }
}

// 删除
const handleDelete = (row) => {
  const ownerIds = row.ownerId ? row.ownerId : selectionIds.value.join(',')
  ElMessageBox.confirm(`是否确认删除业主ID为"${ownerIds}"的数据项?`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteOwner(ownerIds)
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
    ElMessage.warning('请选择要删除的业主')
    return
  }
  handleDelete({ ownerId: selectionIds.value.join(',') })
}

// 状态修改
const handleStatusChange = async (row) => {
  try {
    const newStatus = row.status === '0' ? '1' : '0'
    await deleteOwner(row.ownerId, newStatus)
    ElMessage.success(newStatus === '0' ? '启用成功' : '禁用成功')
    getList()
  } catch (error) {
    console.error('修改状态失败:', error)
    getList()
  }
}

// 房屋绑定
const handleBindHouse = async (row) => {
  currentOwner.value = row
  houseDialogTitle.value = `房屋绑定 - ${row.ownerName}`
  houseForm.ownerId = row.ownerId
  houseForm.ownerInfo = `${row.ownerName} (${row.ownerPhone})`
  houseForm.houseIds = []
  houseForm.relationType = '本人'
  houseForm.isMain = 'Y'
  boundHouses.value = []
  availableHouses.value = []

  try {
    // 获取已绑定房屋
    const res = await getOwnerHouseList({ ownerId: row.ownerId })
    const data = res.data || res
    boundHouses.value = data.rows || data.list || data || []

    // 获取可选房屋树并扁平化
    const treeRes = await getOwnerList()
    const treeData = treeRes.data || treeRes || []
    availableHouses.value = flattenHouseTree(treeData).filter(
      h => !boundHouses.value.some(b => b.houseId === h.value)
    ).map(h => ({
      key: h.value,
      label: h.label,
      disabled: false
    }))
  } catch (error) {
    console.error('获取房屋数据失败:', error)
  }
  houseDialogVisible.value = true
}

const flattenHouseTree = (tree) => {
  const result = []
  tree.forEach(node => {
    result.push({ ...node, children: undefined })
    if (node.children) {
      result.push(...flattenHouseTree(node.children))
    }
  })
  return result
}

const renderTransferContent = (h, option) => {
  return option.label
}

const handleTransferChange = (value, direction, movedKeys) => {
  houseForm.houseIds = value
}

const unbindHouse = async (house) => {
  try {
    await unbindOwnerHouse({ ownerId: currentOwner.value.ownerId, houseId: house.houseId })
    ElMessage.success('解绑成功')
    handleBindHouse(currentOwner.value)
  } catch (error) {
    console.error('解绑失败:', error)
  }
}

const submitHouseForm = async () => {
  if (!houseFormRef.value) return
  try {
    await houseFormRef.value.validate()
    if (houseForm.houseIds.length === 0) {
      ElMessage.warning('请选择要绑定的房屋')
      return
    }
    const data = {
      ownerId: houseForm.ownerId,
      houseIds: houseForm.houseIds,
      relationType: houseForm.relationType,
      isMain: houseForm.isMain
    }
    await bindOwnerHouse(data)
    ElMessage.success('绑定成功')
    houseDialogVisible.value = false
    getList()
  } catch (error) {
    console.error('绑定失败:', error)
  }
}

const closeHouseDialog = (done) => {
  if (houseFormRef.value) {
    houseFormRef.value.resetFields()
  }
  done()
}

// 证件管理
const handleIdCard = async (row) => {
  currentOwner.value = row
  idCardDialogTitle.value = `证件管理 - ${row.ownerName}`
  idCardForm.ownerId = row.ownerId
  idCardForm.ownerInfo = `${row.ownerName} (${row.ownerPhone})`
  idCardForm.idCardFrontUrl = row.idCardFrontUrl || ''
  idCardForm.idCardBackUrl = row.idCardBackUrl || ''

  // 尝试从后端获取最新证件信息
  try {
    const res = await getIdCardInfo(row.ownerId)
    const data = res.data || res
    if (data.idCardFrontUrl) idCardForm.idCardFrontUrl = data.idCardFrontUrl
    if (data.idCardBackUrl) idCardForm.idCardBackUrl = data.idCardBackUrl
  } catch (error) {
    console.error('获取证件信息失败:', error)
  }
  idCardDialogVisible.value = true
}

const handleIdCardFrontChange = (file) => {
  if (!file.raw) return
  uploadIdCardFile(file.raw, 'front')
}

const handleIdCardBackChange = (file) => {
  if (!file.raw) return
  uploadIdCardFile(file.raw, 'back')
}

const handleIdCardFrontUpload = (file) => {
  if (!file.raw) return
  uploadIdCardFile(file.raw, 'front')
}

const handleIdCardBackUpload = (file) => {
  if (!file.raw) return
  uploadIdCardFile(file.raw, 'back')
}

const uploadIdCardFile = async (file, type) => {
  if (!currentOwner.value) return
  try {
    loading.value = true
    const res = await uploadIdCard(file, currentOwner.value.ownerId, type)
    const data = res.data || res
    if (type === 'front') {
      idCardForm.idCardFrontUrl = data.url || data
    } else {
      idCardForm.idCardBackUrl = data.url || data
    }
    ElMessage.success('上传成功')
  } catch (error) {
    console.error('上传失败:', error)
    ElMessage.error('上传失败')
  } finally {
    loading.value = false
  }
}

const handleDeleteIdCard = async (type) => {
  if (!currentOwner.value) return
  try {
    await deleteIdCard(currentOwner.value.ownerId, type)
    if (type === 'front') {
      idCardForm.idCardFrontUrl = ''
    } else {
      idCardForm.idCardBackUrl = ''
    }
    ElMessage.success('删除成功')
  } catch (error) {
    console.error('删除失败:', error)
    ElMessage.error('删除失败')
  }
}

const previewImage = (url, title) => {
  previewImageUrl.value = url
  previewTitle.value = title
  previewVisible.value = true
}

const closeIdCardDialog = (done) => {
  done()
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
  form.ownerId = undefined
  form.ownerName = ''
  form.ownerPhone = ''
  form.idCard = ''
  form.gender = '1'
  form.birthDate = ''
  form.email = ''
  form.emergencyContact = ''
  form.emergencyPhone = ''
  form.idType = ''
  form.idNumber = ''
  form.idCardFrontUrl = ''
  form.idCardBackUrl = ''
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
      await addOwner(form)
      ElMessage.success('新增成功')
    } else {
      await updateOwner(form)
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
  await loadHouseTree()
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

/* 头像上传区域 */
.avatar-uploader {
  display: flex;
  flex-direction: column;
  gap: 8px;
  .el-upload {
    width: 100%;
  }
}

.avatar-preview {
  width: 100%;
  max-width: 300px;
  max-height: 180px;
  border-radius: 4px;
  border: 1px solid #dcdfe6;
  object-fit: contain;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  cursor: pointer;
  transition: all 0.3s;
  &:hover {
    color: #409eff;
    border-color: #409eff;
  }
}

/* 房屋绑定容器 */
.house-bind-container {
  max-height: 60vh;
  overflow-y: auto;
  .el-table {
    margin-bottom: 16px;
  }
}

/* 转移框样式 */
:deep(.el-transfer) {
  width: 100%;
}

:deep(.el-transfer__list) {
  height: 280px;
}

/* 证件预览 */
.id-card-preview {
  width: 100%;
  max-width: 320px;
  min-height: 180px;
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  margin-bottom: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fafafa;
  .preview-img {
    max-width: 100%;
    max-height: 180px;
    object-fit: contain;
  }
  .preview-placeholder {
    color: #909399;
    font-size: 14px;
  }
}

.avatar-uploader .el-upload {
  width: 100%;
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