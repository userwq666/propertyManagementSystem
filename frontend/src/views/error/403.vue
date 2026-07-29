<template>
  <div class="error-page">
    <div class="error-code">403</div>
    <div class="error-desc">抱歉，您没有权限访问此页面</div>
    <div class="error-sub">您的账号角色未被系统识别，请联系管理员</div>
    <el-button type="primary" @click="$router.push('/login')">返回登录</el-button>
  </div>
</template>

<style lang="scss" scoped>
.error-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  min-height: 400px;
}
.error-code {
  font-size: 72px;
  font-weight: 700;
  color: #e6a23c;
  margin-bottom: 16px;
}
.error-desc {
  font-size: 18px;
  color: #606266;
  margin-bottom: 8px;
}
.error-sub {
  font-size: 14px;
  color: #909399;
  margin-bottom: 24px;
}
</style>
