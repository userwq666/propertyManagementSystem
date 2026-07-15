import request from '@/utils/request'

export function getComplaintList(params) {
  return request({
    url: '/complaint/list',
    method: 'get',
    params
  })
}

export function getComplaintInfo(complaintId) {
  return request({
    url: `/complaint/${complaintId}`,
    method: 'get'
  })
}

export function addComplaint(data) {
  return request({
    url: '/complaint',
    method: 'post',
    data
  })
}

export function updateComplaint(data) {
  return request({
    url: '/complaint',
    method: 'put',
    data
  })
}

export function deleteComplaint(complaintIds) {
  return request({
    url: `/complaint/${complaintIds}`,
    method: 'delete'
  })
}

export function getComplaintHandleList(params) {
  return request({
    url: '/complaint/handle/list',
    method: 'get',
    params
  })
}

export function getComplaintHandleInfo(handleId) {
  return request({
    url: `/complaint/handle/${handleId}`,
    method: 'get'
  })
}

export function addComplaintHandle(data) {
  return request({
    url: '/complaint/handle',
    method: 'post',
    data
  })
}

export function updateComplaintHandle(data) {
  return request({
    url: '/complaint/handle',
    method: 'put',
    data
  })
}

export function getFeedbackList(params) {
  return request({
    url: '/complaint/feedback/list',
    method: 'get',
    params
  })
}

export function getFeedbackInfo(feedbackId) {
  return request({
    url: `/complaint/feedback/${feedbackId}`,
    method: 'get'
  })
}

export function addFeedback(data) {
  return request({
    url: '/complaint/feedback',
    method: 'post',
    data
  })
}

export function getComplaintStatistics() {
  return request({
    url: '/complaint/statistics',
    method: 'get'
  })
}