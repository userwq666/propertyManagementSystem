import request from '../utils/request'

export function addAnnouncement(data) {
  return request({
    url: '/api/announcement',
    method: 'post',
    data
  })
}

export function updateAnnouncement(data) {
  return request({
    url: '/api/announcement',
    method: 'put',
    data
  })
}

export function deleteAnnouncement(id) {
  return request({
    url: `/api/announcement/${id}`,
    method: 'delete'
  })
}

export function getAnnouncement(id) {
  return request({
    url: `/api/announcement/${id}`,
    method: 'get'
  })
}

export function pageAnnouncement(params) {
  return request({
    url: '/api/announcement/page',
    method: 'get',
    params
  })
}

export function updateAnnouncementStatus(params) {
  return request({
    url: '/api/announcement/status',
    method: 'put',
    params
  })
}

export function updateAnnouncementTop(params) {
  return request({
    url: '/api/announcement/top',
    method: 'put',
    params
  })
}
