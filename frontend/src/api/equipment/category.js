import request from '@/utils/request'

export function addCategory(data) {
  return request({ url: '/equipment/category', method: 'post', data })
}

export function updateCategory(data) {
  return request({ url: '/equipment/category', method: 'put', data })
}

export function deleteCategory(id) {
  return request({ url: `/equipment/category/${id}`, method: 'delete' })
}

export function getCategoryPage(params) {
  return request({ url: '/equipment/category/page', method: 'get', params })
}
