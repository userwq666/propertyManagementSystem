import { ElMessage, ElMessageBox, ElNotification, ElLoading } from 'element-plus'

export default {
  install(app) {
    app.config.globalProperties.$message = ElMessage
    app.config.globalProperties.$confirm = ElMessageBox.confirm
    app.config.globalProperties.$alert = ElMessageBox.alert
    app.config.globalProperties.$prompt = ElMessageBox.prompt
    app.config.globalProperties.$notify = ElNotification
    app.config.globalProperties.$loading = ElLoading.service
  }
}