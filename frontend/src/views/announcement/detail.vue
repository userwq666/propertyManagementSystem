<template>
  <div class="announcement-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>公告详情</span>
          <el-button @click="handleBack">返回</el-button>
        </div>
      </template>

      <el-descriptions :column="2" border>
        <el-descriptions-item label="ID">{{ detail.id }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detail.title }}</el-descriptions-item>
        <el-descriptions-item label="类型">{{ detail.type }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detail.status)">{{ getStatusText(detail.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="置顶">
          <el-tag v-if="detail.isTop === 1" type="danger">置顶</el-tag>
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="创建人">{{ detail.createUser }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="发布时间">{{ detail.publishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="过期时间">{{ detail.expireTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getAnnouncement } from '../../api/announcement'

const router = useRouter()
const route = useRoute()
const detail = ref({})

const getStatusType = (s) => ({ 0:'info', 1:'warning', 2:'success', 3:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'草稿', 1:'预发布', 2:'已发布', 3:'已过期' }[s] || '未知')

const fetchData = async () => {
  const res = await getAnnouncement(route.params.id)
  detail.value = res.data
}

const handleBack = () => router.back()

onMounted(() => fetchData())
</script>

<style scoped>
.announcement-detail { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>