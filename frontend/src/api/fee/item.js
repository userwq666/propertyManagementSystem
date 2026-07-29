import request from "@/utils/request"

export function getItemList(params) {
  return request({ url: "/fee/item/page", method: "get", params })
}

export function getItemInfo(id) {
  return request({ url: `/fee/item/${id}`, method: "get" })
}

export function addItem(data) {
  return request({ url: "/fee/item", method: "post", data })
}

export function updateItem(data) {
  return request({ url: "/fee/item", method: "put", data })
}

export function deleteItem(id) {
  return request({ url: `/fee/item/${id}`, method: "delete" })
}

export function updateItemStatus(id, status) {
  return request({ url: "/fee/item/status", method: "put", params: { id, status } })
}
