<template>
  <div class="announcement-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑公告' : '新增公告' }}</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="通知" value="通知" />
            <el-option label="活动" value="活动" />
            <el-option label="紧急" value="紧急" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" rows="6" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="发布时间">
          <el-date-picker v-model="form.publishTime" type="datetime" placeholder="选择发布时间" />
        </el-form-item>
        <el-form-item label="过期时间">
          <el-date-picker v-model="form.expireTime" type="datetime" placeholder="选择过期时间" />
        </el-form-item>
        <el-form-item label="置顶">
          <el-switch v-model="form.isTop" :active-value="1" :inactive-value="0" />
        </el-form-item>
      </el-form>

      <template #footer>
        <el-button @click="handleCancel">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addAnnouncement, updateAnnouncement, getAnnouncement } from '../../api/announcement'

const router = useRouter()
const route = useRoute()
const formRef = ref(null)
const isEdit = ref(false)

const form = ref({
  type: '',
  title: '',
  content: '',
  publishTime: null,
  expireTime: null,
  isTop: 0
})

const rules = {
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updateAnnouncement({ id: route.query.id, ...form.value })
    ElMessage.success('更新成功')
  } else {
    await addAnnouncement(form.value)
    ElMessage.success('提交成功')
  }
  router.back()
}

onMounted(async () => {
  if (route.query.id) {
    isEdit.value = true
    const res = await getAnnouncement(route.query.id)
    form.value = res.data
  }
})
</script>

<style scoped>
.announcement-add { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>