import request from '@/utils/request'

// 获取缴费通知列表
export function getNoticeList(params) {
  return request({
    url: '/fee/notice/page',
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
export function deleteNotice(id) {
  return request({
    url: `/fee/notice/${noticeId}`,
    method: 'delete'
  })
}

// 获取收费项目列表（下拉选择用）
export function getChargeItemList(params) {
  return request({
    url: '/fee/item/page',
    method: 'get',
    params
  })
}

// 获取业主列表（指定业主选择用）
export function getOwnerList(params) {
  return request({
    url: '/community/owner/page',
    method: 'get',
    params
  })
}
