// 报修工单类型定义

// 报修类型字典
export const RepairTypeOptions = [
  { value: '1', label: '水电维修' },
  { value: '2', label: '设备维修' },
  { value: '3', label: '公共设施' },
  { value: '4', label: '安保服务' },
  { value: '5', label: '清洁保洁' },
  { value: '6', label: '其他' }
]

// 优先级字典
export const PriorityOptions = [
  { value: '1', label: '低' },
  { value: '2', label: '中' },
  { value: '3', label: '高' },
  { value: '4', label: '紧急' }
]

// 工单状态字典
export const OrderStatusOptions = [
  { value: '1', label: '待派单' },
  { value: '2', label: '派单中' },
  { value: '3', label: '处理中' },
  { value: '4', label: '待确认' },
  { value: '5', label: '已完成' },
  { value: '6', label: '已取消' }
]

// 处理进度字典
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

// 状态颜色映射
export const OrderStatusColorMap = {
  '1': 'info',    // 待派单 - 蓝色
  '2': 'warning', // 派单中 - 橙色
  '3': 'primary', // 处理中 - 蓝色
  '4': 'warning', // 待确认 - 橙色
  '5': 'success', // 已完成 - 绿色
  '6': 'danger'   // 已取消 - 红色
}

export const PriorityColorMap = {
  '1': 'success',  // 低 - 绿色
  '2': 'info',     // 中 - 蓝色
  '3': 'warning',  // 高 - 橙色
  '4': 'danger'    // 紧急 - 红色
}

export const RepairTypeColorMap = {
  '1': 'primary',   // 水电维修
  '2': 'success',   // 设备维修
  '3': 'warning',   // 公共设施
  '4': 'danger',    // 安保服务
  '5': 'info',      // 清洁保洁
  '6': ''           // 其他
}

// 标签页类型
export const TabTypes = [
  { key: 'mine', label: '我的报修', status: '' },
  { key: 'pending', label: '待派单', status: '1' },
  { key: 'dispatching', label: '派单中', status: '2' },
  { key: 'processing', label: '处理中', status: '3' },
  { key: 'confirming', label: '待确认', status: '4' },
  { key: 'completed', label: '已完成', status: '5' },
  { key: 'all', label: '全部工单', status: '' }
]

// 报修工单基本信息
export interface RepairOrder {
  orderId: number
  orderNo: string
  title: string
  repairType: string
  repairTypeLabel?: string
  priority: string
  priorityLabel?: string
  location: string
  description: string
  images: string[]
  reporterId: number
  reporterName: string
  reporterPhone: string
  reporterType: string // 1业主 2租户 3物业人员
  houseId: number
  houseName: string
  roomNumber: string
  handlerId: number
  handlerName: string
  handlerPhone: string
  status: string
  statusLabel?: string
  progress: number
  estimatedFinishTime: string
  actualFinishTime: string
  dispatchTime: string
  cancelReason: string
  cancelTime: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  // 派单相关
  dispatchId?: number
  dispatchRemark?: string
  // 评价相关
  evaluateId?: number
  evaluateScore?: number
  evaluateContent?: string
  evaluateImages?: string[]
  evaluateReply?: string
  evaluateReplyTime?: string
  // 进度日志
  progressLogs?: ProgressLog[]
}

// 查询参数
export interface RepairOrderQueryParams {
  pageNum: number
  pageSize: number
  orderNo?: string
  title?: string
  repairType?: string
  priority?: string
  status?: string
  reporterName?: string
  reporterPhone?: string
  handlerName?: string
  beginTime?: string
  endTime?: string
  houseId?: number | string
  tabType?: string
}

// 新增/编辑工单表单
export interface RepairOrderFormData {
  orderId?: number
  title: string
  repairType: string
  priority: string
  location: string
  description: string
  images: string[]
  reporterPhone: string
  houseId?: number | string
  // 派单相关
  handlerId?: number | string
  estimatedFinishTime?: string
  dispatchRemark?: string
  // 处理进度相关
  progress?: number
  processRemark?: string
  processImages?: string[]
  // 完工确认相关
  finishDescription?: string
  finishImages?: string[]
  ownerSignature?: string
  ownerScore?: number
  ownerEvaluate?: string
}

// 派单表单
export interface DispatchFormData {
  dispatchId?: number
  orderId: number
  handlerId: number | string
  estimatedFinishTime: string
  dispatchRemark: string
}

// 处理进度表单
export interface ProcessFormData {
  orderId: number
  progress: number
  processRemark: string
  processImages: string[]
}

// 完工确认表单
export interface FinishFormData {
  orderId: number
  finishDescription: string
  finishImages: string[]
  ownerSignature?: string
  ownerScore?: number
  ownerEvaluate?: string
}

// 评价回复表单
export interface EvaluateReplyFormData {
  evaluateId: number
  reply: string
}

// 取消工单表单
export interface CancelFormData {
  orderId: number
  reason: string
}

// 统计卡片数据
export interface RepairStatistics {
  pendingDispatchCount: number
  processingCount: number
  pendingConfirmCount: number
  completedCount: number
  timeoutCount: number
}

// 列表响应
export interface RepairOrderListResponse {
  rows: RepairOrder[]
  total: number
}

// 房屋树节点
export interface HouseTreeNode {
  id: number | string
  label: string
  children?: HouseTreeNode[]
  parentId?: number | string
  houseType?: string
  buildingName?: string
  unitName?: string
  roomNumber?: string
}

// 维修人员
export interface RepairWorker {
  workerId: number
  workerName: string
  workerPhone: string
  workerType: string
  specialties: string[]
  status: string
  avatar: string
}

// 评价信息
export interface RepairEvaluate {
  evaluateId: number
  orderId: number
  orderNo: string
  title: string
  score: number
  content: string
  images: string[]
  reply: string
  replyTime: string
  createTime: string
  reporterName: string
  handlerName: string
}

// 图片上传响应
export interface UploadImageResponse {
  url: string
  name: string
}

// 进度日志
export interface ProgressLog {
  logId: number
  orderId: number
  handlerId: number
  handlerName: string
  progress: number
  processRemark: string
  processImages: string[]
  createTime: string
}