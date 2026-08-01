import request from '@/utils/request'

export function addFeeItem(data) {
  return request({ url: '/fee/item', method: 'post', data })
}

export function updateFeeItem(data) {
  return request({ url: '/fee/item', method: 'put', data })
}

export function deleteFeeItem(id) {
  return request({ url: `/fee/item/${id}`, method: 'delete' })
}

export function getFeeItemById(id) {
  return request({ url: `/fee/item/${id}`, method: 'get' })
}

export function getFeeItemPage(params) {
  return request({ url: '/fee/item/page', method: 'get', params })
}

export function updateFeeItemStatus(params) {
  return request({ url: '/fee/item/status', method: 'put', params })
}

export function publishFeeItem(id) {
  return request({ url: `/fee/item/publish/${id}`, method: 'put' })
}
