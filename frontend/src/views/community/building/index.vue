<template>
  <div class="building-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>楼栋管理</span>
          <el-button type="primary" @click="handleAdd">新增楼栋</el-button>
        </div>
      </template>
      
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="楼栋编号">
          <el-input v-model="queryParams.buildingNo" placeholder="请输入楼栋编号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
      
      <el-table :data="tableData" border stripe>
        <el-table-column prop="buildingNo" label="楼栋编号" />
        <el-table-column prop="floorCount" label="总楼层" />
        <el-table-column prop="totalHouse" label="总户数" />
        <el-table-column prop="buildYear" label="建成年份" />
        <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        <el-table-column label="操作" width="180">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="楼栋编号" prop="buildingNo">
          <el-input v-model="form.buildingNo" placeholder="请输入楼栋编号" />
        </el-form-item>
        <el-form-item label="总楼层" prop="floorCount">
          <el-input-number v-model="form.floorCount" :min="1" />
        </el-form-item>
        <el-form-item label="总户数" prop="totalHouse">
          <el-input-number v-model="form.totalHouse" :min="1" />
        </el-form-item>
        <el-form-item label="建成年份" prop="buildYear">
          <el-input-number v-model="form.buildYear" :min="1900" :max="2100" />
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingPage, addBuilding, updateBuilding, deleteBuilding } from '@/api/building'

const tableData = ref([])
const total = ref(0)
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10,
  buildingNo: ''
})

const form = reactive({
  id: null,
  buildingNo: '',
  floorCount: null,
  totalHouse: null,
  buildYear: null,
  remark: ''
})

const rules = {
  buildingNo: [{ required: true, message: '请输入楼栋编号', trigger: 'blur' }]
}

const loadData = async () => {
  const res = await getBuildingPage(queryParams)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleSearch = () => {
  queryParams.pageNum = 1
  loadData()
}

const handleReset = () => {
  queryParams.buildingNo = ''
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
  dialogTitle.value = '新增楼栋'
  Object.assign(form, { id: null, buildingNo: '', floorCount: null, totalHouse: null, buildYear: null, remark: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑楼栋'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (form.id) {
    await updateBuilding(form)
    ElMessage.success('编辑成功')
  } else {
    await addBuilding(form)
    ElMessage.success('新增成功')
  }
  dialogVisible.value = false
  loadData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该楼栋吗？', '提示', { type: 'warning' })
  await deleteBuilding(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.building-container {
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
