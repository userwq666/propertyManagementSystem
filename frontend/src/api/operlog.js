import request from '../utils/request'

export function getOperLogPage(params) {
  return request({
    url: '/operlog/page',
    method: 'get',
    params
  })
}

export function cleanOperLog() {
  return request({
    url: '/operlog/clean',
    method: 'delete'
  })
}