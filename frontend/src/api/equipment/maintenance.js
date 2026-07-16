import request from '@/utils/request'

export function getMaintenancePage(params) {
  return request({
    url: '/equipment/maintenance/page',
    method: 'get',
    params
  })
}

export function getMaintenanceInfo(id) {
  return request({
    url: \/equipment/maintenance/\\,
    method: 'get'
  })
}

export function addMaintenance(data) {
  return request({
    url: '/equipment/maintenance',
    method: 'post',
    data
  })
}

export function updateMaintenance(data) {
  return request({
    url: '/equipment/maintenance',
    method: 'put',
    data
  })
}

export function deleteMaintenance(id) {
  return request({
    url: \/equipment/maintenance/\\,
    method: 'delete'
  })
}

export function startMaintenance(id) {
  return request({
    url: \/equipment/maintenance/start/\\,
    method: 'put'
  })
}

export function completeMaintenance(id) {
  return request({
    url: \/equipment/maintenance/complete/\\,
    method: 'put'
  })
}
