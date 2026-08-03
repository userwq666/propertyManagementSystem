<template>
  <div class="login-page">
    <div class="login-shell">
      <section class="login-visual">
        <div class="visual-top">
          <div class="brand">
            <div class="brand-mark">物</div>
            <div class="brand-copy">
              <strong>智慧物业</strong>
              <span>PROPERTY SERVICE</span>
            </div>
          </div>
          <div class="visual-badge">
            <el-icon><Connection /></el-icon>
            <span>数字化社区服务</span>
          </div>
        </div>

        <div class="visual-main">
          <p class="eyebrow">COMMUNITY OPERATIONS</p>
          <h1>物业运营<br />一站式管理平台</h1>
          <p class="visual-sub">报修、收费、巡检、设备和业主服务统一协同，让社区管理更高效。</p>
          <div class="visual-points">
            <div class="point">
              <el-icon><CircleCheckFilled /></el-icon>
              <span>报修与投诉闭环跟进</span>
            </div>
            <div class="point">
              <el-icon><Money /></el-icon>
              <span>收费与支出透明可查</span>
            </div>
            <div class="point">
              <el-icon><Monitor /></el-icon>
              <span>设备巡检全流程留痕</span>
            </div>
          </div>
        </div>

        <div class="visual-bottom">
          <div class="visual-date">{{ dateText }}</div>
          <div class="visual-line"></div>
          <div class="visual-meta">智慧物业管理系统</div>
        </div>
      </section>

      <section class="login-panel">
        <div class="login-card">
          <div class="card-heading">
            <h2>登录系统</h2>
            <p>使用物业系统账号继续</p>
          </div>
          <el-form ref="loginFormRef" :model="loginForm" :rules="rules" label-width="0" size="large">
            <el-form-item prop="username">
              <el-input v-model="loginForm.username" placeholder="请输入用户名" prefix-icon="User" />
            </el-form-item>
            <el-form-item prop="password">
              <el-input
                v-model="loginForm.password"
                type="password"
                placeholder="请输入密码"
                prefix-icon="Lock"
                show-password
                @keyup.enter="handleLogin"
              />
            </el-form-item>
            <el-form-item class="submit-item">
              <el-button type="primary" :loading="loading" class="login-btn" @click="handleLogin">
                <span>登 录</span>
                <el-icon v-if="!loading" class="login-btn-icon"><Right /></el-icon>
              </el-button>
            </el-form-item>
          </el-form>
          <div class="login-tip">
            <el-icon><Lock /></el-icon>
            <span>账号信息由物业管理员分配</span>
          </div>
        </div>
      </section>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '@/stores/user'
import { ElMessage } from 'element-plus'

const router = useRouter()
const userStore = useUserStore()

const loginFormRef = ref(null)
const loading = ref(false)
const loginForm = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

const dateText = computed(() => new Date().toLocaleDateString('zh-CN', {
  year: 'numeric',
  month: 'long',
  day: 'numeric'
}))

const handleLogin = async () => {
  const valid = await loginFormRef.value.validate().catch(() => false)
  if (!valid) return
  loading.value = true
  try {
    await userStore.login(loginForm)
    ElMessage.success('登录成功')
    router.push('/dashboard')
  } catch (error) {
    // 错误已在 request 拦截器中处理
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.login-page {
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

.login-shell {
  width: min(1120px, 100%);
  min-height: 640px;
  display: grid;
  grid-template-columns: 1.05fr 0.95fr;
  overflow: hidden;
  background: #fff;
  border: 1px solid var(--pms-border);
  border-radius: 8px;
  box-shadow: 0 24px 64px rgba(15, 45, 42, 0.14);
}

.login-visual {
  position: relative;
  display: flex;
  flex-direction: column;
  padding: 44px 48px;
  overflow: hidden;
  color: #fff;
  background: #0e3f3d;
}

.login-visual::after {
  content: '';
  position: absolute;
  right: -110px;
  bottom: -110px;
  width: 340px;
  height: 340px;
  border: 1px solid rgba(110, 231, 183, 0.22);
  border-radius: 8px;
  transform: rotate(45deg);
}

.visual-top {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
}

.brand {
  display: flex;
  align-items: center;
  gap: 12px;
}

.brand-mark {
  width: 42px;
  height: 42px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #6ee7b7;
  color: #0b3b3c;
  font-size: 20px;
  font-weight: 800;
}

.brand-copy {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-copy strong {
  font-size: 17px;
}

.brand-copy span {
  margin-top: 4px;
  color: rgba(255, 255, 255, 0.5);
  font-size: 10px;
  letter-spacing: 0.1em;
}

.visual-badge {
  display: flex;
  align-items: center;
  gap: 7px;
  padding: 7px 12px;
  border: 1px solid rgba(255, 255, 255, 0.14);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.07);
  color: rgba(255, 255, 255, 0.78);
  font-size: 12px;
}

.visual-main {
  position: relative;
  z-index: 1;
  margin: auto 0;
  padding: 40px 0;
}

.eyebrow {
  margin-bottom: 18px;
  color: #6ee7b7;
  font-size: 12px;
  font-weight: 600;
  letter-spacing: 0.18em;
}

.visual-main h1 {
  color: #fff;
  font-size: 40px;
  font-weight: 700;
  line-height: 1.2;
}

.visual-sub {
  max-width: 420px;
  margin-top: 18px;
  color: rgba(255, 255, 255, 0.68);
  font-size: 15px;
  line-height: 1.8;
}

.visual-points {
  display: flex;
  flex-direction: column;
  gap: 13px;
  margin-top: 32px;
}

.point {
  display: flex;
  align-items: center;
  gap: 10px;
  color: rgba(255, 255, 255, 0.88);
  font-size: 14px;
}

.point .el-icon {
  color: #6ee7b7;
  font-size: 17px;
}

.visual-bottom {
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: 14px;
  color: rgba(255, 255, 255, 0.52);
  font-size: 12px;
}

.visual-line {
  flex: 1;
  height: 1px;
  background: rgba(255, 255, 255, 0.12);
}

.visual-meta {
  white-space: nowrap;
}

.login-panel {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 48px;
  background: #fff;
}

.login-card {
  width: min(360px, 100%);
}

.card-heading {
  margin-bottom: 30px;
}

.card-heading h2 {
  color: var(--pms-text);
  font-size: 26px;
  font-weight: 700;
}

.card-heading p {
  margin-top: 8px;
  color: var(--pms-text-muted);
  font-size: 14px;
}

.submit-item {
  margin-top: 8px;
  margin-bottom: 0;
}

.login-btn {
  width: 100%;
  height: 46px;
  border-radius: 6px;
  font-size: 15px;
}

.login-btn-icon {
  margin-left: 8px;
}

.login-tip {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 7px;
  margin-top: 26px;
  color: var(--pms-text-muted);
  font-size: 12px;
}

@media (max-width: 900px) {
  .login-page {
    padding: 16px;
  }

  .login-shell {
    grid-template-columns: 1fr;
    min-height: 0;
    max-width: 460px;
  }

  .login-visual {
    display: none;
  }

  .login-panel {
    padding: 40px 28px;
  }
}
</style>
