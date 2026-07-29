import request from "@/utils/request"

export function getNoticeList(params) {
  return request({ url: "/fee/notice/page", method: "get", params })
}

export function getNoticeInfo(id) {
  return request({ url: `/fee/notice/${id}`, method: "get" })
}

export function addNotice(data) {
  return request({ url: "/fee/notice", method: "post", data })
}

export function updateNotice(data) {
  return request({ url: "/fee/notice", method: "put", data })
}

export function deleteNotice(id) {
  return request({ url: `/fee/notice/${id}`, method: "delete" })
}

export function publishNotice(id) {
  return request({ url: `/fee/notice/publish/${id}`, method: "put" })
}
