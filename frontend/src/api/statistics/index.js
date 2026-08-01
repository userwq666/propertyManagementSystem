import request from '@/utils/request'

export function getStatisticsOverview() {
  return request({ url: '/statistics/overview', method: 'get' })
}

export function getRepairSummary() {
  return request({ url: '/statistics/repair/summary', method: 'get' })
}

export function getEquipmentSummary() {
  return request({ url: '/statistics/equipment/summary', method: 'get' })
}

export function getUserSummary() {
  return request({ url: '/statistics/user/summary', method: 'get' })
}

export function getFeeSummary() {
  return request({ url: '/statistics/fee/summary', method: 'get' })
}

export function getComplaintSummary() {
  return request({ url: '/statistics/complaint/summary', method: 'get' })
}

export function getInspectionSummary() {
  return request({ url: '/statistics/inspection/summary', method: 'get' })
}
