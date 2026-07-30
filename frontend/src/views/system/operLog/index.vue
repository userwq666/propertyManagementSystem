<template>
  <div>
    <el-form :inline="true" :model="searchForm" class="search-form">
      <el-form-item label="用户名">
        <el-input v-model="searchForm.userName" placeholder="请输入用户名" clearable />
      </el-form-item>
      <el-form-item label="模块名称">
        <el-input v-model="searchForm.operModule" placeholder="请输入模块名称" clearable />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" @click="handleSearch">搜索</el-button>
        <el-button @click="resetSearch">重置</el-button>
      </el-form-item>
    </el-form>

    <div class="table-container">
      <div class="toolbar">
        <div class="toolbar-left">
          <el-button type="danger" @click="handleClean" v-permission="'system:operLog:delete'">清理日志</el-button>
        </div>
        <div class="toolbar-right">
          <el-button @click="fetchData">刷新</el-button>
        </div>
      </div>
      <el-table :data="tableData" border stripe v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="operName" label="操作人" width="100" />
        <el-table-column prop="module" label="模块名称" width="120" />
        <el-table-column prop="operType" label="操作类型" width="100" />
        <el-table-column prop="requestMethod" label="请求方式" width="100" />
        <el-table-column prop="requestUrl" label="请求URL" show-overflow-tooltip />
        <el-table-column prop="operIp" label="操作IP" width="140" />
        <el-table-column prop="operTime" label="操作时间" width="180" />
        <el-table-column label="操作" min-width="auto" style="white-space:nowrap" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="handleDetail(row)">详情</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)" v-permission="'system:operLog:delete'">删除</el-button>
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getOperLogPage, deleteOperLog, cleanOperLog } from '@/api/system/operLog'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const searchForm = reactive({ pageNum: 1, pageSize: 10, userName: '', operModule: '' })

onMounted(() => fetchData())

async function fetchData() {
  loading.value = true
  try {
    const res = await getOperLogPage({ ...searchForm })
    tableData.value = res.data.records
    total.value = res.data.total
  } finally { loading.value = false }
}

function handleSearch() { searchForm.pageNum = 1; fetchData() }
function resetSearch() { searchForm.userName = ''; searchForm.operModule = ''; handleSearch() }

function handleDetail(row) {
  const content = [
    '操作人: ' + (row.operName || ''),
    '模块: ' + (row.module || ''),
    '操作类型: ' + (row.operType || ''),
    '请求方式: ' + (row.requestMethod || ''),
    '请求URL: ' + (row.requestUrl || ''),
    '操作IP: ' + (row.operIp || ''),
    '操作参数: ' + (row.operParam || ''),
    '返回结果: ' + (row.jsonResult || ''),
    '操作时间: ' + (row.operTime || '')
  ].join('<br/>')
  ElMessageBox.alert(content, '操作日志详情', { dangerouslyUseHTMLString: true })
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该日志吗？', '提示', { type: 'warning' })
  try { await deleteOperLog(row.id); ElMessage.success('删除成功'); fetchData() } catch (e) {}
}

async function handleClean() {
  const { value: days } = await ElMessageBox.prompt('请输入保留天数', '清理日志', {
    type: 'warning',
    inputValue: '30',
    inputPattern: /^\d+$/,
    inputErrorMessage: '请输入有效天数'
  })
  try {
    await cleanOperLog({ days })
    ElMessage.success('清理成功')
    fetchData()
  } catch (e) {}
}
</script>