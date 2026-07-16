with open("frontend/src/api/user.js", "w", encoding="utf-8") as f:
    f.write("""import request from '@/utils/request'

export function login(data) {
  return request({
    url: '/auth/login',
    method: 'post',
    data
  })
}

export function logout() {
  return request({
    url: '/auth/logout',
    method: 'post'
  })
}

export function getInfo() {
  return request({
    url: '/auth/me',
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
    url: '/system/menu/tree',
    method: 'get'
  })
}
""")
print("done")
