import request from '@/utils/request'

export function addInspectionPlan(data) {
  return request({
    url: '/api/inspection/plan',
    method: 'post',
    data
  })
}

export function updateInspectionPlan(data) {
  return request({
    url: '/api/inspection/plan',
    method: 'put',
    data
  })
}

export function deleteInspectionPlan(id) {
  return request({
    url: `/api/inspection/plan/${id}`,
    method: 'delete'
  })
}

export function getInspectionPlan(id) {
  return request({
    url: `/api/inspection/plan/${id}`,
    method: 'get'
  })
}

export function pageInspectionPlan(params) {
  return request({
    url: '/api/inspection/plan/page',
    method: 'get',
    params
  })
}

export function updateInspectionPlanStatus(params) {
  return request({
    url: '/api/inspection/plan/status',
    method: 'put',
    params
  })
}

export function generateInspectionPlan() {
  return request({
    url: '/api/inspection/plan/generate',
    method: 'post'
  })
}
