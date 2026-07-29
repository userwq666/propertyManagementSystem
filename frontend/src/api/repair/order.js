import request from "@/utils/request"

export function getRepairOrderList(params) {
  return request({ url: "/repair/record/page", method: "get", params })
}

export function getRepairOrderInfo(id) {
  return request({ url: `/repair/record/${id}`, method: "get" })
}

export function addRepairOrder(data) {
  return request({ url: "/repair/record", method: "post", data })
}

export function updateRepairOrder(data) {
  return request({ url: "/repair/record", method: "put", data })
}

export function deleteRepairOrder(id) {
  return request({ url: `/repair/record/${id}`, method: "delete" })
}

export function updateRepairOrderStatus(params) {
  return request({ url: "/repair/record/status", method: "put", params })
}

export function submitRating(id, score, comment) {
  return request({ url: "/repair/record/rating", method: "put", params: { id, score, comment } })
}


export function getRepairStatistics(params) {
  return request({ url: "/repair/record/statistics", method: "get", params })
}

export function getRepairWorkerList(params) {
  return request({ url: "/system/user/page", method: "get", params: { ...params, userType: 4 } })
}