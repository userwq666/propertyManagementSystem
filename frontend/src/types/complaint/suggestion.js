export const ComplaintTypeOptions = [
  { value: '1', label: '物业服务' },
  { value: '2', label: '环境卫生' },
  { value: '3', label: '噪音扰民' },
  { value: '4', label: '安全问题' },
  { value: '5', label: '停车问题' },
  { value: '6', label: '设施维修' },
  { value: '7', label: '其他' }
]

export const ComplaintStatusOptions = [
  { value: '0', label: '待受理' },
  { value: '1', label: '受理中' },
  { value: '2', label: '处理中' },
  { value: '3', label: '已回复' },
  { value: '4', label: '已完成' },
  { value: '5', label: '已关闭' }
]

export const SatisfactionOptions = [
  { value: '5', label: '非常满意' },
  { value: '4', label: '满意' },
  { value: '3', label: '一般' },
  { value: '2', label: '不满意' },
  { value: '1', label: '非常不满意' }
]

export const ComplaintTypeColorMap = {
  '1': 'primary',
  '2': 'success',
  '3': 'warning',
  '4': 'danger',
  '5': 'info',
  '6': '',
  '7': 'info'
}

export const ComplaintStatusColorMap = {
  '0': 'info',
  '1': 'warning',
  '2': 'primary',
  '3': '',
  '4': 'success',
  '5': 'danger'
}

export const SatisfactionColorMap = {
  '5': 'success',
  '4': 'success',
  '3': 'warning',
  '2': 'danger',
  '1': 'danger'
}

export const ComplaintSuggestionType = [
  { key: 'all', label: '全部', type: '' },
  { key: 'complaint', label: '投诉', type: '1' },
  { key: 'suggestion', label: '建议', type: '2' },
  { key: 'inquiry', label: '咨询', type: '3' },
  { key: 'praise', label: '表扬', type: '4' }
]
