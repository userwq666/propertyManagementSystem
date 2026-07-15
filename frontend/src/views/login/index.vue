<template>
  <div class="login-container">
    <el-form ref="loginFormRef" :model="loginForm" :rules="loginRules" class="login-form" @submit.native.prevent="handleLogin">
      <div class="login-header">
        <div class="logo">
          <el-icon :size="48" class="logo-icon"><house-filled /></el-icon>
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
          :autocomplete="false"
        />
      </el-form-item>

      <el-form-item prop="password">
        <el-input
          v-model="loginForm.password"
          type="password"
          placeholder="密码"
          :prefix-icon="Lock"
          :show-password="showPassword"
          @keyup.enter="handleLogin"
        />
      </el-form-item>

      <el-form-item prop="code" v-if="showCaptcha">
        <div class="captcha-input">
          <el-input
            v-model="loginForm.code"
            placeholder="验证码"
            :prefix-icon="Lock"
            style="width: 100px"
            maxlength="4"
          />
          <img :src="captchaUrl" alt="验证码" class="captcha-image" @click="refreshCaptcha" />
          <span class="refresh-captcha" @click="refreshCaptcha">看不清？换一张</span>
        </div>
      </el-form-item>

      <el-form-item>
        <div class="remember-forget">
          <el-checkbox v-model="loginForm.rememberMe">记住我</el-checkbox>
          <a href="javascript:void(0);" class="forget-password">忘记密码？</a>
        </div>
      </el-form-item>

      <el-form-item>
        <el-button
          :loading="loading"
          type="primary"
          native-type="submit"
          class="login-button"
          @click="handleLogin"
        >
          登 录
        </el-button>
      </el-form-item>

      <el-form-item>
        <div class="login-footer">
          <span>版本: 1.0.0</span>
          <span>Copyright © 2024 物业管理系统</span>
        </div>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock, HouseFilled } from '@element-plus/icons-vue'
import { login, getCaptcha } from '@/api/user'

const router = useRouter()
const route = useRoute()
const loginFormRef = ref(null)

const loading = ref(false)
const showPassword = ref(false)
const showCaptcha = ref(false)
const captchaUrl = ref('')
const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: '',
  rememberMe: false
})

const loginRules = reactive({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
})

const getCaptchaImage = async () => {
  try {
    const res = await getCaptcha()
    captchaUrl.value = res.data.img || `data:image/svg+xml;base64,${btoa('<svg xmlns="http://www.w3.org/2000/svg" width="100" height="40"><text x="20" y="25" font-size="20" fill="#666">验证码</text></svg>')}`
    loginForm.uuid = res.data.uuid || ''
    showCaptcha.value = true
  } catch (error) {
    showCaptcha.value = false
  }
}

const refreshCaptcha = () => {
  getCaptchaImage()
}

const handleLogin = async () => {
  if (!loginFormRef.value) return
  loginFormRef.value.validate(async valid => {
    if (!valid) return
    loading.value = true
    try {
      await login(loginForm)
      const redirect = route.query.redirect || '/'
      router.push(redirect)
      ElMessage.success('登录成功')
    } catch (error) {
      if (error.code === 401 || showCaptcha.value) {
        await getCaptchaImage()
      }
      ElMessage.error(error.message || '登录失败')
    } finally {
      loading.value = false
    }
  })
}

onMounted(() => {
  getCaptchaImage()
})
</script>

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

:deep(.el-form-item) {
  margin-bottom: 20px;
}

:deep(.el-input__inner) {
  height: 44px;
  font-size: 14px;
  border-radius: 6px;
  border: 1px solid #dcdfe6;

  &:hover {
    border-color: #c0c4cc;
  }

  &:focus {
    border-color: #409eff;
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
  }
}

:deep(.el-input__prefix) {
  color: #909399;
  font-size: 16px;
}

.captcha-input {
  display: flex;
  align-items: center;
  gap: 10px;

  .captcha-image {
    height: 44px;
    border: 1px solid #dcdfe6;
    border-radius: 6px;
    cursor: pointer;
    user-select: none;
  }

  .refresh-captcha {
    font-size: 12px;
    color: #409eff;
    cursor: pointer;
    white-space: nowrap;

    &:hover {
      text-decoration: underline;
    }
  }
}

.remember-forget {
  display: flex;
  justify-content: space-between;
  align-items: center;

  .forget-password {
    font-size: 13px;
    color: #409eff;
    text-decoration: none;

    &:hover {
      text-decoration: underline;
    }
  }
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
  display: flex;
  flex-direction: column;
  gap: 8px;

  span {
    font-size: 12px;
    color: #909399;
  }
}
</style>