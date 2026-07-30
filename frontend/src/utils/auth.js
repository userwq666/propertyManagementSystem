export function getToken() {
  return sessionStorage.getItem('token')
}

export function setToken(token) {
  sessionStorage.setItem('token', token)
}

export function removeToken() {
  sessionStorage.removeItem('token')
}

export function getUserInfo() {
  const info = sessionStorage.getItem('userInfo')
  return info ? JSON.parse(info) : null
}

export function setUserInfo(info) {
  sessionStorage.setItem('userInfo', JSON.stringify(info))
}

export function removeUserInfo() {
  sessionStorage.removeItem('userInfo')
}

export function getPermissions() {
  const perms = sessionStorage.getItem('permissions')
  return perms ? JSON.parse(perms) : []
}

export function setPermissions(permissions) {
  sessionStorage.setItem('permissions', JSON.stringify(permissions))
}

export function removePermissions() {
  sessionStorage.removeItem('permissions')
}