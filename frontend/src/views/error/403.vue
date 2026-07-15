<template>
  <div class="error-container">
    <div class="error-content">
      <el-icon class="error-icon"><lock /></el-icon>
      <h1 class="error-title">无权限访问</h1>
      <p class="error-desc">抱歉，您没有权限访问该页面。请联系管理员获取权限。</p>
      <div class="error-actions">
        <el-button type="primary" @click="goBack">返回上一页</el-button>
        <el-button @click="goHome">返回首页</el-button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { Lock } from '@element-plus/icons-vue'

const router = useRouter()

const goBack = () => router.back()
const goHome = () => router.push('/')
</script>

<style lang="scss" scoped>
.error-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f5f7;
}

.error-content {
  text-align: center;
  padding: 40px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  max-width: 400px;

  .error-icon {
    font-size: 64px;
    color: #f56c6c;
    margin-bottom: 16px;
  }

  .error-title {
    margin: 0 0 12px;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }

  .error-desc {
    margin: 0 0 28px;
    font-size: 14px;
    color: #909399;
    line-height: 1.6;
  }

  .error-actions {
    display: flex;
    gap: 12px;
    justify-content: center;
  }
}
</style>