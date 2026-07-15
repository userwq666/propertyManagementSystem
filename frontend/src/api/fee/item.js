import request from '@/utils/request'

// 获取收费项目列表
export function getChargeItemList(params) {
  return request({
    url: '/fee/item/list',
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
export function deleteChargeItem(itemIds) {
  return request({
    url: `/fee/item/${itemIds}`,
    method: 'delete'
  })
}

// 修改收费项目状态
export function changeChargeItemStatus(itemId, status) {
  return request({
    url: `/fee/item/changeStatus`,
    method: 'put',
    data: { itemId, status }
  })
}

// 导出收费项目
export function exportChargeItem(params) {
  return request({
    url: '/fee/item/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

// 获取收费标准列表（关联收费项目）
export function getChargeStandardList(params) {
  return request({
    url: '/fee/standard/list',
    method: 'get',
    params
  })
}

// 获取字典数据
export function getDictData(dictType) {
  return request({
    url: `/system/dict/data/type/${dictType}`,
    method: 'get'
  })
}