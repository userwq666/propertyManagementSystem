import request from '@/utils/request'

export function addAnnouncement(data) {
  return request({ url: '/announcement', method: 'post', data })
}

export function updateAnnouncement(data) {
  return request({ url: '/announcement', method: 'put', data })
}

export function deleteAnnouncement(id) {
  return request({ url: `/announcement/${id}`, method: 'delete' })
}

export function getAnnouncementById(id) {
  return request({ url: `/announcement/${id}`, method: 'get' })
}

export function getAnnouncementPage(params) {
  return request({ url: '/announcement/page', method: 'get', params })
}

export function updateAnnouncementStatus(params) {
  return request({ url: '/announcement/status', method: 'put', params })
}

export function updateAnnouncementTop(params) {
  return request({ url: '/announcement/top', method: 'put', params })
}
