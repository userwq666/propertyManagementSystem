import request from '@/utils/request'

// 获取缴费记录列表
export function getFeeRecordList(params) {
  return request({
    url: '/fee/record/page',
    method: 'get',
    params
  })
}

// 获取缴费记录详情
export function getFeeRecordInfo(recordId) {
  return request({
    url: `/fee/record/${recordId}`,
    method: 'get'
  })
}

// 退费操作
export function refundFeeRecord(data) {
  return request({
    url: '/fee/record/refund',
    method: 'post',
    data
  })
}

// 导出缴费记录
export function exportFeeRecord(params) {
  return request({
    url: '/fee/record/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取缴费记录统计
export function getFeeRecordStatistics(params) {
  return request({
    url: '/fee/record/statistics',
    method: 'get',
    params
  })
}

// 获取缴费项目列表（用于下拉选择）
export function getChargeItemListForSelect(params) {
  return request({
    url: '/fee/item/page',
    method: 'get',
    params
  })
}
