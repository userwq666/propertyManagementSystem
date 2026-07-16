export const PlanTypeOptions = [
  { value: '1', label: '日常巡检' },
  { value: '2', label: '专项巡检' },
  { value: '3', label: '设备巡检' },
  { value: '4', label: '安全巡检' },
  { value: '5', label: '消防巡检' }
]

export const PlanStatusOptions = [
  { value: '0', label: '草稿' },
  { value: '1', label: '待执行' },
  { value: '2', label: '执行中' },
  { value: '3', label: '已暂停' },
  { value: '4', label: '已完成' }
]

export const FrequencyOptions = [
  { value: '1', label: '每天' },
  { value: '2', label: '每周' },
  { value: '3', label: '每月' },
  { value: '4', label: '自定义' }
]

export const PlanStatusColorMap = {
  '0': 'info',
  '1': 'warning',
  '2': 'primary',
  '3': 'danger',
  '4': 'success'
}

export const PlanTypeColorMap = {
  '1': 'success',
  '2': 'primary',
  '3': 'warning',
  '4': 'danger',
  '5': ''
}

export const FrequencyColorMap = {
  '1': 'success',
  '2': 'primary',
  '3': 'warning',
  '4': 'info'
}
