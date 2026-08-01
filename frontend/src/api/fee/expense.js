import request from '@/utils/request'

export function getFeeExpensePage(params) {
  return request({ url: '/fee/expense/page', method: 'get', params })
}

export function addFeeExpense(data) {
  return request({ url: '/fee/expense', method: 'post', data })
}

export function updateFeeExpense(data) {
  return request({ url: '/fee/expense', method: 'put', data })
}

export function deleteFeeExpense(id) {
  return request({ url: `/fee/expense/${id}`, method: 'delete' })
}
