<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="业主">
        <el-select v-model="searchForm.ownerId" placeholder="请选择" clearable filterable>
          <el-option v-for="o in owners" :key="o.id" :label="o.name" :value="o.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="报修状态">
        <el-select v-model="searchForm.status" placeholder="请选择" clearable>
          <el-option label="待处理" :value="0" /><el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left"><el-button type="primary" @click="handleAdd" v-permission="'repair:record:add'">新增报修</el-button></div>
        <div class="toolbar-right"><el-button @click="fetchData">刷新</el-button></div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="ownerName" label="业主" width="100" />
        <el-table-column prop="roomNo" label="房号" width="100" />
        <el-table-column label="报修类型" width="100">
          <template #default="{ row }">{{ typeText(row.repairType) }}</template>
        </el-table-column>
        <el-table-column prop="repairContent" label="报修描述" show-overflow-tooltip />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTag(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="handlerName" label="处理人" width="100" />
        <el-table-column prop="score" label="评分" width="80" />
        <el-table-column prop="createTime" label="报修时间" width="180" />
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'repair:record:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'repair:record:delete'">删除</el-button>
            <el-button v-if="row.status===0" type="warning" size="small" @click="handleStatus(row)" v-permission="'repair:record:edit'">处理</el-button>
            <el-button v-if="row.status===2 && !row.score" type="success" size="small" @click="handleRating(row)" v-permission="'repair:record:edit'">评价</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <!-- 新增/编辑 -->
    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="600px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主" prop="ownerId">
          <el-select v-model="form.ownerId" placeholder="请选择" filterable>
            <el-option v-for="o in owners" :key="o.id" :label="o.name" :value="o.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房屋" prop="houseId">
          <el-select v-model="form.houseId" placeholder="请选择" filterable>
            <el-option v-for="h in houses" :key="h.id" :label="h.roomNo" :value="h.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="报修类型" prop="repairType">
          <el-select v-model="form.repairType"><el-option label="水电维修" value="WATER_ELECTRICITY" /><el-option label="门窗维修" value="DOOR_WINDOW" /><el-option label="管道疏通" value="PIPE_DREDGE" /><el-option label="电器维修" value="ELECTRICAL" /><el-option label="其他" value="OTHER" /></el-select>
        </el-form-item>
        <el-form-item label="报修描述" prop="repairContent">
          <el-input v-model="form.repairContent" type="textarea" :rows="3" />
        </el-form-item>
        <el-form-item label="图片">
          <el-input v-model="form.repairImages" placeholder="图片URL" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 处理报修 -->
    <el-dialog title="处理报修" v-model="statusDialogVisible" width="500px">
      <el-form :model="statusForm" label-width="100px">
        <el-form-item label="处理状态">
          <el-select v-model="statusForm.status">
            <el-option label="处理中" :value="1" /><el-option label="已完成" :value="2" /><el-option label="已取消" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="处理内容">
          <el-input v-model="statusForm.handleContent" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="statusDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitStatus">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 评价 -->
    <el-dialog title="评价报修" v-model="ratingDialogVisible" width="400px">
      <el-form :model="ratingForm" label-width="100px">
        <el-form-item label="评分" required>
          <el-rate v-model="ratingForm.score" :max="5" />
        </el-form-item>
        <el-form-item label="评价内容">
          <el-input v-model="ratingForm.content" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="ratingDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitRating">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addRepair, updateRepair, deleteRepair, getRepairById, getRepairPage, updateRepairStatus, updateRepairRating } from '@/api/repair/record'
import { getOwnerPage } from '@/api/community/owner'
import { getHousePage } from '@/api/community/house'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const statusDialogVisible = ref(false)
const ratingDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const owners = ref([])
const houses = ref([])
const currentRow = ref(null)

const searchForm = reactive({ pageNum: 1, pageSize: 10, ownerId: '', status: '' })
const form = reactive({ id: null, ownerId: null, houseId: null, repairType: 'WATER_ELECTRICITY', repairContent: '', repairImages: '', remark: '' })
const statusForm = reactive({ status: 1, handleContent: '' })
const ratingForm = reactive({ score: 5, content: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑报修' : '新增报修')
const typeText = (t) => ({ 0: '水电维修', 1: '门窗维修', 2: '管道疏通', 3: '电器维修', 4: '其他' }[t] || '')
const statusTag = (s) => ({ 0: 'info', 1: 'warning', 2: 'success', 3: 'danger' }[s] || 'info')
const statusText = (s) => ({ 0: '待处理', 1: '处理中', 2: '已完成', 3: '已取消' }[s] || '')

const rules = {
  ownerId: [{ required: true, message: '请选择业主', trigger: 'change' }],
  houseId: [{ required: true, message: '请选择房屋', trigger: 'change' }],
  repairType: [{ required: true, message: '请选择报修类型', trigger: 'change' }],
  repairContent: [{ required: true, message: '请输入报修描述', trigger: 'blur' }]
}

onMounted(async () => {
  fetchData()
  const oRes = await getOwnerPage({ pageNum: 1, pageSize: 200 })
  owners.value = oRes.data.records
  const hRes = await getHousePage({ pageNum: 1, pageSize: 200 })
  houses.value = hRes.data.records
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRepairPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.ownerId = ''; searchForm.status = ''; handleSearch() }
function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, { ...row, repairType: row.repairType || 'WATER_ELECTRICITY', repairContent: row.repairContent || row.description || '', repairImages: row.repairImages || row.images || '' }); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateRepair(form)
    else await addRepair(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该报修记录吗？', '提示', { type: 'warning' })
  try { await deleteRepair(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) { /* handled */ }
}

function handleStatus(row) { currentRow.value = row; statusForm.status = 1; statusForm.handleContent = ''; statusDialogVisible.value = true }
async function submitStatus() {
  try {
    await updateRepairStatus({ id: currentRow.value.id, ...statusForm })
    ElMessage.success('处理成功')
    statusDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}

function handleRating(row) { currentRow.value = row; ratingForm.score = 5; ratingForm.content = ''; ratingDialogVisible.value = true }
async function submitRating() {
  try {
    await updateRepairRating({ id: currentRow.value.id, ...ratingForm })
    ElMessage.success('评价成功')
    ratingDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}
</script>
