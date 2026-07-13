<template>
  <div class="complaint-suggest-add">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>新增投诉建议</span>
        </div>
      </template>

      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="业主ID" prop="ownerId">
          <el-input v-model="form.ownerId" placeholder="请输入业主ID" />
        </el-form-item>
        <el-form-item label="房屋ID" prop="houseId">
          <el-input v-model="form.houseId" placeholder="请输入房屋ID" />
        </el-form-item>
        <el-form-item label="类型" prop="type">
          <el-select v-model="form.type" placeholder="请选择类型">
            <el-option label="投诉" value="投诉" />
            <el-option label="建议" value="建议" />
            <el-option label="其他" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" rows="6" placeholder="请输入内容" />
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
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { addComplaintSuggest } from '@/api/complaintSuggest'

const router = useRouter()
const formRef = ref(null)

const form = ref({
  ownerId: '',
  houseId: '',
  type: '',
  title: '',
  content: ''
})

const rules = {
  ownerId: [{ required: true, message: '请输入业主ID', trigger: 'blur' }],
  houseId: [{ required: true, message: '请输入房屋ID', trigger: 'blur' }],
  type: [{ required: true, message: '请选择类型', trigger: 'change' }],
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }]
}

const handleCancel = () => router.back()

const handleSubmit = async () => {
  await formRef.value.validate()
  await addComplaintSuggest(form.value)
  ElMessage.success('提交成功')
  router.back()
}
</script>