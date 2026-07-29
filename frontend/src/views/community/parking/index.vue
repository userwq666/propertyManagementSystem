<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="车位编号">
        <el-input v-model="searchForm.parkingNo" placeholder="请输入车位编号" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable style="width: 150px">
          <el-option label="空闲" :value="0" />
          <el-option label="已租" :value="1" />
          <el-option label="已售" :value="2" />
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
          <el-button type="primary" @click="handleAdd">新增车位</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="parkingNo" label="车位编号" width="120" />
        <el-table-column label="类型" width="80">
          <template #default="{ row }">
            <el-tag :type="row.parkingType === 0 ? '' : 'warning'">{{ row.parkingType === 0 ? '地面' : '地下' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="statusType(row.status)">{{ statusLabel(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="ownerName" label="业主名" width="100" />
        <el-table-column prop="rentPrice" label="租金(元)" width="100" />
        <el-table-column prop="sellPrice" label="售价(元)" width="100" />
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

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="500px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="车位编号" prop="parkingNo">
          <el-input v-model="form.parkingNo" placeholder="请输入车位编号" />
        </el-form-item>
        <el-form-item label="类型" prop="parkingType">
          <el-radio-group v-model="form.parkingType">
            <el-radio :value="0">地面</el-radio>
            <el-radio :value="1">地下</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态" style="width: 100%">
            <el-option label="空闲" :value="0" />
            <el-option label="已租" :value="1" />
            <el-option label="已售" :value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择业主" clearable style="width: 100%">
            <el-option v-for="o in ownerList" :key="o.id" :label="o.name" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="租金" prop="rentPrice">
          <el-input-number v-model="form.rentPrice" :min="0" :precision="2" placeholder="请输入租金" style="width: 100%" />
        </el-form-item>
        <el-form-item label="售价" prop="sellPrice">
          <el-input-number v-model="form.sellPrice" :min="0" :precision="2" placeholder="请输入售价" style="width: 100%" />
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
import { addParking, updateParking, deleteParking, getParkingPage } from '@/api/community/parking'
import { getOwnerPage } from '@/api/community/owner'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const ownerList = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, parkingNo: '', status: null })
const form = reactive({ id: null, parkingNo: '', parkingType: 0, status: 0, ownerId: null, rentPrice: null, sellPrice: null, remark: '' })

const dialogTitle = computed(() => isEdit.value ? '编辑车位' : '新增车位')

const rules = {
  parkingNo: [{ required: true, message: '请输入车位编号', trigger: 'blur' }]
}

const statusMap = { 0: '空闲', 1: '已租', 2: '已售' }
const statusTypeMap = { 0: 'success', 1: 'warning', 2: 'info' }
function statusLabel(s) { return statusMap[s] || '未知' }
function statusType(s) { return statusTypeMap[s] || 'info' }

onMounted(() => { fetchData(); loadOwnerList() })

async function fetchData() {
  loading.value = true
  try {
    const res = await getParkingPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

async function loadOwnerList() {
  try {
    const res = await getOwnerPage()
    ownerList.value = res.data || []
  } catch (e) { /* ignore */ }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.parkingNo = ''; searchForm.status = null; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.parkingType = 0; form.status = 0 }

async function handleSubmit() {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateParking(form) }
    else { await addParking(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该车位吗？', '提示', { type: 'warning' })
  try {
    await deleteParking(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
