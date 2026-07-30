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
