import request from '@/utils/request'

/**
 * 获取报修工单列表
 */
export function getRepairOrderList(params) {
  return request({
    url: '/repair/order/list',
    method: 'get',
    params
  })
}

/**
 * 获取报修工单详情
 */
export function getRepairOrderInfo(orderId) {
  return request({
    url: `/repair/order/${orderId}`,
    method: 'get'
  })
}

/**
 * 新增报修工单
 */
export function addRepairOrder(data) {
  return request({
    url: '/repair/order',
    method: 'post',
    data
  })
}

/**
 * 修改报修工单
 */
export function updateRepairOrder(data) {
  return request({
    url: '/repair/order',
    method: 'put',
    data
  })
}

/**
 * 删除报修工单
 */
export function deleteRepairOrder(orderIds) {
  return request({
    url: `/repair/order/${orderIds}`,
    method: 'delete'
  })
}

/**
 * 取消报修工单
 */
export function cancelRepairOrder(orderId, reason) {
  return request({
    url: `/repair/order/${orderId}/cancel`,
    method: 'put',
    data: { reason }
  })
}

/**
 * 派单
 */
export function dispatchRepairOrder(data) {
  return request({
    url: '/repair/order/dispatch',
    method: 'put',
    data
  })
}

/**
 * 处理进度更新
 */
export function processRepairOrder(data) {
  return request({
    url: '/repair/order/process',
    method: 'put',
    data
  })
}

/**
 * 完工确认
 */
export function finishRepairOrder(data) {
  return request({
    url: '/repair/order/finish',
    method: 'put',
    data
  })
}

/**
 * 评价回复
 */
export function replyEvaluate(evaluateId, reply) {
  return request({
    url: `/repair/evaluate/${evaluateId}/reply`,
    method: 'put',
    data: { reply }
  })
}

/**
 * 导出报修工单
 */
export function exportRepairOrder(params) {
  return request({
    url: '/repair/order/export',
    method: 'get',
    params,
    responseType: 'blob'
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
 * 获取房屋树
 */
export function getHouseTree(params) {
  return request({
    url: '/community/house/tree',
    method: 'get',
    params
  })
}

/**
 * 获取维修人员列表
 */
export function getRepairWorkerList(params) {
  return request({
    url: '/repair/worker/list',
    method: 'get',
    params
  })
}

/**
 * 图片上传
 */
export function uploadImage(data) {
  return request({
    url: '/common/upload',
    method: 'post',
    data,
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 批量删除图片
 */
export function deleteImages(imageUrls) {
  return request({
    url: '/common/upload/batchDelete',
    method: 'delete',
    data: { urls: imageUrls }
  })
}