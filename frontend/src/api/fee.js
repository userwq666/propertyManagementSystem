import request from '@/utils/request'

export function getBillList(params) {
  return request({
    url: '/fee/bill/list',
    method: 'get',
    params
  })
}

export function getBillInfo(billId) {
  return request({
    url: `/fee/bill/${billId}`,
    method: 'get'
  })
}

export function addBill(data) {
  return request({
    url: '/fee/bill',
    method: 'post',
    data
  })
}

export function updateBill(data) {
  return request({
    url: '/fee/bill',
    method: 'put',
    data
  })
}

export function deleteBill(billIds) {
  return request({
    url: `/fee/bill/${billIds}`,
    method: 'delete'
  })
}

export function generateBill(data) {
  return request({
    url: '/fee/bill/generate',
    method: 'post',
    data
  })
}

export function sendBill(billIds) {
  return request({
    url: `/fee/bill/send`,
    method: 'post',
    data: { billIds }
  })
}

export function getPaymentList(params) {
  return request({
    url: '/fee/payment/list',
    method: 'get',
    params
  })
}

export function getPaymentInfo(paymentId) {
  return request({
    url: `/fee/payment/${paymentId}`,
    method: 'get'
  })
}

export function addPayment(data) {
  return request({
    url: '/fee/payment',
    method: 'post',
    data
  })
}

export function refundPayment(paymentId, reason) {
  return request({
    url: `/fee/payment/${paymentId}/refund`,
    method: 'post',
    data: { reason }
  })
}

export function getRefundList(params) {
  return request({
    url: '/fee/refund/list',
    method: 'get',
    params
  })
}

export function getRefundInfo(refundId) {
  return request({
    url: `/fee/refund/${refundId}`,
    method: 'get'
  })
}

export function addRefund(data) {
  return request({
    url: '/fee/refund',
    method: 'post',
    data
  })
}

export function approveRefund(refundId, status, remark) {
  return request({
    url: `/fee/refund/${refundId}/approve`,
    method: 'put',
    data: { status, remark }
  })
}

export function getChargeItemList(params) {
  return request({
    url: '/fee/item/list',
    method: 'get',
    params
  })
}

export function getChargeItemInfo(itemId) {
  return request({
    url: `/fee/item/${itemId}`,
    method: 'get'
  })
}

export function addChargeItem(data) {
  return request({
    url: '/fee/item',
    method: 'post',
    data
  })
}

export function updateChargeItem(data) {
  return request({
    url: '/fee/item',
    method: 'put',
    data
  })
}

export function deleteChargeItem(itemIds) {
  return request({
    url: `/fee/item/${itemIds}`,
    method: 'delete'
  })
}

export function getChargeStandardList(params) {
  return request({
    url: '/fee/standard/list',
    method: 'get',
    params
  })
}

export function getChargeStandardInfo(standardId) {
  return request({
    url: `/fee/standard/${standardId}`,
    method: 'get'
  })
}

export function addChargeStandard(data) {
  return request({
    url: '/fee/standard',
    method: 'post',
    data
  })
}

export function updateChargeStandard(data) {
  return request({
    url: '/fee/standard',
    method: 'put',
    data
  })
}

export function deleteChargeStandard(standardIds) {
  return request({
    url: `/fee/standard/${standardIds}`,
    method: 'delete'
  })
}

export function getArrearsList(params) {
  return request({
    url: '/fee/arrears/list',
    method: 'get',
    params
  })
}

export function getArrearsStatistics() {
  return request({
    url: '/fee/arrears/statistics',
    method: 'get'
  })
}

export function sendArrearsNotice(arrearsIds) {
  return request({
    url: '/fee/arrears/sendNotice',
    method: 'post',
    data: { arrearsIds }
  })
}