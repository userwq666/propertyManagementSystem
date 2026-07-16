import request from '@/utils/request'

/**
 * 获取巡检计划列表
 */
export function getInspectionPlanList(params) {
  return request({
    url: '/inspection/plan/page',
    method: 'get',
    params
  })
}

/**
 * 获取巡检计划详情
 */
export function getInspectionPlanInfo(planId) {
  return request({
    url: `/inspection/plan/${planId}`,
    method: 'get'
  })
}

/**
 * 新增巡检计划
 */
export function addInspectionPlan(data) {
  return request({
    url: '/inspection/plan',
    method: 'post',
    data
  })
}

/**
 * 修改巡检计划
 */
export function updateInspectionPlan(data) {
  return request({
    url: '/inspection/plan',
    method: 'put',
    data
  })
}

/**
 * 删除巡检计划
 */
export function deleteInspectionPlan(id) {
  return request({
    url: `/inspection/plan/${planId}`,
    method: 'delete'
  })
}

/**
 * 获取巡检计划统计
 */
export function getInspectionPlanStatistics() {
  return request({
    url: '/inspection/plan/statistics',
    method: 'get'
  })
}

/**
 * 获取巡检计划执行记录
 */
export function getInspectionPlanRecords(params) {
  return request({
    url: '/inspection/plan/records',
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
