<template>
  <div class="house-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>房屋管理</span>
          <el-button type="primary" @click="handleAdd">新增房屋</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="楼栋">
          <el-select v-model="queryParams.buildingId" placeholder="请选择楼栋" clearable>
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号">
          <el-input v-model="queryParams.roomNo" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="queryParams.houseStatus" placeholder="请选择状态" clearable>
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="出租" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="buildingId" label="楼栋">
          <template #default="{ row }">
            {{ getBuildingName(row.buildingId) }}
          </template>
        </el-table-column>
        <el-table-column prop="roomNo" label="房间号" />
        <el-table-column prop="area" label="面积(㎡)" />
        <el-table-column prop="houseType" label="户型" />
        <el-table-column prop="houseStatus" label="状态">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.houseStatus)">{{ getStatusText(row.houseStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="220">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="warning" link @click="handleChangeStatus(row)">修改状态</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      
      <el-pagination
        v-model:current-page="queryParams.pageNum"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingNo" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="form.area" :precision="2" :min="0" />
        </el-form-item>
        <el-form-item label="户型" prop="houseType">
          <el-input v-model="form.houseType" placeholder="请输入户型" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="statusDialogVisible" title="修改状态" width="400px">
      <el-form label-width="80px">
        <el-form-item label="状态">
          <el-select v-model="statusForm.houseStatus">
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="出租" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="statusDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleStatusSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getHousePage, addHouse, updateHouse, deleteHouse, updateHouseStatus } from '@/api/house'
import { getBuildingList } from '@/api/building'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const buildingList = ref([])

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingId: null,
  roomNo: '',
  houseStatus: null
})

const form = reactive({
  id: null,
  buildingId: null,
  roomNo: '',
  area: null,
  houseType: '',
  houseStatus: 0,
  ownerId: null,
  remark: ''
})

const statusForm = reactive({
  id: null,
  houseStatus: 0
})

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getHousePage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const loadBuildings = async () => {
  const res = await getBuildingList()
  buildingList.value = res.data
}

const getBuildingName = (id) => {
  const building = buildingList.value.find(item => item.id === id)
  return building ? building.buildingNo : '-'
}

const getStatusType = (status) => {
  const types = { 0: 'info', 1: 'success', 2: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 0: '空置', 1: '已入住', 2: '出租' }
  return texts[status] || '未知'
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.buildingId = null
  queryParams.roomNo = ''
  queryParams.houseStatus = null
  handleSearch()
}

const handleSizeChange = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleCurrentChange = () => {
  loadData()
}

const handleAdd = () => {
  dialogTitle.value = '新增房屋'
  Object.assign(form, { id: null, buildingId: null, roomNo: '', area: null, houseType: '', houseStatus: 0, ownerId: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房屋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateHouse(form)
    ElMessage.success('编辑成功')
  } else {
    await addHouse(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleChangeStatus = (row) => {
  statusForm.id = row.id
  statusForm.houseStatus = row.houseStatus
  statusDialogVisible.value = true
}

const handleStatusSubmit = async () => {
  await updateHouseStatus(statusForm.id, statusForm.houseStatus)
  ElMessage.success('状态修改成功')
  statusDialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该房屋吗？', '提示', { type: 'warning' })
  await deleteHouse(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadBuildings()
  loadData()
})
</script>

<style scoped>
.house-container {
  padding: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.search-form {
  margin-bottom: 20px;
}
.el-pagination {
  margin-top: 20px;
  justify-content: flex-end;
}
</style>
