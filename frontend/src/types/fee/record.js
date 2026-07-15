export interface FeeRecord {
  recordId: number
  recordNo: string
  ownerId: number
  ownerName: string
  ownerPhone: string
  houseId: number
  houseNo: string
  buildingName: string
  unitNo: string
  roomNo: string
  itemId: number
  itemName: string
  itemType: string
  itemTypeLabel: string
  chargeCycle: string
  chargeCycleLabel: string
  chargeAmount: number
  chargeUnit: string
  billingMethod: string
  billingMethodLabel: string
  payableAmount: number
  paidAmount: number
  refundAmount: number
  arrearsAmount: number
  payMethod: string
  payMethodLabel: string
  payStatus: '0' | '1' | '2' | '3'
  payStatusLabel: string
  payTime: string
  payPeriod: string
  payPeriodLabel: string
  refundStatus: '0' | '1' | '2'
  refundStatusLabel: string
  refundAmount: number
  refundReason: string
  refundMethod: string
  refundMethodLabel: string
  refundTime: string
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

export interface FeeRecordForm {
  recordId?: number
  recordNo?: string
  ownerId?: number
  ownerName?: string
  ownerPhone?: string
  houseId?: number
  houseNo?: string
  buildingName?: string
  unitNo?: string
  roomNo?: string
  itemId?: number
  itemName?: string
  itemType?: string
  chargeCycle?: string
  chargeAmount?: number
  chargeUnit?: string
  billingMethod?: string
  payableAmount?: number
  paidAmount?: number
  refundAmount?: number
  arrearsAmount?: number
  payMethod?: string
  payStatus?: '0' | '1' | '2' | '3'
  payTime?: string
  payPeriod?: string
  refundStatus?: '0' | '1' | '2'
  refundAmount?: number
  refundReason?: string
  refundMethod?: string
  refundTime?: string
  remark?: string
}

export interface FeeRecordQuery {
  pageNum: number
  pageSize: number
  ownerName?: string
  houseNo?: string
  itemId?: number
  payMethod?: string
  payStatus?: string
  payPeriod?: string
  beginTime?: string
  endTime?: string
}

export interface RefundForm {
  recordId: number
  recordNo: string
  refundAmount: number
  refundReason: string
  refundMethod: string
  refundRemark: string
}

export interface FeeRecordStatistics {
  totalAmount: number
  paidAmount: number
  refundAmount: number
  arrearsAmount: number
}

export interface DictOption {
  dictValue: string
  dictLabel: string
  dictType?: string
}

export const PAY_METHOD_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '现金' },
  { dictValue: '2', dictLabel: '微信' },
  { dictValue: '3', dictLabel: '支付宝' },
  { dictValue: '4', dictLabel: '银行转账' },
  { dictValue: '5', dictLabel: 'POS机' },
  { dictValue: '6', dictLabel: '其他' }
]

export const PAY_STATUS_OPTIONS: DictOption[] = [
  { dictValue: '0', dictLabel: '待缴费' },
  { dictValue: '1', dictLabel: '已缴费' },
  { dictValue: '2', dictLabel: '部分缴费' },
  { dictValue: '3', dictLabel: '已退费' }
]

export const REFUND_STATUS_OPTIONS: DictOption[] = [
  { dictValue: '0', dictLabel: '无退费' },
  { dictValue: '1', dictLabel: '退费中' },
  { dictValue: '2', dictLabel: '已退费' }
]

export const REFUND_METHOD_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '原路退回' },
  { dictValue: '2', dictLabel: '现金退费' },
  { dictValue: '3', dictLabel: '银行转账' },
  { dictValue: '4', dictLabel: '其他' }
]

export const REFUND_REASON_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '重复缴费' },
  { dictValue: '2', dictLabel: '金额错误' },
  { dictValue: '3', dictLabel: '业主退房' },
  { dictValue: '4', dictLabel: '费用减免' },
  { dictValue: '5', dictLabel: '其他原因' }
]

export const CHARGE_CYCLE_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '月' },
  { dictValue: '2', dictLabel: '季' },
  { dictValue: '3', dictLabel: '半年' },
  { dictValue: '4', dictLabel: '年' },
  { dictValue: '5', dictLabel: '一次性' }
]

export function getPayStatusType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  switch (status) {
    case '0': return 'warning'
    case '1': return 'success'
    case '2': return 'info'
    case '3': return 'danger'
    default: return 'info'
  }
}

export function getRefundStatusType(status: string): 'success' | 'warning' | 'danger' | 'info' {
  switch (status) {
    case '0': return 'info'
    case '1': return 'warning'
    case '2': return 'success'
    default: return 'info'
  }
}

export function formatAmount(amount: number | string): string {
  const num = typeof amount === 'string' ? parseFloat(amount) : amount
  if (isNaN(num)) return '0.00'
  return num.toFixed(2)
}

// 退费表单扩展接口（包含显示用字段）
export interface RefundFormExtend extends RefundForm {
  ownerHouse?: string
  itemName?: string
  paidAmount?: string
  refundedAmount?: string
  refundableAmount?: string
}