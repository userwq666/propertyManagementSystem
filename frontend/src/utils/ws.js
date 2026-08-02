import { getToken } from '@/utils/auth'

let socket = null
let reconnectTimer = null
const handlers = []

export function connectWebSocket(onMessage) {
  const token = getToken()
  if (!token) return
  if (socket && (socket.readyState === WebSocket.OPEN || socket.readyState === WebSocket.CONNECTING)) {
    if (onMessage) handlers.push(onMessage)
    return
  }
  if (onMessage) handlers.push(onMessage)
  const proto = window.location.protocol === 'https:' ? 'wss' : 'ws'
  try {
    socket = new WebSocket(`${proto}://${window.location.host}/ws?token=${encodeURIComponent(token)}`)
  } catch (e) {
    return
  }
  socket.onopen = () => { /* 连接成功 */ }
  socket.onmessage = (event) => {
    try {
      const msg = JSON.parse(event.data)
      handlers.forEach(h => h(msg))
    } catch (e) { /* ignore */ }
  }
  socket.onclose = () => { scheduleReconnect() }
  socket.onerror = () => { try { socket.close() } catch (e) { /* ignore */ } }
}

export function disconnectWebSocket() {
  clearTimeout(reconnectTimer)
  handlers.length = 0
  if (socket) {
    try { socket.close() } catch (e) { /* ignore */ }
    socket = null
  }
}

function scheduleReconnect() {
  clearTimeout(reconnectTimer)
  reconnectTimer = setTimeout(() => {
    socket = null
    connectWebSocket()
  }, 5000)
}
