<template>
  <div class="announcement">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告管理</span>
          <el-button type="primary" @click="handleAdd">新增公告</el-button>
        </div>
      </template>

      <!-- 搜索栏 -->
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="类型">
          <el-select v-model="searchForm.type" placeholder="请选择类型" clearable>
            <el-option label="通知" value="通知" />
            <el-option label="活动" value="活动" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择状态" clearable>
            <el-option label="草稿" :value="0" />
            <el-option label="预发布" :value="1" />
            <el-option label="已发布" :value="2" />
            <el-option label="已过期" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 数据表格 -->
      <el-table :data="tableData" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="title" label="标题" show-overflow-tooltip />
        <el-table-column prop="type" label="类型" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="isTop" label="置顶" width="80">
          <template #default="{ row }">
            <el-tag v-if="row.isTop === 1" type="danger">置顶</el-tag>
            <span v-else>-</span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" />
        <el-table-column label="操作" width="250">
          <template #default="{ row }">
            <el-button size="small" @click="handleDetail(row)">详情</el-button>
            <el-button size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 0" size="small" type="success" @click="handlePublish(row)">发布</el-button>
            <el-button v-if="row.status === 2" size="small" type="warning" @click="handleOffline(row)">下架</el-button>
            <el-button size="small" type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination v-model:current-page="currentPage" v-model:page-size="pageSize"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        @size-change="handleSizeChange" @current-change="handleCurrentChange" />
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { pageAnnouncement, deleteAnnouncement, updateAnnouncementStatus } from '../../api/announcement'

const router = useRouter()
const tableData = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const total = ref(0)

const searchForm = ref({ type: null, status: null })

const getStatusType = (s) => ({ 0:'info', 1:'warning', 2:'success', 3:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'草稿', 1:'预发布', 2:'已发布', 3:'已过期' }[s] || '未知')

const fetchData = async () => {
  const params = { pageNum: currentPage.value, pageSize: pageSize.value }
  if (searchForm.value.type) params.type = searchForm.value.type
  if (searchForm.value.status !== null) params.status = searchForm.value.status
  const res = await pageAnnouncement(params)
  tableData.value = res.data.records
  total.value = res.data.total
}

const handleAdd = () => router.push('/announcement/add')
const handleDetail = (row) => router.push(`/announcement/${row.id}`)
const handleEdit = (row) => router.push(`/announcement/add?id=${row.id}`)
const handleSearch = () => { currentPage.value = 1; fetchData() }
const handleReset = () => { searchForm.value = { type: null, status: null }; handleSearch() }
const handleSizeChange = () => { currentPage.value = 1; fetchData() }
const handleCurrentChange = () => fetchData()

const handlePublish = async (row) => {
  await ElMessageBox.confirm('确认发布该公告？', '提示')
  await updateAnnouncementStatus({ id: row.id, status: 2 })
  ElMessage.success('发布成功')
  fetchData()
}

const handleOffline = async (row) => {
  await ElMessageBox.confirm('确认下架该公告？', '提示')
  await updateAnnouncementStatus({ id: row.id, status: 3 })
  ElMessage.success('下架成功')
  fetchData()
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该公告？', '提示')
  await deleteAnnouncement(row.id)
  ElMessage.success('删除成功')
  fetchData()
}

onMounted(() => fetchData())
</script>

<style scoped>
.announcement { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-form { margin-bottom: 20px; }
.el-pagination { margin-top: 20px; justify-content: flex-end; }
</style>