import { onMounted, onUnmounted } from 'vue'

export function useRealtimeRefresh(loadFn) {
  let timer = null
  const handler = () => {
    clearTimeout(timer)
    timer = setTimeout(() => { if (loadFn) loadFn() }, 200)
  }
  onMounted(() => window.addEventListener('pms:data-changed', handler))
  onUnmounted(() => {
    clearTimeout(timer)
    window.removeEventListener('pms:data-changed', handler)
  })
}
