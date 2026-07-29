import request from "@/utils/request"

export function getSuggestionList(params) {
  return request({ url: "/complaint/suggest/page", method: "get", params })
}

export function addSuggestion(data) {
  return request({ url: "/complaint/suggest", method: "post", data })
}

export function updateSuggestion(data) {
  return request({ url: "/complaint/suggest", method: "put", data })
}

export function deleteSuggestion(id) {
  return request({ url: `/complaint/suggest/${id}`, method: "delete" })
}

export function updateSuggestionStatus(id, status) {
  return request({ url: "/complaint/suggest/status", method: "put", params: { id, status } })
}
