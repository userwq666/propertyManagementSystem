export const RepairTypeOptions = [
  { value: '1', label: '水电维修' },
  { value: '2', label: '设备维修' },
  { value: '3', label: '公共设施' },
  { value: '4', label: '安保服务' },
  { value: '5', label: '清洁保洁' },
  { value: '6', label: '其他' }
]

export const PriorityOptions = [
  { value: '1', label: '低' },
  { value: '2', label: '中' },
  { value: '3', label: '高' },
  { value: '4', label: '紧急' }
]

export const OrderStatusOptions = [
  { value: '1', label: '待派单' },
  { value: '2', label: '派单中' },
  { value: '3', label: '处理中' },
  { value: '4', label: '待确认' },
  { value: '5', label: '已完成' },
  { value: '6', label: '已取消' }
]

export const ProgressOptions = [
  { value: '0', label: '0%' },
  { value: '10', label: '10%' },
  { value: '20', label: '20%' },
  { value: '30', label: '30%' },
  { value: '40', label: '40%' },
  { value: '50', label: '50%' },
  { value: '60', label: '60%' },
  { value: '70', label: '70%' },
  { value: '80', label: '80%' },
  { value: '90', label: '90%' },
  { value: '100', label: '100%' }
]

export const OrderStatusColorMap = {
  '1': 'info',
  '2': 'warning',
  '3': 'primary',
  '4': 'warning',
  '5': 'success',
  '6': 'danger'
}

export const PriorityColorMap = {
  '1': 'success',
  '2': 'info',
  '3': 'warning',
  '4': 'danger'
}

export const RepairTypeColorMap = {
  '1': 'primary',
  '2': 'success',
  '3': 'warning',
  '4': 'danger',
  '5': 'info',
  '6': ''
}

export const TabTypes = [
  { key: 'mine', label: '我的报修', status: '' },
  { key: 'pending', label: '待派单', status: '1' },
  { key: 'dispatching', label: '派单中', status: '2' },
  { key: 'processing', label: '处理中', status: '3' },
  { key: 'confirming', label: '待确认', status: '4' },
  { key: 'completed', label: '已完成', status: '5' },
  { key: 'all', label: '全部工单', status: '' }
]
