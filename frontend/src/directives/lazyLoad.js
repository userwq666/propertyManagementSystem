export default {
  mounted(el, binding) {
    const { value } = binding
    const src = value?.src || value
    const error = value?.error || ''
    const loading = value?.loading || ''

    el.style.opacity = 0
    el.style.transition = 'opacity 0.3s ease'

    const observer = new IntersectionObserver(
      entries => {
        entries.forEach(entry => {
          if (entry.isIntersecting) {
            const img = new Image()
            if (loading) {
              el.src = loading
            }

            img.onload = () => {
              el.src = src
              el.style.opacity = 1
            }

            img.onerror = () => {
              if (error) {
                el.src = error
              }
              el.style.opacity = 1
            }

            img.src = src
            observer.unobserve(el)
          }
        })
      },
      {
        rootMargin: '50px',
        threshold: 0.01
      }
    )

    observer.observe(el)
    el._lazyObserver = observer
  },
  unmounted(el) {
    if (el._lazyObserver) {
      el._lazyObserver.disconnect()
      delete el._lazyObserver
    }
  }
}