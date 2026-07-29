import request from '@/utils/request'

export function getOperLogPage(params) {
  return request({ url: '/system/operLog/page', method: 'get', params })
}

export function getOperLogById(id) {
  return request({ url: `/system/operLog/${id}`, method: 'get' })
}

export function deleteOperLog(id) {
  return request({ url: `/system/operLog/${id}`, method: 'delete' })
}

export function clearOperLog() {
  return request({ url: '/system/operLog/clear', method: 'delete' })
}
