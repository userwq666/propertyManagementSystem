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
        <el-table-column label="通知角色" width="140">
          <template #default="{ row }">{{ noticeRolesText(row.noticeRoles) }}</template>
        </el-table-column>
        <el-table-column label="发布状态" width="90">
          <template #default="{ row }">
            <el-tag :type="row.published === 1 ? 'success' : 'info'">{{ row.published === 1 ? '已发布' : '未发布' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalTimes" label="收费次数" width="90">
          <template #default="{ row }">{{ row.totalTimes ? row.totalTimes + ' 次' : '长期' }}</template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'">{{ row.status === 1 ? '启用' : '停用' }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="240" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'fee:item:edit'">编辑</el-button>
            <el-button v-if="row.status === 0" type="danger" size="small" @click="handleDelete(row)" v-permission="'fee:item:delete'">删除</el-button>
            <el-button
              :type="row.status === 1 ? 'warning' : 'success'"
              size="small"
              @click="handleToggleStatus(row)"
            v-permission="'fee:item:edit'">{{ row.status === 1 ? '停用' : '启用' }}</el-button>
            <el-button v-if="row.published !== 1" type="primary" size="small" @click="handlePublish(row)" v-permission="'fee:item:edit'">发布</el-button>
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
        <el-form-item label="类型">
          <el-tag type="primary">物业费</el-tag>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="form.unitPrice" :min="0" :precision="2" placeholder="请输入单价" style="width: 100%" />
        </el-form-item>
        <el-form-item label="单位" prop="unit">
          <el-input v-model="form.unit" placeholder="请输入单位（如：元/㎡、元/吨）" />
        </el-form-item>
        <el-form-item label="收费方式">
          <el-tag>单次收费</el-tag>
          <div class="form-tip">生成账单时按房屋面积 × 单价自动计算金额</div>
        </el-form-item>
        <el-form-item label="通知角色" prop="noticeRoles">
          <el-select v-model="form.noticeRoles" multiple placeholder="选择通知角色" style="width: 100%">
            <el-option v-for="r in roleList" :key="r.id" :label="r.roleName" :value="r.roleKey" />
          </el-select>
          <div class="form-tip">按角色身份通知，如选择"业主"则通知业主缴费</div>
        </el-form-item>
        <el-form-item label="收费范围">
          <el-tree
            ref="scopeTreeRef"
            :data="scopeTreeData"
            show-checkbox
            node-key="id"
            default-expand-all
            :props="{ label: 'label', children: 'children' }"
            style="width: 100%; border: 1px solid #ebeef5; border-radius: 4px; padding: 8px; max-height: 220px; overflow: auto;"
          />
          <div class="form-tip">选择业主/楼栋/房屋；勾选父级默认包含全部子级；勾选"全部业主"则向全体业主收费</div>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">停用</el-radio>
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
import { addFeeItem, updateFeeItem, deleteFeeItem, getFeeItemPage, updateFeeItemStatus, publishFeeItem } from '@/api/fee/item'
import { getRoleList } from '@/api/system/role'
import { getBuildingPage } from '@/api/community/building'
import { getHousePage } from '@/api/community/house'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)
const roleList = ref([])
const scopeTreeRef = ref(null)
const scopeTreeData = ref([])

const searchForm = reactive({ pageNum: 1, pageSize: 10, itemName: '', status: null })
const form = reactive({ id: null, itemName: '物业费', itemType: 1, unitPrice: null, unit: '元/㎡', cycleType: 5, noticeRoles: [], totalTimes: 1, scopeType: 1, scopeIds: [], description: '', status: 0 })

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

onMounted(() => { fetchData(); loadRoles(); loadScopeTree() })

async function loadRoles() {
  try {
    const res = await getRoleList()
    roleList.value = res.data || []
  } catch (e) { /* ignore */ }
}

async function loadScopeTree() {
  try {
    const [bRes, hRes] = await Promise.all([
      getBuildingPage({ pageNum: 1, pageSize: 200 }, { silent: true }),
      getHousePage({ pageNum: 1, pageSize: 200 }, { silent: true })
    ])
    const buildings = bRes.data?.records || []
    const houses = hRes.data?.records || []
    scopeTreeData.value = [{
      id: 'all',
      label: '全部业主',
      nodeType: 'all',
      children: buildings.map(b => ({
        id: 'b' + b.id,
        label: b.buildingNo + '栋',
        nodeType: 'building',
        buildingId: b.id,
        children: houses.filter(h => h.buildingId === b.id).map(h => ({
          id: 'h' + h.id,
          label: h.roomNo + (h.ownerName ? '（' + h.ownerName + '）' : '（未关联业主）'),
          nodeType: 'house',
          houseId: h.id
        }))
      }))
    }]
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
  // 回填范围树勾选
  requestAnimationFrame(() => {
    if (!scopeTreeRef.value) return
    scopeTreeRef.value.setCheckedKeys([])
    if (row.scopeType === 1) {
      scopeTreeRef.value.setCheckedKeys(['all'])
    } else if (row.scopeIds && row.scopeIds.length) {
      const prefix = row.scopeType === 2 ? 'b' : 'h'
      scopeTreeRef.value.setCheckedKeys(row.scopeIds.map(id => prefix + id))
    }
  })
}
function resetForm() { formRef.value?.resetFields(); form.id = null; form.itemName = '物业费'; form.itemType = 1; form.cycleType = 5; form.totalTimes = 1; form.noticeRoles = []; form.scopeType = 1; form.scopeIds = []; form.status = 0; scopeTreeRef.value?.setCheckedKeys([]) }

function resolveScope() {
  const tree = scopeTreeRef.value
  if (!tree) return { scopeType: 1, scopeIds: [] }
  const checked = tree.getCheckedKeys()
  const half = tree.getHalfCheckedKeys()
  if (checked.includes('all')) return { scopeType: 1, scopeIds: [] }
  const houseIds = checked.filter(k => String(k).startsWith('h')).map(k => Number(String(k).slice(1)))
  const checkedBuildings = checked.filter(k => String(k).startsWith('b')).map(k => Number(String(k).slice(1)))
  const halfBuildings = half.filter(k => String(k).startsWith('b')).map(k => Number(String(k).slice(1)))
  const houseBuildingIds = houseIds.map(hid => {
    const node = scopeTreeData.value[0]?.children.flatMap(b => b.children).find(h => h.houseId === hid)
    return node ? node.buildingId : null
  }).filter(Boolean)
  const allBuildings = [...new Set([...checkedBuildings, ...halfBuildings, ...houseBuildingIds])]
  if (allBuildings.length) return { scopeType: 2, scopeIds: allBuildings }
  return { scopeType: 3, scopeIds: houseIds }
}

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    const scope = resolveScope()
    const payload = { ...form, noticeRoles: Array.isArray(form.noticeRoles) ? form.noticeRoles.join(',') : form.noticeRoles, scopeType: scope.scopeType, scopeIds: scope.scopeIds }
    if (isEdit.value) { await updateFeeItem(payload) }
    else { await addFeeItem(payload) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handlePublish(row) {
  await ElMessageBox.confirm('发布后将为范围内业主自动生成待缴费账单，确定发布？', '提示', { type: 'warning' })
  try {
    await publishFeeItem(row.id)
    ElMessage.success('发布成功，已生成待缴费账单')
    fetchData()
  } catch (e) { /* handled */ }
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
  const newStatus = row.status === 1 ? 0 : 1
  const action = newStatus === 1 ? '启用' : '停用'
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
