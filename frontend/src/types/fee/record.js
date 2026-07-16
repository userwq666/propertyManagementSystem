export const PAY_METHOD_OPTIONS = [
  { dictValue: '1', dictLabel: '现金' },
  { dictValue: '2', dictLabel: '微信' },
  { dictValue: '3', dictLabel: '支付宝' },
  { dictValue: '4', dictLabel: '银行转账' },
  { dictValue: '5', dictLabel: 'POS机' },
  { dictValue: '6', dictLabel: '其他' }
]

export const PAY_STATUS_OPTIONS = [
  { dictValue: '0', dictLabel: '待缴费' },
  { dictValue: '1', dictLabel: '已缴费' },
  { dictValue: '2', dictLabel: '部分缴费' },
  { dictValue: '3', dictLabel: '已退费' }
]

export const REFUND_STATUS_OPTIONS = [
  { dictValue: '0', dictLabel: '无退费' },
  { dictValue: '1', dictLabel: '退费中' },
  { dictValue: '2', dictLabel: '已退费' }
]

export const REFUND_METHOD_OPTIONS = [
  { dictValue: '1', dictLabel: '原路退回' },
  { dictValue: '2', dictLabel: '现金退费' },
  { dictValue: '3', dictLabel: '银行转账' },
  { dictValue: '4', dictLabel: '其他' }
]

export const REFUND_REASON_OPTIONS = [
  { dictValue: '1', dictLabel: '重复缴费' },
  { dictValue: '2', dictLabel: '金额错误' },
  { dictValue: '3', dictLabel: '业主退房' },
  { dictValue: '4', dictLabel: '费用减免' },
  { dictValue: '5', dictLabel: '其他原因' }
]

export const CHARGE_CYCLE_OPTIONS = [
  { dictValue: '1', dictLabel: '月' },
  { dictValue: '2', dictLabel: '季' },
  { dictValue: '3', dictLabel: '半年' },
  { dictValue: '4', dictLabel: '年' },
  { dictValue: '5', dictLabel: '一次性' }
]

export function getPayStatusType(status) {
  switch (status) {
    case '0': return 'warning'
    case '1': return 'success'
    case '2': return 'info'
    case '3': return 'danger'
    default: return 'info'
  }
}

export function getRefundStatusType(status) {
  switch (status) {
    case '0': return 'info'
    case '1': return 'warning'
    case '2': return 'success'
    default: return 'info'
  }
}

export function formatAmount(amount) {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  if (isNaN(num)) return '0.00'
  return num.toFixed(2)
}
