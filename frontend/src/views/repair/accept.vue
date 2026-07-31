<template>
  <div>
    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">待派单工单</div>
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
        <el-table-column prop="createTime" label="报修时间" width="180" />
        <el-table-column label="操作" min-width="140" fixed="right">
          <template #default="{ row }">
            <el-button v-if="isAdmin" type="warning" size="small" @click="handleAssign(row)" v-permission="'repair:record:process'">派单</el-button>
            <el-button v-if="isWorker" type="success" size="small" @click="handleAccept(row)" v-permission="'repair:record:process'">接单</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog title="派单" v-model="assignDialogVisible" width="450px">
      <el-form label-width="100px">
        <el-form-item label="维修工" required>
          <el-select v-model="assignForm.handlerId" placeholder="请选择维修工" filterable>
            <el-option v-for="w in workers" :key="w.id" :label="w.realName" :value="w.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="assignDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAssign">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRepairPage, updateRepairStatus } from '@/api/repair/record'
import { getUserPage } from '@/api/system/user'
import { useUserStore } from '@/stores/user'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const assignDialogVisible = ref(false)
const assignForm = reactive({ handlerId: null })
const workers = ref([])
const currentRow = ref(null)
const userStore = useUserStore()

const searchForm = reactive({ pageNum: 1, pageSize: 10, status: 0 })

const isAdmin = computed(() => userStore.roles.includes('超级管理员') || userStore.roles.includes('物业管理员'))
const isWorker = computed(() => userStore.roles.includes('维修工'))
const typeText = (t) => ({ 水电: '水电维修', 门窗: '门窗维修', 家电: '电器维修', 公共设施: '公共设施', 其他: '其他' }[t] || t || '')

onMounted(async () => {
  fetchData()
  if (isAdmin.value) {
    try {
      const wRes = await getUserPage({ pageNum: 1, pageSize: 100 })
      workers.value = (wRes.data.records || []).filter(u => u.roleName === '维修工')
    } catch (e) { /* handled */ }
  }
})

async function fetchData() {
  loading.value = true
  try {
    const res = await getRepairPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleAssign(row) {
  currentRow.value = row
  assignForm.handlerId = null
  assignDialogVisible.value = true
}
async function submitAssign() {
  if (!assignForm.handlerId) {
    ElMessage.warning('请选择维修工')
    return
  }
  try {
    await updateRepairStatus({ id: currentRow.value.id, status: 1, handlerId: assignForm.handlerId })
    ElMessage.success('派单成功')
    assignDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}
async function handleAccept(row) {
  await ElMessageBox.confirm('确定接单吗？', '提示', { type: 'warning' })
  try {
    await updateRepairStatus({ id: row.id, status: 1 })
    ElMessage.success('接单成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
