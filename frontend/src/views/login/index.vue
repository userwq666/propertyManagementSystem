<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="48" class="logo-icon"><House /></el-icon>
          <span>物业管理系统</span>
        </div>
        <h2>欢迎登录</h2>
        <p>请输入您的账号和密码</p>
      </div>

      <el-form-item prop="username">
        <el-input
          v-model="loginForm.username"
          placeholder="用户名"
          :prefix-icon="User"
          clearable
          :autocomplete="'off'"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="密码"
          :prefix-icon="Lock"
          :show-password="true"
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          class="login-button"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form-item>

      <el-form-item>
        <div class="login-footer">
          <span>Copyright  2026 物业管理系统</span>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>


<script setup>
import { ref, reactive } from "vue"
import { useRouter, useRoute } from "vue-router"
import { ElMessage } from "element-plus"
import { User, Lock, House } from "@element-plus/icons-vue"
import { useUserStore } from "@/store/modules/user"
import { usePermissionStore } from "@/store/modules/permission"

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)

const loading = ref(false)
const loginForm = reactive({
  username: "",
  password: ""
})

const loginRules = {
  username: [{ required: true, message: "请输入用户名", trigger: "blur" }],
  password: [
    { required: true, message: "请输入密码", trigger: "blur" },
    { min: 6, message: "密码至少6位", trigger: "blur" }
  ]
}

function getDefaultPath(roles) {
  if (roles.includes("admin")) return "/system/user"
  if (roles.includes("property")) return "/repair/order"
  if (roles.includes("finance")) return "/fee/bill"
  if (roles.includes("owner")) return "/fee/arrears"
  return null
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  try {
    await loginFormRef.value.validate()
  } catch {
    return
  }
  loading.value = true
  try {
    const userStore = useUserStore()
    const permissionStore = usePermissionStore()
    await userStore.login(loginForm)
    await userStore.getInfo()

    permissionStore.resetRouter(); await permissionStore.generateRoutes([...userStore.roles])
    permissionStore.addRoutes.forEach(r => {
      if (!router.hasRoute(r.name)) {
        router.addRoute(r)
      }
    })

    const redirect = route.query.redirect || getDefaultPath(userStore.roles)
    router.push(redirect)
    ElMessage.success("登录成功")
  } catch (error) {
    ElMessage.error(error.message || "登录失败")
  } finally {
    loading.value = false
  }
}</script>

<style lang="scss" scoped>
.login-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 20px;
  box-sizing: border-box;
}

.login-form {
  width: 100%;
  max-width: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 40px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
}

.login-header {
  text-align: center;
  margin-bottom: 32px;

  .logo {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 10px;
    margin-bottom: 16px;

    .logo-icon {
      color: #409eff;
    }

    span {
      font-size: 24px;
      font-weight: 600;
      color: #303133;
    }
  }

  h2 {
    margin: 0 0 8px;
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }

  p {
    margin: 0;
    font-size: 14px;
    color: #909399;
  }
}

:deep(.el-input__inner) {
  height: 44px;
  font-size: 14px;
  border-radius: 6px;
}

:deep(.el-input__prefix) {
  color: #909399;
  font-size: 16px;
}

.login-button {
  width: 100%;
  height: 44px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 6px;
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  padding-top: 20px;
  border-top: 1px solid #ebeef5;

  span {
    font-size: 12px;
    color: #909399;
  }
}
</style>





