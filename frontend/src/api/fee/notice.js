import request from '@/utils/request'

export function addFeeNotice(data) {
  return request({ url: '/fee/notice', method: 'post', data })
}

export function updateFeeNotice(data) {
  return request({ url: '/fee/notice', method: 'put', data })
}

export function deleteFeeNotice(id) {
  return request({ url: `/fee/notice/${id}`, method: 'delete' })
}

export function getFeeNoticeById(id) {
  return request({ url: `/fee/notice/${id}`, method: 'get' })
}

export function getFeeNoticePage(params) {
  return request({ url: '/fee/notice/page', method: 'get', params })
}

export function publishFeeNotice(id) {
  return request({ url: `/fee/notice/publish/${id}`, method: 'put' })
}
