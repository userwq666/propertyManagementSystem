import request from '@/utils/request'

// 获取收费项目列表
export function getChargeItemList(params) {
  return request({
    url: '/fee/item/page',
    method: 'get',
    params
  })
}

// 获取收费项目详情
export function getChargeItemInfo(itemId) {
  return request({
    url: `/fee/item/${itemId}`,
    method: 'get'
  })
}

// 新增收费项目
export function addChargeItem(data) {
  return request({
    url: '/fee/item',
    method: 'post',
    data
  })
}

// 修改收费项目
export function updateChargeItem(data) {
  return request({
    url: '/fee/item',
    method: 'put',
    data
  })
}

// 删除收费项目
export function deleteChargeItem(id) {
  return request({
    url: `/fee/item/${itemId}`,
    method: 'delete'
  })
}

// 获取收费标准列表（关联收费项目）
export function getChargeStandardList(params) {
  return request({
    url: '/fee/standard/page',
    method: 'get',
    params
  })
}
