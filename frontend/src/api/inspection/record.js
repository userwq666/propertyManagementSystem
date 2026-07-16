import request from '@/utils/request'

/**
 * 获取巡检记录列表
 */
export function getInspectionRecordList(params) {
  return request({
    url: '/inspection/record/page',
    method: 'get',
    params
  })
}

/**
 * 获取巡检记录详情
 */
export function getInspectionRecordInfo(recordId) {
  return request({
    url: `/inspection/record/${recordId}`,
    method: 'get'
  })
}

/**
 * 新增巡检记录
 */
export function addInspectionRecord(data) {
  return request({
    url: '/inspection/record',
    method: 'post',
    data
  })
}

/**
 * 修改巡检记录
 */
export function updateInspectionRecord(data) {
  return request({
    url: '/inspection/record',
    method: 'put',
    data
  })
}

/**
 * 删除巡检记录
 */
export function deleteInspectionRecord(id) {
  return request({
    url: `/inspection/record/${recordId}`,
    method: 'delete'
  })
}

/**
 * 获取巡检记录统计
 */
export function getInspectionRecordStatistics() {
  return request({
    url: '/inspection/record/statistics',
    method: 'get'
  })
}

/**
 * 获取巡检计划下拉列表
 */
export function getInspectionPlanOptions(params) {
  return request({
    url: '/inspection/plan/page',
    method: 'get',
    params
  })
}

/**
 * 获取用户列表
 */
export function getUserList(params) {
  return request({
    url: '/system/user/page',
    method: 'get',
    params
  })
}
