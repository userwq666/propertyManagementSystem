<template>
  <div class="app-container">
    <div class="page-header">
      <h1>收费项目管理</h1>
      <el-button type="primary" @click="handleAdd" :icon="Plus">新增项目</el-button>
    </div>

    <el-form :inline="true" :model="query" class="search-form">
      <el-form-item label="项目名称">
        <el-input v-model="query.itemName" placeholder="搜索" clearable @keyup.enter="loadData" style="width:180px" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="loadData" :icon="Search">搜索</el-button>
        <el-button @click="resetQuery" :icon="RefreshRight">重置</el-button>
      </el-form-item>
    </el-form>

    <el-table v-loading="loading" :data="tableData" border stripe>
      <el-table-column prop="id" label="ID" width="60" />
      <el-table-column prop="itemName" label="项目名称" min-width="120" />
      <el-table-column prop="itemType" label="类型" width="100">
        <template #default="{ row }">{{ itemTypeLabel(row.itemType) }}</template>
      </el-table-column>
      <el-table-column prop="unitPrice" label="单价" width="100" />
      <el-table-column prop="unit" label="单位" width="80" />
      <el-table-column prop="cycleType" label="周期" width="100">
        <template #default="{ row }">{{ cycleTypeLabel(row.cycleType) }}</template>
      </el-table-column>
      <el-table-column label="状态" width="80">
        <template #default="{ row }">
          <el-switch :model-value="row.status===1" @change="(v) => handleStatusChange(row, v)" />
        </template>
      </el-table-column>
      <el-table-column label="操作" width="160" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" size="small" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" size="small" link @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-container">
      <el-pagination v-model:current-page="query.pageNum" v-model:page-size="query.pageSize" :page-sizes="[10,20,50]" layout="total,sizes,prev,pager,next" :total="total" @size-change="loadData" @current-change="loadData" />
    </div>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="500px" @close="formRef?.resetFields()">
      <el-form ref="formRef" :model="dialog.form" :rules="rules" label-width="90px">
        <el-form-item label="项目名称" prop="itemName">
          <el-input v-model="dialog.form.itemName" placeholder="如：物业管理费" />
        </el-form-item>
        <el-form-item label="类型" prop="itemType">
          <el-select v-model="dialog.form.itemType" style="width:100%">
            <el-option v-for="o in itemTypes" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="单价" prop="unitPrice">
          <el-input-number v-model="dialog.form.unitPrice" :min="0" :precision="2" style="width:100%" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="dialog.form.unit" placeholder="如：元/m²·月" />
        </el-form-item>
        <el-form-item label="周期" prop="cycleType">
          <el-select v-model="dialog.form.cycleType" style="width:100%">
            <el-option v-for="o in cycleTypes" :key="o.value" :label="o.label" :value="o.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="dialog.form.status">
            <el-radio :value="1">启用</el-radio>
            <el-radio :value="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible=false">取消</el-button>
        <el-button type="primary" :loading="dialog.loading" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from "vue"
import { ElMessage, ElMessageBox } from "element-plus"
import { Plus, Search, RefreshRight } from "@element-plus/icons-vue"
import { getItemList, getItemInfo, addItem, updateItem, deleteItem, updateItemStatus } from "@/api/fee/item"

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const formRef = ref(null)

const query = reactive({ pageNum: 1, pageSize: 10, itemName: "" })
const dialog = reactive({ visible: false, title: "", loading: false, form: {} })
const rules = {
  itemName: [{ required: true, message: "请输入项目名称", trigger: "blur" }],
  itemType: [{ required: true, message: "请选择类型", trigger: "change" }],
  unitPrice: [{ required: true, message: "请输入单价", trigger: "blur" }],
  cycleType: [{ required: true, message: "请选择周期", trigger: "change" }]
}

const itemTypes = [
  { value: 1, label: "物业管理费" }, { value: 2, label: "水费" }, { value: 3, label: "电费" },
  { value: 4, label: "燃气费" }, { value: 5, label: "供暖费" }, { value: 6, label: "停车费" },
  { value: 7, label: "垃圾处理费" }, { value: 8, label: "维修基金" }, { value: 9, label: "其他" }
]
const cycleTypes = [
  { value: 1, label: "每月" }, { value: 2, label: "每季" },
  { value: 3, label: "每年" }, { value: 4, label: "一次性" }, { value: 5, label: "自定义" }
]

const itemTypeLabel = (v) => itemTypes.find(o => o.value === v)?.label || "未知"
const cycleTypeLabel = (v) => cycleTypes.find(o => o.value === v)?.label || "未知"

const loadData = async () => {
  loading.value = true
  try {
    const res = await getItemList(query)
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch { tableData.value = [] }
  loading.value = false
}

const resetQuery = () => { query.itemName = ""; query.pageNum = 1; loadData() }

const handleAdd = () => {
  dialog.title = "新增项目"
  dialog.form = { id: null, itemName: "", itemType: 1, unitPrice: 0, unit: "", cycleType: 1, status: 1 }
  dialog.visible = true
}

const handleEdit = async (row) => {
  dialog.title = "编辑项目"
  try {
    const res = await getItemInfo(row.id)
    dialog.form = { ...res.data }
    dialog.visible = true
  } catch {}
}

const submitForm = async () => {
  try { await formRef.value.validate() } catch { return }
  dialog.loading = true
  try {
    if (dialog.form.id) await updateItem(dialog.form)
    else await addItem(dialog.form)
    ElMessage.success(dialog.form.id ? "修改成功" : "新增成功")
    dialog.visible = false
    loadData()
  } catch {}
  dialog.loading = false
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm(`确定删除 "${row.itemName}" 吗？`, "提示", { type: "warning" })
    await deleteItem(row.id)
    ElMessage.success("删除成功")
    loadData()
  } catch {}
}

const handleStatusChange = async (row, v) => {
  try {
    await updateItemStatus(row.id, v ? 1 : 0)
    ElMessage.success(v ? "已启用" : "已禁用")
    loadData()
  } catch {}
}

onMounted(() => { loadData() })
</script>

<style lang="scss" scoped>
.app-container { padding: 20px; }
.page-header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px;
  h1 { font-size: 20px; font-weight: 600; margin: 0; }
}
.search-form { margin-bottom: 16px; }
.pagination-container { display: flex; justify-content: flex-end; margin-top: 16px; }
</style>
