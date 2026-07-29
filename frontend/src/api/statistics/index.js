import request from '@/utils/request'

export function getOverview() {
  return request({ url: '/statistics/overview', method: 'get' })
}

export function getMonthlyFee(year) {
  return request({ url: '/statistics/fee/monthly', method: 'get', params: { year } })
}

export function getFeeByItem() {
  return request({ url: '/statistics/fee/byItem', method: 'get' })
}

export function getFeeTrend(params) {
  return request({ url: '/statistics/fee/trend', method: 'get', params })
}

export function getRepairOverview() {
  return request({ url: '/statistics/repair/overview', method: 'get' })
}

export function getRepairByType() {
  return request({ url: '/statistics/repair/byType', method: 'get' })
}

export function getRepairTrend(params) {
  return request({ url: '/statistics/repair/trend', method: 'get', params })
}

export function getRepairTypeRatio(params) {
  return request({ url: '/statistics/repair/typeRatio', method: 'get', params })
}

export function getEquipmentStatus(params) {
  return request({ url: '/statistics/equipment/status', method: 'get', params })
}

export function getMaintenanceWarning(params) {
  return request({ url: '/statistics/equipment/maintenanceWarning', method: 'get', params })
}

export function getSatisfactionTrend(params) {
  return request({ url: '/statistics/complaint/satisfactionTrend', method: 'get', params })
}

export function getComplaintTypeRatio(params) {
  return request({ url: '/statistics/complaint/typeRatio', method: 'get', params })
}

export function getInspectionCompletion(params) {
  return request({ url: '/statistics/inspection/completion', method: 'get', params })
}

export function getInspectionAbnormal(params) {
  return request({ url: '/statistics/inspection/abnormal', method: 'get', params })
}
