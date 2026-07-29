export const NOTICE_TYPE_OPTIONS = [
  { dictValue: '1', dictLabel: '物业公告' },
  { dictValue: '2', dictLabel: '安全通知' },
  { dictValue: '3', dictLabel: '活动通知' },
  { dictValue: '4', dictLabel: '缴费提醒' },
  { dictValue: '5', dictLabel: '停水停电' },
  { dictValue: '6', dictLabel: '政策法规' },
  { dictValue: '7', dictLabel: '其他' }
]

export const NOTICE_STATUS_OPTIONS = [
  { dictValue: '0', dictLabel: '草稿' },
  { dictValue: '1', dictLabel: '待发布' },
  { dictValue: '2', dictLabel: '已发布' },
  { dictValue: '3', dictLabel: '已撤回' }
]

export const NOTICE_IS_TOP_OPTIONS = [
  { dictValue: '0', dictLabel: '否' },
  { dictValue: '1', dictLabel: '是' }
]

export const PUBLISH_SCOPE_OPTIONS = [
  { dictValue: '1', dictLabel: '全员' },
  { dictValue: '2', dictLabel: '指定楼栋' },
  { dictValue: '3', dictLabel: '指定房屋类型' }
]

export const NOTICE_TYPE_MAP = {
  '1': '',
  '2': 'danger',
  '3': 'success',
  '4': 'warning',
  '5': 'danger',
  '6': 'info',
  '7': 'info'
}

export const NOTICE_STATUS_MAP = {
  '0': 'info',
  '1': 'warning',
  '2': 'success',
  '3': 'danger'
}
