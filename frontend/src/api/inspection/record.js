import request from \"@/utils/request\"

export function getInspectionRecordList(params) {
  return request({ url: \"/inspection/record/page\", method: \"get\", params })
}

export function getInspectionRecordInfo(id) {
  return request({ url: \/inspection/record/\\, method: \"get\" })
}

export function addInspectionRecord(data) {
  return request({ url: \"/inspection/record\", method: \"post\", data })
}

export function updateInspectionRecord(data) {
  return request({ url: \"/inspection/record\", method: \"put\", data })
}

export function deleteInspectionRecord(id) {
  return request({ url: \/inspection/record/\\, method: \"delete\" })
}

export function getInspectionPlanOptions(params) {
  return request({ url: \"/inspection/plan/page\", method: \"get\", params })
}

export function getUserList(params) {
  return request({ url: \"/system/user/page\", method: \"get\", params })
}
