<template>
  <div class="file-upload-container">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="headers"
      :multiple="multiple"
      :limit="limit"
      :accept="accept"
      :file-list="fileList"
      :on-exceed="handleExceed"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :on-preview="handlePreview"
      :before-upload="beforeUpload"
      :disabled="disabled"
    >
      <slot>
        <el-button type="primary" :disabled="disabled">
          <upload-filled /> 点击上传
        </el-button>
      </slot>
      <template #tip>
        <div class="el-upload__tip" v-if="tip">
          {{ tip }}
        </div>
      </template>
    </el-upload>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { UploadFilled } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  action: { type: String, default: '/common/upload' },
  multiple: { type: Boolean, default: true },
  limit: { type: Number, default: 5 },
  accept: { type: String, default: '' },
  tip: { type: String, default: '' },
  disabled: { type: Boolean, default: false },
  fileSize: { type: Number, default: 10 }
})

const emit = defineEmits(['update:modelValue', 'success', 'remove'])

const uploadRef = ref(null)
const fileList = ref([])

const uploadUrl = computed(() => props.action)
const headers = computed(() => ({ Authorization: `Bearer ${getToken()}` }))

watch(() => props.modelValue, (val) => {
  if (val && val.length > 0 && fileList.value.length === 0) {
    fileList.value = val.map((item, index) => ({
      name: item.name || item.fileName || `文件${index + 1}`,
      url: item.url || item.filePath
    }))
  }
}, { immediate: true })

const beforeUpload = (file) => {
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      ElMessage.error(`文件大小不能超过 ${props.fileSize}MB`)
      return false
    }
  }
  return true
}

const handleSuccess = (response, file) => {
  const url = response.data?.url || response.data?.filePath || response.url
  const name = file.name
  const list = [...props.modelValue, { name, url, fileName: name, filePath: url }]
  emit('update:modelValue', list)
  emit('success', response, file)
}

const handleRemove = (file) => {
  const list = props.modelValue.filter(item => {
    const fileUrl = item.url || item.filePath
    return fileUrl !== file.url && fileUrl !== file.response?.data?.url
  })
  emit('update:modelValue', list)
  emit('remove', file)
}

const handlePreview = (file) => {
  window.open(file.url || file.response?.data?.url, '_blank')
}

const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 个文件`)
}

const handleError = (error) => {
  ElMessage.error('文件上传失败')
  console.error('Upload error:', error)
}

defineExpose({ uploadRef })
</script>
