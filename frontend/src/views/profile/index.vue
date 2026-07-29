<template>
  <div class="app-container">
    <div class="page-header">
      <h1>个人中心</h1>
    </div>

    <el-row :gutter="20">
      <el-col :xs="24" :sm="8">
        <el-card shadow="never">
          <div class="avatar-section">
            <el-avatar :size="80" :src="userStore.avatar">
              <el-icon :size="40"><UserFilled /></el-icon>
            </el-avatar>
            <h3>{{ userStore.name || "未设置" }}</h3>
            <el-tag v-for="role in userStore.roles" :key="role" size="small" class="role-tag">{{ role }}</el-tag>
          </div>
          <el-divider />
          <div class="info-list">
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ userStore.email || "--" }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ userStore.phone || "--" }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">部门</span>
              <span class="info-value">{{ userStore.deptId || "--" }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="16">
        <el-card shadow="never">
          <template #header><span>基本信息</span></template>
          <el-form ref="formRef" :model="form" :rules="rules" label-width="100px" style="max-width:500px">
            <el-form-item label="用户名">
              <el-input v-model="userStore.name" disabled />
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
              <el-input v-model="form.email" placeholder="请输入邮箱" />
            </el-form-item>
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="saving" @click="handleSave">保存修改</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card shadow="never" style="margin-top:16px">
          <template #header><span>修改密码</span></template>
          <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px" style="max-width:500px">
            <el-form-item label="当前密码" prop="oldPassword">
              <el-input v-model="pwdForm.oldPassword" type="password" show-password placeholder="请输入当前密码" />
            </el-form-item>
            <el-form-item label="新密码" prop="newPassword">
              <el-input v-model="pwdForm.newPassword" type="password" show-password placeholder="请输入新密码" />
            </el-form-item>
            <el-form-item label="确认密码" prop="confirmPassword">
              <el-input v-model="pwdForm.confirmPassword" type="password" show-password placeholder="请再次输入新密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" :loading="changingPwd" @click="handleChangePwd">修改密码</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive } from "vue"
import { ElMessage } from "element-plus"
import { UserFilled } from "@element-plus/icons-vue"
import { useUserStore } from "@/store/modules/user"
import { updateUserProfile, updateUserPassword } from "@/api/system/user"

const userStore = useUserStore()
const saving = ref(false)
const changingPwd = ref(false)
const formRef = ref(null)
const pwdFormRef = ref(null)

const form = reactive({
  email: userStore.email || "",
  phone: userStore.phone || ""
})

const rules = {
  email: [{ type: "email", message: "邮箱格式不正确", trigger: "blur" }],
  phone: [{ pattern: /^1[3-9]\d{9}$/, message: "手机号格式不正确", trigger: "blur" }]
}

const pwdForm = reactive({
  oldPassword: "",
  newPassword: "",
  confirmPassword: ""
})

const validateConfirmPwd = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error("两次密码不一致"))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: "请输入当前密码", trigger: "blur" }],
  newPassword: [
    { required: true, message: "请输入新密码", trigger: "blur" },
    { min: 6, message: "密码至少6位", trigger: "blur" }
  ],
  confirmPassword: [
    { required: true, message: "请确认新密码", trigger: "blur" },
    { validator: validateConfirmPwd, trigger: "blur" }
  ]
}

const handleSave = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    return
  }
  saving.value = true
  try {
    await updateUserProfile({ email: form.email, phone: form.phone })
    userStore.email = form.email
    userStore.phone = form.phone
    ElMessage.success("保存成功")
  } catch {
    ElMessage.error("保存失败")
  } finally {
    saving.value = false
  }
}

const handleChangePwd = async () => {
  if (!pwdFormRef.value) return
  try {
    await pwdFormRef.value.validate()
  } catch {
    return
  }
  changingPwd.value = true
  try {
    await updateUserPassword({
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success("密码修改成功，请重新登录")
    pwdForm.oldPassword = ""
    pwdForm.newPassword = ""
    pwdForm.confirmPassword = ""
    setTimeout(() => {
      userStore.logout()
    }, 1500)
  } catch {
    ElMessage.error("密码修改失败")
  } finally {
    changingPwd.value = false
  }
}
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
    margin: 0;
  }
}
.avatar-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 20px 0;
  h3 {
    margin: 12px 0 8px;
    font-size: 18px;
  }
  .role-tag {
    margin: 2px 4px;
  }
}
.info-list {
  .info-item {
    display: flex;
    justify-content: space-between;
    padding: 10px 0;
    border-bottom: 1px solid #ebeef5;
    &:last-child {
      border-bottom: none;
    }
    .info-label {
      color: #909399;
      font-size: 14px;
    }
    .info-value {
      color: #303133;
      font-size: 14px;
    }
  }
}
</style>