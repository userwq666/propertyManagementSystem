import request from '@/utils/request'

export function addFeeItem(data) {
  return request({
    url: '/api/fee/item',
    method: 'post',
    data
  })
}

export function updateFeeItem(data) {
  return request({
    url: '/api/fee/item',
    method: 'put',
    data
  })
}

export function deleteFeeItem(id) {
  return request({
    url: `/api/fee/item/${id}`,
    method: 'delete'
  })
}

export function getFeeItem(id) {
  return request({
    url: `/api/fee/item/${id}`,
    method: 'get'
  })
}

export function listFeeItem() {
  return request({
    url: '/api/fee/item/list',
    method: 'get'
  })
}

export function pageFeeItem(params) {
  return request({
    url: '/api/fee/item/page',
    method: 'get',
    params
  })
}

export function updateFeeItemStatus(id, status) {
  return request({
    url: '/api/fee/item/status',
    method: 'put',
    params: { id, status }
  })
}