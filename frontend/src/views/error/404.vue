<template>
  <div class="error-container">
    <h1>404</h1>
    <p>页面不存在</p>
    <el-button type="primary" @click="$router.push('/')">返回首页</el-button>
  </div>
</template>

<style scoped>
.error-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  color: #606266;
}
.error-container h1 {
  font-size: 80px;
  color: #409eff;
  margin-bottom: 10px;
}
.error-container p {
  font-size: 18px;
  margin-bottom: 20px;
}
</style>
