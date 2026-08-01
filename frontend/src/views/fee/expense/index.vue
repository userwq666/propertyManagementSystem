<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="事项名称">
        <el-input v-model="searchForm.expenseName" placeholder="请输入事项名称" clearable />
      </el-form-item>
      <el-form-item label="支出类型">
        <el-select v-model="searchForm.expenseType" placeholder="请选择" clearable>
          <el-option label="维修" :value="1" /><el-option label="人工" :value="2" /><el-option label="材料" :value="3" /><el-option label="其他" :value="4" />
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
          <el-button type="primary" @click="handleAdd" v-permission="'fee:expense:add'">新增事项</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="expenseName" label="事项名称" min-width="140" />
        <el-table-column label="支出类型" width="100">
          <template #default="{ row }">
            <el-tag>{{ row.expenseTypeName || '-' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="支出金额(元)" width="130" />
        <el-table-column label="审核状态" width="100">
          <template #default="{ row }">
            <el-tag :type="auditTag(row.auditStatus)">{{ auditText(row.auditStatus) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="auditorName" label="审核人" width="100">
          <template #default="{ row }">{{ row.auditorName || '-' }}</template>
        </el-table-column>
        <el-table-column prop="expenseDate" label="支出日期" width="120" />
        <el-table-column prop="content" label="说明" show-overflow-tooltip />
        <el-table-column prop="creatorName" label="记录人" width="100" />
        <el-table-column prop="createTime" label="记录时间" width="170" />
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column label="操作" min-width="220" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button v-if="canEdit(row)" type="primary" size="small" @click="handleEdit(row)" v-permission="'fee:expense:edit'">编辑</el-button>
            <el-button v-if="row.auditStatus === 0" type="success" size="small" @click="openAudit(row)" v-permission="'fee:expense:audit'">审核</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'fee:expense:delete'">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination v-model:current-page="searchForm.pageNum" v-model:page-size="searchForm.pageSize"
        :total="total" :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="fetchData" @current-change="fetchData" style="margin-top:16px;justify-content:flex-end" />
    </div>

    <el-dialog :title="dialogTitle" v-model="dialogVisible" width="520px" @close="resetForm">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="事项名称" prop="expenseName">
          <el-input v-model="form.expenseName" placeholder="如：电梯维修、公共区域保洁" />
        </el-form-item>
        <el-form-item label="支出类型" prop="expenseType">
          <el-select v-model="form.expenseType" style="width:100%">
            <el-option label="维修" :value="1" /><el-option label="人工" :value="2" /><el-option label="材料" :value="3" /><el-option label="其他" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="支出金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0.01" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="支出日期" prop="expenseDate">
          <el-date-picker v-model="form.expenseDate" type="date" value-format="YYYY-MM-DD" placeholder="选择日期" style="width:100%" />
        </el-form-item>
        <el-form-item label="说明" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="3" placeholder="支出用途说明（用于向业主公示）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <el-dialog title="审核消费事项" v-model="auditDialogVisible" width="420px">
      <el-form :model="auditForm" label-width="90px">
        <el-form-item label="事项名称">
          <span>{{ auditRow?.expenseName }}</span>
        </el-form-item>
        <el-form-item label="支出金额">
          <span>{{ auditRow?.amount ?? '-' }} 元</span>
        </el-form-item>
        <el-form-item label="审核结果">
          <el-radio-group v-model="auditForm.status">
            <el-radio :value="1">通过（向业主公示）</el-radio>
            <el-radio :value="2">驳回</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="auditDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitAudit">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeeExpensePage, addFeeExpense, updateFeeExpense, deleteFeeExpense, auditFeeExpense } from '@/api/fee/expense'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const userId = computed(() => userStore.userInfo.id || userStore.userInfo.userId)
const managerLike = computed(() => (userStore.roles || []).some(r => ['admin', 'property_admin', 'finance'].includes(r)))

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const auditDialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const auditRow = ref(null)

const searchForm = reactive({ pageNum: 1, pageSize: 10, expenseName: '', expenseType: '' })
const form = reactive({ id: null, expenseName: '', expenseType: 1, amount: null, expenseDate: '', content: '' })
const auditForm = reactive({ status: 1 })

const submitting = ref(false)
const dialogTitle = computed(() => isEdit.value ? '编辑消费事项' : '新增消费事项')

const auditText = (s) => ({ 0: '待审核', 1: '已通过', 2: '已驳回' }[s] || '待审核')
const auditTag = (s) => ({ 0: 'warning', 1: 'success', 2: 'danger' }[s] || 'info')

function canEdit(row) {
  return row.auditStatus === 0 && (managerLike.value || row.creatorId === userId.value)
}

const rules = {
  expenseName: [{ required: true, message: '请输入事项名称', trigger: 'blur' }],
  expenseType: [{ required: true, message: '请选择支出类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入支出金额', trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeeExpensePage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.expenseName = ''; searchForm.expenseType = ''; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null; form.expenseName = ''; form.expenseType = 1; form.amount = null; form.expenseDate = ''; form.content = '' }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) await updateFeeExpense(form)
    else await addFeeExpense(form)
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ } finally { submitting.value = false }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该消费事项吗？', '提示', { type: 'warning' })
  try {
    await deleteFeeExpense(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}

function openAudit(row) {
  auditRow.value = row
  auditForm.status = 1
  auditDialogVisible.value = true
}

async function submitAudit() {
  try {
    await auditFeeExpense({ id: auditRow.value.id, status: auditForm.status })
    ElMessage.success(auditForm.status === 1 ? '审核通过，已向业主公示' : '已驳回')
    auditDialogVisible.value = false
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
