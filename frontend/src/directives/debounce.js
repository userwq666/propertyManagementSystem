export default {
  mounted(el, binding) {
    const { value, arg } = binding
    const delay = Number(arg) || 300
    let timer = null

    const handler = (...args) => {
      if (timer) clearTimeout(timer)
      timer = setTimeout(() => {
        value(...args)
      }, delay)
    }

    el._debounceHandler = handler
    el.addEventListener('click', handler)
  },
  unmounted(el) {
    if (el._debounceHandler) {
      el.removeEventListener('click', el._debounceHandler)
      delete el._debounceHandler
    }
  }
}