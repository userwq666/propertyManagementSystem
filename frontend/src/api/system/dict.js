import request from '@/utils/request'

export function getDictTypeList(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

export function getDictTypeInfo(dictId) {
  return request({
    url: `/system/dict/type/${dictId}`,
    method: 'get'
  })
}

export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

export function deleteDictType(dictIds) {
  return request({
    url: `/system/dict/type/${dictIds}`,
    method: 'delete'
  })
}

export function exportDictType(params) {
  return request({
    url: '/system/dict/type/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function changeDictTypeStatus(dictId, status) {
  return request({
    url: `/system/dict/type/changeStatus`,
    method: 'put',
    data: { dictId, status }
  })
}

// 字典数据相关 API
export function getDictDataList(params) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params
  })
}

export function getDictDataInfo(dictCode) {
  return request({
    url: `/system/dict/data/${dictCode}`,
    method: 'get'
  })
}

export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

export function deleteDictData(dictCodes) {
  return request({
    url: `/system/dict/data/${dictCodes}`,
    method: 'delete'
  })
}

export function exportDictData(params) {
  return request({
    url: '/system/dict/data/export',
    method: 'get',
    params,
    responseType: 'blob'
  })
}

export function changeDictDataStatus(dictCode, status) {
  return request({
    url: `/system/dict/data/changeStatus`,
    method: 'put',
    data: { dictCode, status }
  })
}