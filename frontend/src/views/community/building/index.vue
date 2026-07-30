<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="楼栋编号">
        <el-input v-model="searchForm.buildingNo" placeholder="请输入楼栋编�? clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="primary" @click="handleAdd" v-permission="'community:building:add'">新增楼栋</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>

      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="buildingNo" label="楼栋编号" width="120" />
        <el-table-column prop="floorCount" label="楼层�? width="100" />
        <el-table-column prop="totalHouse" label="总户�? width="100" />
        <el-table-column prop="buildYear" label="建成年份" width="100" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" min-width="160" class-name="action-column" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleEdit(row)" v-permission="'community:building:edit'">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'community:building:delete'">删除</el-button>
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
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="请输入楼栋编�? />
        </el-form-item>
        <el-form-item label="楼层�? prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" placeholder="请输入楼层数" style="width: 100%" />
        </el-form-item>
        <el-form-item label="总户�? prop="totalHouse">
          <el-input-number v-model="form.totalHouse" :min="0" placeholder="请输入总户�? style="width: 100%" />
        </el-form-item>
        <el-form-item label="建成年份" prop="buildYear">
          <el-input-number v-model="form.buildYear" :min="1990" :max="2099" placeholder="请输入建成年�? style="width: 100%" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入备�? />
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
import { addBuilding, updateBuilding, deleteBuilding, getBuildingPage } from '@/api/community/building'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const formRef = ref(null)
const isEdit = ref(false)

const searchForm = reactive({ pageNum: 1, pageSize: 10, buildingNo: '' })
const form = reactive({ id: null, buildingNo: '', floorCount: null, totalHouse: null, buildYear: null, remark: '' })

const submitting = ref(false)

const dialogTitle = computed(() => isEdit.value ? '编辑楼栋' : '新增楼栋')

const rules = {
  buildingNo: [{ required: true, message: '请输入楼栋编�?, trigger: 'blur' }],
  floorCount: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  totalHouse: [{ required: true, message: '请输入总户�?, trigger: 'blur' }]
}

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getBuildingPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.buildingNo = ''; handleSearch() }

function handleAdd() { isEdit.value = false; resetForm(); dialogVisible.value = true }
function handleEdit(row) { isEdit.value = true; Object.assign(form, row); dialogVisible.value = true }
function resetForm() { formRef.value?.resetFields(); form.id = null }

async function handleSubmit() {
  if (submitting.value) return
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) { await updateBuilding(form) }
    else { await addBuilding(form) }
    ElMessage.success(isEdit.value ? '编辑成功' : '新增成功')
    dialogVisible.value = false
    fetchData()
  } catch (e) { /* handled by interceptor */ }
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定要删除该楼栋吗？', '提示', { type: 'warning' })
  try {
    await deleteBuilding(row.id)
    ElMessage.success('删除成功')
    fetchData()
  } catch (e) { /* handled */ }
}
</script>
