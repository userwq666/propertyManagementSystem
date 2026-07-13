<template>
  <div class="complaint-suggest-detail">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>投诉建议详情</span>
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
        <el-descriptions-item label="业主ID">{{ detail.ownerId }}</el-descriptions-item>
        <el-descriptions-item label="房屋ID">{{ detail.houseId }}</el-descriptions-item>
        <el-descriptions-item label="内容" :span="2">{{ detail.content }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detail.handleUser || '-' }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ detail.finishTime || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ detail.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="评分" :span="2">
          <el-rate v-if="detail.rating" v-model="detail.rating" disabled />
          <span v-else>-</span>
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detail.createTime }}</el-descriptions-item>
      </el-descriptions>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getComplaintSuggest } from '@/api/complaintSuggest'

const router = useRouter()
const route = useRoute()
const detail = ref({})

const getStatusType = (s) => ({ 0:'warning', 1:'primary', 2:'', 3:'success', 4:'success', 5:'danger' }[s] || 'info')
const getStatusText = (s) => ({ 0:'待受理', 1:'已受理', 2:'处理中', 3:'已完成', 4:'已评价', 5:'已驳回' }[s] || '未知')

const fetchData = async () => {
  const res = await getComplaintSuggest(route.params.id)
  detail.value = res.data
}

const handleBack = () => router.back()

onMounted(() => fetchData())
</script>
