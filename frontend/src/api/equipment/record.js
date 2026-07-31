import request from '@/utils/request'

export function getEquipmentRecordOptions() {
  return request({ url: '/equipment/record/equipments', method: 'get' })
}

export function getEquipmentRecordSummary(equipmentId) {
  return request({ url: '/equipment/record/summary', method: 'get', params: { equipmentId } })
}
