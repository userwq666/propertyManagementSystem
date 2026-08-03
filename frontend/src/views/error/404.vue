<template>
  <div class="error-page">
    <div class="error-panel">
      <div class="error-code">404</div>
      <h1>页面不存在</h1>
      <p>访问的地址可能已被移动或删除</p>
      <el-button type="primary" size="large" @click="$router.push('/')">返回首页</el-button>
    </div>
  </div>
</template>

<style scoped>
.error-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 24px;
  background-color: #eef6f3;
  background-image:
    linear-gradient(rgba(15, 118, 110, 0.06) 1px, transparent 1px),
    linear-gradient(90deg, rgba(15, 118, 110, 0.06) 1px, transparent 1px);
  background-size: 32px 32px;
}

.error-panel {
  width: min(440px, 100%);
  padding: 48px 40px;
  text-align: center;
  background: #fff;
  border: 1px solid var(--pms-border);
  border-radius: 8px;
  box-shadow: 0 24px 64px rgba(15, 45, 42, 0.12);
}

.error-code {
  color: var(--pms-primary);
  font-size: 72px;
  font-weight: 800;
  line-height: 1;
}

h1 {
  margin-top: 20px;
  color: var(--pms-text);
  font-size: 22px;
}

p {
  margin: 10px 0 28px;
  color: var(--pms-text-muted);
  font-size: 14px;
}
</style>
