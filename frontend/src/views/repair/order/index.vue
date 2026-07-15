<template>
  <div class="app-container">
    <div class="page-header">
      <h1>order</h1>
    </div>
    <el-card>
      <template #header>
        <span>order 管理</span>
      </template>
      <div class="content">
        <p>页面开发中...</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
</script>

<style lang="scss" scoped>
.app-container {
  padding: 20px;
}
.page-header {
  margin-bottom: 20px;
  h1 {
    font-size: 20px;
    font-weight: 600;
    color: #303133;
  }
}
.content {
  padding: 20px;
  text-align: center;
  color: #909399;
}
</style>
