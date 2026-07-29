import request from '@/utils/request'

export function addMaintenance(data) {
  return request({ url: '/equipment/maintenance', method: 'post', data })
}

export function updateMaintenance(data) {
  return request({ url: '/equipment/maintenance', method: 'put', data })
}

export function deleteMaintenance(id) {
  return request({ url: `/equipment/maintenance/${id}`, method: 'delete' })
}

export function getMaintenanceById(id) {
  return request({ url: `/equipment/maintenance/${id}`, method: 'get' })
}

export function getMaintenancePage(params) {
  return request({ url: '/equipment/maintenance/page', method: 'get', params })
}

export function startMaintenance(id) {
  return request({ url: `/equipment/maintenance/start/${id}`, method: 'put' })
}

export function completeMaintenance(id) {
  return request({ url: `/equipment/maintenance/complete/${id}`, method: 'put' })
}
