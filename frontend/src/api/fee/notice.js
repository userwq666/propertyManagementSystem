import request from '@/utils/request'

// 获取缴费通知列表
export function getNoticeList(params) {
  return request({
    url: '/fee/notice/list',
    method: 'get',
    params
  })
}

// 获取缴费通知详情
export function getNoticeInfo(noticeId) {
  return request({
    url: `/fee/notice/${noticeId}`,
    method: 'get'
  })
}

// 新增缴费通知
export function addNotice(data) {
  return request({
    url: '/fee/notice',
    method: 'post',
    data
  })
}

// 修改缴费通知
export function updateNotice(data) {
  return request({
    url: '/fee/notice',
    method: 'put',
    data
  })
}

// 删除缴费通知
export function deleteNotice(noticeIds) {
  return request({
    url: `/fee/notice/${noticeIds}`,
    method: 'delete'
  })
}

// 发送通知（单条/批量）
export function sendNotice(data) {
  return request({
    url: '/fee/notice/send',
    method: 'post',
    data
  })
}

// 获取发送详情
export function getSendDetail(noticeId) {
  return request({
    url: `/fee/notice/${noticeId}/sendDetail`,
    method: 'get'
  })
}

// 标记已读/未读
export function markReadStatus(data) {
  return request({
    url: '/fee/notice/markRead',
    method: 'put',
    data
  })
}

// 导出缴费通知
export function exportNotice(params) {
  return request({
    url: '/fee/notice/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取收费项目列表（下拉选择用）
export function getChargeItemList(params) {
  return request({
    url: '/fee/item/list',
    method: 'get',
    params
  })
}

// 获取房屋树数据（指定房屋选择用）
export function getHouseTree(params) {
  return request({
    url: '/property/house/tree',
    method: 'get',
    params
  })
}

// 获取业主列表（指定业主选择用）
export function getOwnerList(params) {
  return request({
    url: '/property/owner/list',
    method: 'get',
    params
  })
}