import request from '@/utils/request'

export function addRepairRecord(data) {
  return request({
    url: '/api/repair/record',
    method: 'post',
    data
  })
}

export function updateRepairRecord(data) {
  return request({
    url: '/api/repair/record',
    method: 'put',
    data
  })
}

export function deleteRepairRecord(id) {
  return request({
    url: `/api/repair/record/${id}`,
    method: 'delete'
  })
}

export function getRepairRecord(id) {
  return request({
    url: `/api/repair/record/${id}`,
    method: 'get'
  })
}

export function pageRepairRecord(params) {
  return request({
    url: '/api/repair/record/page',
    method: 'get',
    params
  })
}

export function updateRepairRecordStatus(params) {
  return request({
    url: '/api/repair/record/status',
    method: 'put',
    params
  })
}

export function updateRepairRecordRating(params) {
  return request({
    url: '/api/repair/record/rating',
    method: 'put',
    params
  })
}
