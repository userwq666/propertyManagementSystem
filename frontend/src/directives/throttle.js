export default {
  mounted(el, binding) {
    const { value, arg } = binding
    const delay = Number(arg) || 300
    let lastTime = 0

    const handler = (...args) => {
      const now = Date.now()
      if (now - lastTime >= delay) {
        lastTime = now
        value(...args)
      }
    }

    el._throttleHandler = handler
    el.addEventListener('click', handler)
  },
  unmounted(el) {
    if (el._throttleHandler) {
      el.removeEventListener('click', el._throttleHandler)
      delete el._throttleHandler
    }
  }
}