<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="楼栋">
        <el-select v-model="searchForm.buildingId" placeholder="请选择楼栋" clearable style="width: 180px">
          <el-option v-for="b in buildingList" :key="b.id" :label="b.buildingNo" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="房屋状态">
        <el-select v-model="searchForm.houseStatus" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="空置" :value="0" />
          <el-option label="已入住" :value="1" />
          <el-option label="出租" :value="2" />
          <el-option label="装修中" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd">新增房屋</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="buildingNo" label="楼栋号" width="100" />
        <el-table-column prop="roomNo" label="房间号" width="100" />
        <el-table-column prop="area" label="面积(㎡)" width="100" />
        <el-table-column prop="houseType" label="户型" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusType(row.houseStatus)">{{ statusLabel(row.houseStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="业主名" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="searchForm.pageNum"
        v-model:page-size="searchForm.pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="fetchData"
        @current-change="fetchData"
        style="margin-top: 16px; justify-content: flex-end;"
      />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="550px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择楼栋" style="width: 100%">
            <el-option v-for="b in buildingList" :key="b.id" :label="b.buildingNo" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间号" prop="roomNo">
          <el-input v-model="form.roomNo" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="面积(㎡)" prop="area">
          <el-input-number v-model="form.area" :min="0" :precision="2" placeholder="请输入面积" style="width: 100%" />
        </el-form-item>
        <el-form-item label="户型" prop="houseType">
          <el-input v-model="form.houseType" placeholder="请输入户型（如：三室一厅）" />
        </el-form-item>
        <el-form-item label="房屋状态" prop="houseStatus">
          <el-select v-model="form.houseStatus" placeholder="请选择状态" style="width: 100%">
            <el-option label="空置" :value="0" />
            <el-option label="已入住" :value="1" />
            <el-option label="出租" :value="2" />
            <el-option label="装修中" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择业主" clearable style="width: 100%">
            <el-option v-for="o in ownerList" :key="o.id" :label="o.name" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addHouse, updateHouse, deleteHouse, getHousePage } from '@/api/community/house'
import { getBuildingPage } from '@/api/community/building'
import { getOwnerPage } from '@/api/community/owner'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const buildingList = ref([])
const ownerList = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, buildingId: null, houseStatus: null })
const form = reactive({ id: null, buildingId: null, roomNo: '', area: null, houseType: '', houseStatus: null, ownerId: null, remark: '' })

const dialogTitle = computed(() => isEdit.value ? '编辑房屋' : '新增房屋')

const rules = {
  buildingId: [{ required: true, message: '请选择楼栋', trigger: 'change' }],
  roomNo: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  area: [{ required: true, message: '请输入面积', trigger: 'blur' }]
}

const statusMap = { 0: '空置', 1: '已入住', 2: '出租', 3: '装修中' }
const statusTypeMap = { 0: 'info', 1: 'success', 2: 'warning', 3: 'danger' }
function statusLabel(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(() => { fetchData(); loadBuildingList(); loadOwnerList() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getHousePage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadBuildingList() {
  try {
    const res = await getBuildingPage()
    buildingList.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadOwnerList() {
  try {
    const res = await getOwnerPage()
    ownerList.value = res.data || []
  } catch (e) { /* ignore */ }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.buildingId = null; searchForm.houseStatus = null; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateHouse(form) }
    else { await addHouse(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该房屋吗？', '提示', { type: 'warning' })
  try {
    await deleteHouse(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
