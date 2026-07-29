<template>
  <div class="page-container">
    <!-- 搜索表单 -->
    <el-card class="search-card">
      <el-form :model="searchForm" inline>
        <el-form-item label="用户名">
          <el-input v-model="searchForm.username" placeholder="请输入用户名" clearable />
        </el-form-item>
        <el-form-item label="模块名称">
          <el-input v-model="searchForm.module" placeholder="请输入模块名称" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="Search" @click="handleSearch">搜索</el-button>
          <el-button icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 工具栏 -->
    <el-card class="toolbar-card">
      <el-button type="danger" icon="Delete" @click="handleClear">清理日志</el-button>
      <el-button icon="Refresh" @click="handleRefresh">刷新</el-button>
    </el-card>

    <!-- 数据表格 -->
    <el-card>
      <el-table :data="tableData" border stripe v-loading="loading" style="width: 100%">
        <el-table-column prop="username" label="操作人" min-width="100" />
        <el-table-column prop="module" label="模块名称" min-width="120" show-overflow-tooltip />
        <el-table-column prop="type" label="操作类型" min-width="100" />
        <el-table-column prop="method" label="请求方式" min-width="80">
          <template #default="{ row }">
            <el-tag
              :type="row.method === 'GET' ? 'success' : row.method === 'POST' ? 'primary' : row.method === 'PUT' ? 'warning' : 'danger'"
            >{{ row.method }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="url" label="请求URL" min-width="200" show-overflow-tooltip />
        <el-table-column prop="ip" label="操作IP" min-width="140" />
        <el-table-column prop="createTime" label="操作时间" min-width="160" />
        <el-table-column label="操作" min-width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link icon="View" @click="handleView(row)">查看</el-button>
            <el-button type="danger" link icon="Delete" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <el-pagination
        v-model:current-page="pagination.current"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50, 100]"
        layout="total, sizes, prev, pager, next, jumper"
        background
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="handleSearch"
        @current-change="handleSearch"
      />
    </el-card>

    <!-- 查看详情对话框 -->
    <el-dialog v-model="detailVisible" title="操作日志详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="操作人">{{ detail.username }}</el-descriptions-item>
        <el-descriptions-item label="模块名称">{{ detail.module }}</el-descriptions-item>
        <el-descriptions-item label="操作类型">{{ detail.type }}</el-descriptions-item>
        <el-descriptions-item label="请求方式">
          <el-tag
            :type="detail.method === 'GET' ? 'success' : detail.method === 'POST' ? 'primary' : detail.method === 'PUT' ? 'warning' : 'danger'"
          >{{ detail.method }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="请求URL" :span="2">{{ detail.url }}</el-descriptions-item>
        <el-descriptions-item label="操作IP">{{ detail.ip }}</el-descriptions-item>
        <el-descriptions-item label="操作时间">{{ detail.createTime }}</el-descriptions-item>
        <el-descriptions-item label="请求参数" :span="2">
          <pre class="json-pre">{{ detail.params }}</pre>
        </el-descriptions-item>
        <el-descriptions-item label="返回结果" :span="2">
          <pre class="json-pre">{{ detail.result }}</pre>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getOperLogPage, getOperLogById, deleteOperLog, clearOperLog } from '@/api/system/operLog'
import { ElMessage, ElMessageBox } from 'element-plus'

const loading = ref(false)
const tableData = ref([])
const pagination = reactive({ current: 1, size: 10, total: 0 })

const searchForm = reactive({ username: '', module: '' })

const detailVisible = ref(false)
const detail = reactive({})

const fetchData = async () => {
  loading.value = true
  try {
    const params = { current: pagination.current, size: pagination.size }
    if (searchForm.username) params.username = searchForm.username
    if (searchForm.module) params.module = searchForm.module
    const res = await getOperLogPage(params)
    tableData.value = res.data.records
    pagination.total = res.data.total
    pagination.current = res.data.current
    pagination.size = res.data.size
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.current = 1
  fetchData()
}

const handleReset = () => {
  searchForm.username = ''
  searchForm.module = ''
  handleSearch()
}

const handleRefresh = () => fetchData()

const handleView = async (row) => {
  const res = await getOperLogById(row.id)
  Object.assign(detail, res.data)
  detailVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确认删除该操作日志吗？', '提示', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await deleteOperLog(row.id)
    ElMessage.success('删除成功')
    fetchData()
  }).catch(() => {})
}

const handleClear = () => {
  ElMessageBox.confirm('确认清理所有操作日志吗？此操作不可恢复！', '警告', {
    type: 'warning',
    confirmButtonText: '确定',
    cancelButtonText: '取消'
  }).then(async () => {
    await clearOperLog()
    ElMessage.success('清理成功')
    fetchData()
  }).catch(() => {})
}

onMounted(() => {
  fetchData()
})
</script>

<style scoped>
.page-container .el-card {
  margin-bottom: 16px;
}
.search-card .el-form-item {
  margin-bottom: 0;
}
.json-pre {
  max-height: 200px;
  overflow: auto;
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
  font-size: 12px;
}
</style>
