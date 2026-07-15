import { ElMessage } from 'element-plus'

export default {
  mounted(el, binding) {
    const { value } = binding
    const text = typeof value === 'string' ? value : value?.text
    const successMessage = value?.success || '复制成功'
    const errorMessage = value?.error || '复制失败'

    el._copyHandler = async () => {
      try {
        await navigator.clipboard.writeText(text)
        ElMessage.success(successMessage)
      } catch (err) {
        ElMessage.error(errorMessage)
      }
    }

    el.style.cursor = 'pointer'
    el.addEventListener('click', el._copyHandler)
  },
  updated(el, binding) {
    if (binding.value !== binding.oldValue) {
      const { value } = binding
      const text = typeof value === 'string' ? value : value?.text
      el._copyText = text
    }
  },
  unmounted(el) {
    if (el._copyHandler) {
      el.removeEventListener('click', el._copyHandler)
      delete el._copyHandler
    }
  }
}