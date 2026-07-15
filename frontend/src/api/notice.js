import request from '@/utils/request'

export function getAnnouncementList(params) {
  return request({
    url: '/notice/announcement/list',
    method: 'get',
    params
  })
}

export function getAnnouncementInfo(announcementId) {
  return request({
    url: `/notice/announcement/${announcementId}`,
    method: 'get'
  })
}

export function addAnnouncement(data) {
  return request({
    url: '/notice/announcement',
    method: 'post',
    data
  })
}

export function updateAnnouncement(data) {
  return request({
    url: '/notice/announcement',
    method: 'put',
    data
  })
}

export function deleteAnnouncement(announcementIds) {
  return request({
    url: `/notice/announcement/${announcementIds}`,
    method: 'delete'
  })
}

export function publishAnnouncement(announcementId) {
  return request({
    url: `/notice/announcement/${announcementId}/publish`,
    method: 'put'
  })
}

export function getMessageList(params) {
  return request({
    url: '/notice/message/list',
    method: 'get',
    params
  })
}

export function getMessageInfo(messageId) {
  return request({
    url: `/notice/message/${messageId}`,
    method: 'get'
  })
}

export function readMessage(messageId) {
  return request({
    url: `/notice/message/${messageId}/read`,
    method: 'put'
  })
}

export function readAllMessages() {
  return request({
    url: '/notice/message/readAll',
    method: 'put'
  })
}

export function deleteMessage(messageIds) {
  return request({
    url: `/notice/message/${messageIds}`,
    method: 'delete'
  })
}

export function getUnreadCount() {
  return request({
    url: '/notice/message/unreadCount',
    method: 'get'
  })
}

export function getTemplateList(params) {
  return request({
    url: '/notice/template/list',
    method: 'get',
    params
  })
}

export function getTemplateInfo(templateId) {
  return request({
    url: `/notice/template/${templateId}`,
    method: 'get'
  })
}

export function addTemplate(data) {
  return request({
    url: '/notice/template',
    method: 'post',
    data
  })
}

export function updateTemplate(data) {
  return request({
    url: '/notice/template',
    method: 'put',
    data
  })
}

export function deleteTemplate(templateIds) {
  return request({
    url: `/notice/template/${templateIds}`,
    method: 'delete'
  })
}