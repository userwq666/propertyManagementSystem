import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/logout',
    method: 'post'
  })
}

export function getInfo() {
  return request({
    url: '/getInfo',
    method: 'get'
  })
}

export function getCaptcha() {
  return request({
    url: '/captchaImage',
    method: 'get'
  })
}

export function getRouters() {
  return request({
    url: '/getRouters',
    method: 'get'
  })
}