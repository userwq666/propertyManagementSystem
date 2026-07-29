import request from "@/utils/request"

export function getInspectionPlanList(params) {
  return request({ url: "/inspection/plan/page", method: "get", params })
}

export function getInspectionPlanInfo(id) {
  return request({ url: `/inspection/plan/${id}`, method: "get" })
}

export function addInspectionPlan(data) {
  return request({ url: "/inspection/plan", method: "post", data })
}

export function updateInspectionPlan(data) {
  return request({ url: "/inspection/plan", method: "put", data })
}

export function deleteInspectionPlan(id) {
  return request({ url: `/inspection/plan/${id}`, method: "delete" })
}

export function updateInspectionPlanStatus(id, status) {
  return request({ url: "/inspection/plan/status", method: "put", params: { id, status } })
}

export function getUserList(params) {
  return request({ url: "/system/user/page", method: "get", params })
}

export function generatePlan() {
  return request({ url: "/inspection/plan/generate", method: "post" })
}


export function getInspectionPlanStatistics(params) {
  return request({ url: "/inspection/plan/statistics", method: "get", params })
}

export function getInspectionPlanRecords(params) {
  return request({ url: "/inspection/record/page", method: "get", params })
}