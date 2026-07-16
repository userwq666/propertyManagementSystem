<template>
  <div class="image-upload-container">
    <el-upload
      ref="uploadRef"
      :action="uploadUrl"
      :headers="headers"
      :file-list="fileList"
      :list-type="listType"
      :multiple="multiple"
      :limit="limit"
      :accept="acceptTypes"
      :on-exceed="handleExceed"
      :on-success="handleSuccess"
      :on-error="handleError"
      :on-remove="handleRemove"
      :on-preview="handlePreview"
      :before-upload="beforeUpload"
      :disabled="disabled"
      :class="{ hideUpload: !showUpload }"
    >
      <slot>
        <el-icon v-if="listType === 'picture-card'"><Plus /></el-icon>
        <el-button v-else type="primary" :disabled="disabled">
          <upload-filled /> 上传图片
        </el-button>
      </slot>
      <template #tip>
        <div class="el-upload__tip" v-if="tip">
          {{ tip }}
        </div>
      </template>
    </el-upload>

    <!-- 图片预览弹窗 -->
    <el-dialog v-model="previewVisible" title="图片预览" width="600px" append-to-body>
      <img :src="previewUrl" alt="预览" style="width: 100%" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { Plus, UploadFilled } from '@element-plus/icons-vue'
import { getToken } from '@/utils/auth'

const props = defineProps({
  modelValue: { type: Array, default: () => [] },
  action: { type: String, default: '/common/upload' },
  multiple: { type: Boolean, default: true },
  limit: { type: Number, default: 5 },
  tip: { type: String, default: '只能上传jpg/png文件，且不超过5MB' },
  disabled: { type: Boolean, default: false },
  fileSize: { type: Number, default: 5 },
  listType: { type: String, default: 'picture-card' }
})

const emit = defineEmits(['update:modelValue', 'success', 'remove'])

const uploadRef = ref(null)
const fileList = ref([])
const previewVisible = ref(false)
const previewUrl = ref('')

const acceptTypes = 'image/jpeg,image/png,image/gif,image/bmp,image/webp'

const uploadUrl = computed(() => props.action)
const headers = computed(() => ({ Authorization: `Bearer ${getToken()}` }))
const showUpload = computed(() => {
  if (!props.multiple) return fileList.value.length < 1
  return fileList.value.length < props.limit
})

watch(() => props.modelValue, (val) => {
  if (val && val.length > 0 && fileList.value.length === 0) {
    fileList.value = val.map((item, index) => ({
      name: item.name || `图片${index + 1}`,
      url: item.url || item.filePath
    }))
  }
}, { immediate: true })

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  if (!isImage) {
    ElMessage.error('只能上传图片文件')
    return false
  }
  if (props.fileSize) {
    const isLt = file.size / 1024 / 1024 < props.fileSize
    if (!isLt) {
      ElMessage.error(`图片大小不能超过 ${props.fileSize}MB`)
      return false
    }
  }
  return true
}

const handleSuccess = (response, file) => {
  const url = response.data?.url || response.data?.filePath || response.url
  const list = [...props.modelValue, { url, filePath: url, name: file.name }]
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
  previewUrl.value = file.url || file.response?.data?.url
  previewVisible.value = true
}

const handleExceed = () => {
  ElMessage.warning(`最多只能上传 ${props.limit} 张图片`)
}

const handleError = () => {
  ElMessage.error('图片上传失败')
}

defineExpose({ uploadRef })
</script>

<style lang="scss" scoped>
.hideUpload :deep(.el-upload--picture-card) {
  display: none;
}
</style>
