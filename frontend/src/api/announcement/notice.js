import request from "@/utils/request"

export function getNoticeList(params) {
  return request({ url: "/announcement/page", method: "get", params })
}

export function getNoticeInfo(id) {
  return request({ url: `/announcement/${id}`, method: "get" })
}

export function addNotice(data) {
  return request({ url: "/announcement", method: "post", data })
}

export function updateNotice(data) {
  return request({ url: "/announcement", method: "put", data })
}

export function deleteNotice(id) {
  return request({ url: `/announcement/${id}`, method: "delete" })
}

export function updateNoticeStatus(id, status) {
  return request({ url: "/announcement/status", method: "put", params: { id, status } })
}

export function toggleTopNotice(id, isTop) {
  return request({ url: "/announcement/top", method: "put", params: { id, isTop } })
}
