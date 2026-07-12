<template>
  <div class="operlog-container">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>操作日志</span>
          <el-button type="danger" @click="handleClean">清空日志</el-button>
        </div>
      </template>
      
      <!-- 表格 -->
      <el-table :data="tableData" v-loading="loading" border stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="userName" label="操作人" width="120" />
        <el-table-column prop="operModule" label="操作模块" width="120" />
        <el-table-column prop="operType" label="操作类型" width="100" />
        <el-table-column prop="operIp" label="操作IP" width="120" />
        <el-table-column prop="operDesc" label="操作描述" />
        <el-table-column prop="createTime" label="操作时间" width="180" />
      </el-table>
      
      <!-- 分页 -->
      <Pagination :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="fetchData" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOperLogPage, cleanOperLog } from '../../../api/operlog'
import Pagination from '../../../components/Pagination.vue'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  pageNum: 1,
  pageSize: 10
})

const fetchData = async () => {
  loading.value = true
  try {
    const res = await getOperLogPage(queryParams)
    tableData.value = res.data.records
    total.value = res.data.total
  } finally {
    loading.value = false
  }
}

const handleClean = () => {
  ElMessageBox.confirm('确定要清空所有操作日志吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await cleanOperLog()
      ElMessage.success('清空成功')
      fetchData()
    } catch (error) {
      ElMessage.error(error.message || '清空失败')
    }
  })
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.operlog-container {
  padding: 0;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
