import request from "@/utils/request"

export function getHouseList(params) {
  return request({ url: "/community/house/page", method: "get", params })
}

export function getHouseInfo(id) {
  return request({ url: `/community/house/${id}`, method: "get" })
}

export function addHouse(data) {
  return request({ url: "/community/house", method: "post", data })
}

export function updateHouse(data) {
  return request({ url: "/community/house", method: "put", data })
}

export function deleteHouse(id) {
  return request({ url: `/community/house/${id}`, method: "delete" })
}
