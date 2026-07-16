<template>
  <div class="image-preview-container">
    <div class="preview-list">
      <div
        v-for="(url, index) in images"
        :key="index"
        class="preview-item"
        @click="handlePreview(index)"
      >
        <el-image
          :src="url"
          :preview-src-list="images"
          :initial-index="index"
          fit="cover"
          class="preview-image"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  images: { type: Array, default: () => [] }
})

const handlePreview = (index) => {
  // el-image 内置预览功能
}
</script>

<style lang="scss" scoped>
.preview-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.preview-item {
  width: 80px;
  height: 80px;
  border-radius: 4px;
  overflow: hidden;
  cursor: pointer;
  border: 1px solid #dcdfe6;

  &:hover {
    border-color: #409eff;
  }
}

.preview-image {
  width: 100%;
  height: 100%;
}
</style>
