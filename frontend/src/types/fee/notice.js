export interface FeeNotice {
  noticeId: number
  noticeTitle: string
  itemId: number
  itemName: string
  itemType: string
  itemTypeLabel: string
  applicableScope: string
  applicableScopeLabel: string
  houseIds: number[]
  houseNames: string[]
  ownerIds: number[]
  ownerNames: string[]
  amount: number
  dueDate: string
  deadline: string
  content: string
  sendMethods: string
  sendMethodLabels: string[]
  sendStatus: '0' | '1' | '2'
  sendStatusLabel: string
  sendTime: string
  readStatus: '0' | '1'
  readStatusLabel: string
  remark: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
}

export interface FeeNoticeForm {
  noticeId?: number
  noticeTitle: string
  itemId: number
  applicableScope: '1' | '2' | '3'
  houseIds: number[]
  ownerIds: number[]
  amount: number
  dueDate: string
  deadline: string
  content: string
  sendMethods: string[]
  remark?: string
}

export interface FeeNoticeQuery {
  pageNum: number
  pageSize: number
  noticeTitle?: string
  itemId?: number
  houseId?: number
  ownerId?: number
  sendStatus?: string
  readStatus?: string
  beginTime?: string
  endTime?: string
}

export interface SendDetail {
  sendId: number
  noticeId: number
  noticeTitle: string
  receiveType: '1' | '2' | '3'
  receiveTypeLabel: string
  receiverId: number
  receiverName: string
  sendMethod: string
  sendMethodLabel: string
  sendStatus: '0' | '1' | '2'
  sendStatusLabel: string
  sendTime: string
  readStatus: '0' | '1'
  readStatusLabel: string
  readTime: string
  failReason: string
}

export interface SendDetailQuery {
  pageNum: number
  pageSize: number
  noticeId: number
  sendMethod?: string
  sendStatus?: string
  readStatus?: string
}

export interface SendNoticeData {
  noticeIds: number[]
  sendMethods?: string[]
}

export interface MarkReadData {
  noticeIds: number[]
  readStatus: '0' | '1'
}

export interface DictOption {
  dictValue: string
  dictLabel: string
  dictType?: string
}

export const APPLICABLE_SCOPE_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '全体' },
  { dictValue: '2', dictLabel: '指定房屋' },
  { dictValue: '3', dictLabel: '指定业主' }
]

export const SEND_METHOD_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '短信' },
  { dictValue: '2', dictLabel: '微信' },
  { dictValue: '3', dictLabel: 'APP推送' },
  { dictValue: '4', dictLabel: '邮件' },
  { dictValue: '5', dictLabel: '公告栏' }
]

export const SEND_STATUS_OPTIONS: DictOption[] = [
  { dictValue: '0', dictLabel: '待发送' },
  { dictValue: '1', dictLabel: '已发送' },
  { dictValue: '2', dictLabel: '发送失败' }
]

export const READ_STATUS_OPTIONS: DictOption[] = [
  { dictValue: '0', dictLabel: '未读' },
  { dictValue: '1', dictLabel: '已读' }
]

export const RECEIVE_TYPE_OPTIONS: DictOption[] = [
  { dictValue: '1', dictLabel: '房屋' },
  { dictValue: '2', dictLabel: '业主' },
  { dictValue: '3', dictLabel: '租户' }
]