export interface ChargeItem {
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
  applicableObject: string
  applicableObjectLabel: string
  status: '0' | '1'
  statusLabel: string
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

export interface ChargeItemForm {
  itemId?: number
  itemName: string
  itemType: string
  chargeCycle: string
  chargeAmount: number
  chargeUnit: string
  billingMethod: string
  applicableObject: string
  status: '0' | '1'
  remark?: string
}

export interface ChargeItemQuery {
  pageNum: number
  pageSize: number
  itemName?: string
  itemType?: string
  chargeCycle?: string
  status?: string
  beginTime?: string
  endTime?: string
}

// 字典选项类型
export interface DictOption {
  dictValue: string
  dictLabel: string
  dictType?: string
}

// 字典类型常量
export const CHARGE_ITEM_TYPE_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '物业费' },
  { dictValue: '2', dictLabel: '车位费' },
  { dictValue: '3', dictLabel: '水电费' },
  { dictValue: '4', dictLabel: '暖气费' },
  { dictValue: '5', dictLabel: '维修基金' },
  { dictValue: '6', dictLabel: '其他' }
]

export const CHARGE_CYCLE_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '月' },
  { dictValue: '2', dictLabel: '季' },
  { dictValue: '3', dictLabel: '半年' },
  { dictValue: '4', dictLabel: '年' },
  { dictValue: '5', dictLabel: '一次性' }
]

export const BILLING_METHOD_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '按套' },
  { dictValue: '2', dictLabel: '按面积' },
  { dictValue: '3', dictLabel: '按车位' },
  { dictValue: '4', dictLabel: '固定金额' }
]

export const APPLICABLE_OBJECT_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '全体' },
  { dictValue: '2', dictLabel: '住宅' },
  { dictValue: '3', dictLabel: '商铺' },
  { dictValue: '4', dictLabel: '车库' },
  { dictValue: '5', dictLabel: '办公' }
]

export const STATUS_OPTIONS: DictOption[] = [
  { dictValue: '0', dictLabel: '启用' },
  { dictValue: '1', dictLabel: '禁用' }
]

export const CHARGE_UNIT_OPTIONS: DictOption[] = [
  { dictValue: '元/月', dictLabel: '元/月' },
  { dictValue: '元/季', dictLabel: '元/季' },
  { dictValue: '元/半年', dictLabel: '元/半年' },
  { dictValue: '元/年', dictLabel: '元/年' },
  { dictValue: '元/次', dictLabel: '元/次' },
  { dictValue: '元/㎡', dictLabel: '元/㎡' },
  { dictValue: '元/车位', dictLabel: '元/车位' },
  { dictValue: '元/套', dictLabel: '元/套' }
]