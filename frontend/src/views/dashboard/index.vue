<template>
  <div class="dashboard-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">用户总数</div>
              <div class="card-value">{{ stats.userCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #409eff"><User /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">角色总数</div>
              <div class="card-value">{{ stats.roleCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #67c23a"><UserFilled /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">菜单总数</div>
              <div class="card-value">{{ stats.menuCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #e6a23c"><Menu /></el-icon>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card shadow="hover">
          <div class="card-content">
            <div class="card-info">
              <div class="card-title">今日操作</div>
              <div class="card-value">{{ stats.todayOperCount }}</div>
            </div>
            <el-icon class="card-icon" style="color: #f56c6c"><Document /></el-icon>
          </div>
        </el-card>
      </el-col>
    </el-row>
    
    <el-card class="welcome-card">
      <div class="welcome-content">
        <h2>欢迎使用物业管理系统</h2>
        <p>这是一个基于SpringBoot+Vue3+MyBatisPlus+MySQL的物业管理系统</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const stats = ref({
  userCount: 0,
  roleCount: 0,
  menuCount: 0,
  todayOperCount: 0
})

onMounted(() => {
  stats.value = {
    userCount: 1,
    roleCount: 3,
    menuCount: 5,
    todayOperCount: 0
  }
})
</script>

<style scoped>
.dashboard-container {
  padding: 0;
}

.card-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.card-title {
  font-size: 14px;
  color: #909399;
  margin-bottom: 10px;
}

.card-value {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
}

.card-icon {
  font-size: 48px;
}

.welcome-card {
  margin-top: 20px;
}

.welcome-content {
  text-align: center;
  padding: 40px 0;
}

.welcome-content h2 {
  font-size: 24px;
  color: #303133;
  margin-bottom: 10px;
}

.welcome-content p {
  font-size: 14px;
  color: #909399;
}
</style>