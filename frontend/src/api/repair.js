import request from '@/utils/request'

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

export function getRepairStatistics() {
  return request({
    url: '/repair/statistics',
    method: 'get'
  })
}