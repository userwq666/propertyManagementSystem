import request from '@/utils/request'

// 报修工单相关
export function getRepairOrderList(params) {
  return request({
    url: '/repair/order/list',
    method: 'get',
    params
  })
}

export function getRepairOrderInfo(orderId) {
  return request({
    url: `/repair/order/${orderId}`,
    method: 'get'
  })
}

export function addRepairOrder(data) {
  return request({
    url: '/repair/order',
    method: 'post',
    data
  })
}

export function updateRepairOrder(data) {
  return request({
    url: '/repair/order',
    method: 'put',
    data
  })
}

export function deleteRepairOrder(orderIds) {
  return request({
    url: `/repair/order/${orderIds}`,
    method: 'delete'
  })
}

export function cancelRepairOrder(orderId, reason) {
  return request({
    url: `/repair/order/${orderId}/cancel`,
    method: 'put',
    data: { reason }
  })
}

// 派单
export function dispatchRepairOrder(data) {
  return request({
    url: '/repair/order/dispatch',
    method: 'put',
    data
  })
}

// 处理进度更新
export function processRepairOrder(data) {
  return request({
    url: '/repair/order/process',
    method: 'put',
    data
  })
}

// 完工确认
export function finishRepairOrder(data) {
  return request({
    url: '/repair/order/finish',
    method: 'put',
    data
  })
}

// 导出报修工单
export function exportRepairOrder(params) {
  return request({
    url: '/repair/order/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取报修统计
export function getRepairStatistics() {
  return request({
    url: '/repair/statistics',
    method: 'get'
  })
}

// 获取房屋树
export function getHouseTree(params) {
  return request({
    url: '/community/house/tree',
    method: 'get',
    params
  })
}

// 获取维修人员列表
export function getRepairWorkerList(params) {
  return request({
    url: '/repair/worker/list',
    method: 'get',
    params
  })
}

// 图片上传
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

// 批量删除图片
export function deleteImages(imageUrls) {
  return request({
    url: '/common/upload/batchDelete',
    method: 'delete',
    data: { urls: imageUrls }
  })
}

// 派单管理相关
export function getRepairDispatchList(params) {
  return request({
    url: '/repair/dispatch/list',
    method: 'get',
    params
  })
}

export function getRepairDispatchInfo(dispatchId) {
  return request({
    url: `/repair/dispatch/${dispatchId}`,
    method: 'get'
  })
}

export function addRepairDispatch(data) {
  return request({
    url: '/repair/dispatch',
    method: 'post',
    data
  })
}

export function updateRepairDispatch(data) {
  return request({
    url: '/repair/dispatch',
    method: 'put',
    data
  })
}

export function deleteRepairDispatch(dispatchIds) {
  return request({
    url: `/repair/dispatch/${dispatchIds}`,
    method: 'delete'
  })
}

export function assignWorker(dispatchId, workerId) {
  return request({
    url: `/repair/dispatch/${dispatchId}/assign`,
    method: 'put',
    data: { workerId }
  })
}

export function getWorkerList(params) {
  return request({
    url: '/repair/worker/list',
    method: 'get',
    params
  })
}

export function getWorkerInfo(workerId) {
  return request({
    url: `/repair/worker/${workerId}`,
    method: 'get'
  })
}

export function addWorker(data) {
  return request({
    url: '/repair/worker',
    method: 'post',
    data
  })
}

export function updateWorker(data) {
  return request({
    url: '/repair/worker',
    method: 'put',
    data
  })
}

export function deleteWorker(workerIds) {
  return request({
    url: `/repair/worker/${workerIds}`,
    method: 'delete'
  })
}

export function getEvaluateList(params) {
  return request({
    url: '/repair/evaluate/list',
    method: 'get',
    params
  })
}

export function getEvaluateInfo(evaluateId) {
  return request({
    url: `/repair/evaluate/${evaluateId}`,
    method: 'get'
  })
}

export function replyEvaluate(evaluateId, reply) {
  return request({
    url: `/repair/evaluate/${evaluateId}/reply`,
    method: 'put',
    data: { reply }
  })
}