import request from '@/utils/request'

export function addPlan(data) {
  return request({ url: '/inspection/plan', method: 'post', data })
}

export function updatePlan(data) {
  return request({ url: '/inspection/plan', method: 'put', data })
}

export function deletePlan(id) {
  return request({ url: `/inspection/plan/${id}`, method: 'delete' })
}

export function getPlanById(id) {
  return request({ url: `/inspection/plan/${id}`, method: 'get' })
}

export function getPlanPage(params, config = {}) {
  return request({ url: '/inspection/plan/page', method: 'get', params, ...config })
}

export function updatePlanStatus(params) {
  return request({ url: '/inspection/plan/status', method: 'put', params })
}

export function generateRecords(params) {
  return request({ url: '/inspection/plan/generate', method: 'post', params })
}

export function getInspectors(config = {}) {
  return request({ url: '/inspection/plan/inspectors', method: 'get', ...config })
}
