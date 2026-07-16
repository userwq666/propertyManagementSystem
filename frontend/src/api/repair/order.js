import request from '@/utils/request'

/**
 * 获取报修工单列表
 */
export function getRepairOrderList(params) {
  return request({
    url: '/repair/record/page',
    method: 'get',
    params
  })
}

/**
 * 获取报修工单详情
 */
export function getRepairOrderInfo(orderId) {
  return request({
    url: `/repair/record/${recordId}`,
    method: 'get'
  })
}

/**
 * 新增报修工单
 */
export function addRepairOrder(data) {
  return request({
    url: '/repair/record',
    method: 'post',
    data
  })
}

/**
 * 修改报修工单
 */
export function updateRepairOrder(data) {
  return request({
    url: '/repair/record',
    method: 'put',
    data
  })
}

/**
 * 删除报修工单
 */
export function deleteRepairOrder(id) {
  return request({
    url: `/repair/record/${recordId}`,
    method: 'delete'
  })
}

/**
 * 更新报修工单状态
 */
export function updateRepairOrderStatus(data) {
  return request({
    url: '/repair/record/status',
    method: 'put',
    data
  })
}

/**
 * 获取报修统计
 */
export function getRepairStatistics() {
  return request({
    url: '/repair/statistics',
    method: 'get'
  })
}

/**
 * 获取维修人员列表
 */
export function getRepairWorkerList(params) {
  return request({
    url: '/repair/worker/page',
    method: 'get',
    params
  })
}
