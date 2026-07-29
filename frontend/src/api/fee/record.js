import request from "@/utils/request"

export function getFeeRecordList(params) {
  return request({ url: "/fee/record/page", method: "get", params })
}

export function getFeeRecordInfo(id) {
  return request({ url: `/fee/record/${id}`, method: "get" })
}

export function generateFeeRecords(data) {
  return request({ url: "/fee/record/generate", method: "post", data })
}

export function confirmPay(id, payWay) {
  return request({ url: "/fee/record/pay", method: "put", params: { id, payWay } })
}

export function getFeeRecordStatistics() {
  return request({ url: "/fee/record/statistics", method: "get" })
}

export function markOverdue() {
  return request({ url: "/fee/record/markOverdue", method: "put" })
}
