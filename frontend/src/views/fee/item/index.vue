<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="项目名称">
        <el-input v-model="searchForm.itemName" placeholder="请输入项目名称" clearable />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
          <el-option label="停用" :value="0" />
          <el-option label="启用" :value="1" />
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
          <el-button type="primary" @click="handleAdd" v-permission="'fee:item:add'">新增项目</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="itemName" label="项目名称" width="140" />
        <el-table-column label="类型" width="100">
          <template #default="{ row }">{{ itemTypeLabel(row.itemType) }}</template>
        </el-table-column>
        <el-table-column prop="unitPrice" label="单价" width="100" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column label="周期" width="80">
          <template #default="{ row }">{{ cycleTypeLabel(row.cycleType) }}</template>
        </el-table-column>
        <el-table-column prop="dueDay" label="最迟收款日" width="100">
          <template #default="{ row }">{{ row.dueDay ? '每月' + row.dueDay + '日' : '-' }}</template>
        </el-table-column>
        <el-table-column label="通知角色" width="140">
          <template #default="{ row }">{{ noticeRolesText(row.noticeRoles) }}</template>
        </el-table-column>
        <el-table-column prop="totalTimes" label="收费次数" width="90">
          <template #default="{ row }">{{ row.totalTimes ? row.totalTimes + ' 次' : '长期' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 0 ? 'success' : 'danger'">{{ row.status === 0 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="240" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'fee:item:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'fee:item:delete'">删除</el-button>
            <el-button
              :type="row.status === 0 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
             v-permission="'fee:item:edit'">{{ row.status === 0 ? '停用' : '启用' }}</el-button>
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
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="form.itemName" placeholder="请输入项目名称" />
        </el-form-item>
        <el-form-item label="类型" prop="itemType">
          <el-select v-model="form.itemType" placeholder="请选择类型" style="width: 100%">
            <el-option label="物业费" :value="1" />
            <el-option label="车位费" :value="2" />
            <el-option label="水费" :value="3" />
            <el-option label="电费" :value="4" />
            <el-option label="燃气费" :value="5" />
            <el-option label="暖气费" :value="6" />
            <el-option label="其他" :value="9" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="请输入单价" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位（如：元/㎡、元/吨）" />
        </el-form-item>
        <el-form-item label="周期" prop="cycleType">
          <el-select v-model="form.cycleType" placeholder="请选择周期" style="width: 100%">
            <el-option label="按月" :value="1" />
            <el-option label="按季" :value="2" />
            <el-option label="按半年" :value="3" />
            <el-option label="按年" :value="4" />
            <el-option label="一次性" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="最迟收款日" prop="dueDay">
          <el-input-number v-model="form.dueDay" :min="1" :max="31" placeholder="每月第几天截止" style="width: 100%" />
          <div class="form-tip">每个收费周期内的截止日期（如 15 = 每月15日）</div>
        </el-form-item>
        <el-form-item label="通知角色" prop="noticeRoles">
          <el-select v-model="form.noticeRoles" multiple placeholder="选择通知角色" style="width: 100%">
            <el-option v-for="r in roleList" :key="r.id" :label="r.roleName" :value="r.roleKey" />
          </el-select>
          <div class="form-tip">按角色身份通知，如选择"业主"则通知业主缴费</div>
        </el-form-item>
        <el-form-item label="收费次数" prop="totalTimes">
          <el-input-number v-model="form.totalTimes" :min="0" placeholder="0=长期周期收费" style="width: 100%" />
          <div class="form-tip">0 表示长期周期性收费；填写 N 表示仅收费 N 次</div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="0">启用</el-radio>
            <el-radio :value="1">停用</el-radio>
          </el-radio-group>
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
import { addFeeItem, updateFeeItem, deleteFeeItem, getFeeItemPage, updateFeeItemStatus } from '@/api/fee/item'
import { getRoleList } from '@/api/system/role'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const roleList = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, itemName: '', status: null })
const form = reactive({ id: null, itemName: '', itemType: 1, unitPrice: null, unit: '', cycleType: 1, dueDay: null, noticeRoles: [], totalTimes: 0, description: '', status: 0 })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑收费项目' : '新增收费项目')

const rules = {
  itemName: [{ required: true, message: '请输入项目名称', trigger: 'blur' }],
  itemType: [{ required: true, message: '请选择类型', trigger: 'change' }],
  unitPrice: [{ required: true, message: '请输入单价', trigger: 'blur' }],
  unit: [{ required: true, message: '请输入单位', trigger: 'blur' }],
  cycleType: [{ required: true, message: '请选择周期', trigger: 'change' }]
}

const itemTypeMap = { 1: '物业费', 2: '车位费', 3: '水费', 4: '电费', 5: '燃气费', 6: '暖气费', 9: '其他' }
const cycleTypeMap = { 1: '按月', 2: '按季', 3: '按半年', 4: '按年', 5: '一次性' }
function itemTypeLabel(t) { return itemTypeMap[t] || '未知' }
function cycleTypeLabel(t) { return cycleTypeMap[t] || '未知' }
function noticeRolesText(roles) {
  if (!roles) return '-'
  const keys = String(roles).split(',')
  const nameMap = Object.fromEntries(roleList.value.map(r => [r.roleKey, r.roleName]))
  return keys.map(k => nameMap[k] || k).join('、')
}

onMounted(() => { fetchData(); loadRoles() })

async function loadRoles() {
  try {
    const res = await getRoleList()
    roleList.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function fetchData() {
  loading.value = true
  try {
    const res = await getFeeItemPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.itemName = ''; searchForm.status = null; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, { ...row, noticeRoles: row.noticeRoles ? String(row.noticeRoles).split(',') : [] })
  dialogVisible.value = true
}
function resetForm() { formRef.value?.resetFields(); form.id = null; form.itemType = 1; form.cycleType = 1; form.dueDay = null; form.noticeRoles = []; form.totalTimes = 0; form.status = 0 }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateFeeItem(form) }
    else { await addFeeItem(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该收费项目吗？', '提示', { type: 'warning' })
  try {
    await deleteFeeItem(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}

async function handleToggleStatus(row) {
  const newStatus = row.status === 0 ? 1 : 0
  const action = newStatus === 1 ? '停用' : '启用'
  await ElMessageBox.confirm(`确定要${action}该收费项目吗？`, '提示', { type: 'warning' })
  try {
    await updateFeeItemStatus(row.id, newStatus)
    ElMessage.success(`${action}成功`)
    fetchData()
  } catch (e) { /* handled */ }
}
</script>

<style scoped>
.form-tip { font-size: 12px; color: #909399; line-height: 1.6; margin-top: 4px; }
</style>
