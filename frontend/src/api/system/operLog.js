import request from '@/utils/request'

export function getOperLogPage(params) {
  return request({ url: '/system/operLog/page', method: 'get', params })
}

export function deleteOperLog(id) {
  return request({ url: '/system/operLog/' + id, method: 'delete' })
}

export function cleanOperLog(params) {
  return request({ url: '/system/operLog/clean', method: 'delete', params })
}