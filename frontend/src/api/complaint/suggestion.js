import request from '@/utils/request'

/**
 * 获取投诉建议分页列表
 */
export function getSuggestionPage(params) {
  return request({
    url: '/complaint/suggest/page',
    method: 'get',
    params
  })
}

/**
 * 获取投诉建议详情
 */
export function getSuggestionInfo(id) {
  return request({
    url: `/complaint/suggest/${id}`,
    method: 'get'
  })
}

/**
 * 新增投诉建议
 */
export function addSuggestion(data) {
  return request({
    url: '/complaint/suggest',
    method: 'post',
    data
  })
}

/**
 * 修改投诉建议
 */
export function updateSuggestion(data) {
  return request({
    url: '/complaint/suggest',
    method: 'put',
    data
  })
}

/**
 * 删除投诉建议
 */
export function deleteSuggestion(id) {
  return request({
    url: `/complaint/suggest/${id}`,
    method: 'delete'
  })
}

/**
 * 更新投诉建议状态
 */
export function updateSuggestionStatus(id, status) {
  return request({
    url: \/complaint/suggest/\/status\,
    method: 'put',
    data: { status }
  })
}

/**
 * 获取投诉建议统计
 */
export function getSuggestionStatistics() {
  return request({
    url: '/complaint/suggest/statistics',
    method: 'get'
  })
}

/**
 * 获取处理人列表
 */
export function getHandlerList(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}
