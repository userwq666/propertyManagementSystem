export default {
  mounted(el, binding) {
    const { value, arg } = binding
    const delay = Number(arg) || 0

    const focus = () => {
      if (el.tagName === 'INPUT' || el.tagName === 'TEXTAREA' || el.isContentEditable) {
        el.focus()
      } else {
        const input = el.querySelector('input, textarea, [contenteditable]')
        if (input) input.focus()
      }
    }

    if (value === 'focus' || value === true) {
      if (delay > 0) {
        setTimeout(focus, delay)
      } else {
        focus()
      }
    }
  },
  updated(el, binding) {
    if (binding.value && !binding.oldValue) {
      el.focus()
    }
  }
}