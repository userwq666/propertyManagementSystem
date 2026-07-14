import request from '@/utils/request'

export function addInspectionRecord(data) {
  return request({
    url: '/api/inspection/record',
    method: 'post',
    data
  })
}

export function updateInspectionRecord(data) {
  return request({
    url: '/api/inspection/record',
    method: 'put',
    data
  })
}

export function deleteInspectionRecord(id) {
  return request({
    url: `/api/inspection/record/${id}`,
    method: 'delete'
  })
}

export function getInspectionRecord(id) {
  return request({
    url: `/api/inspection/record/${id}`,
    method: 'get'
  })
}

export function pageInspectionRecord(params) {
  return request({
    url: '/api/inspection/record/page',
    method: 'get',
    params
  })
}

export function getInspectionRecordByEquipment(id) {
  return request({
    url: `/api/inspection/record/equipment/${id}`,
    method: 'get'
  })
}
