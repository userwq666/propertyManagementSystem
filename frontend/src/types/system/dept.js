export interface Dept {
  deptId: number
  parentId: number
  ancestors: string
  deptName: string
  orderNum: number
  leader: string
  phone: string
  email: string
  status: '0' | '1'
  delFlag: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  children: Dept[]
  hasChildren: boolean
}

export interface DeptQueryParams {
  pageNum: number
  pageSize: number
  deptName?: string
  status?: string
  beginTime?: string
  endTime?: string
}

export interface DeptFormData {
  deptId?: number
  parentId: number | string
  deptName: string
  orderNum: number
  leader: string
  phone: string
  email: string
  status: '0' | '1'
  remark?: string
}

export interface DeptTreeNode {
  deptId: number
  deptName: string
  parentId: number
  orderNum: number
  leader: string
  phone: string
  email: string
  status: '0' | '1'
  children: DeptTreeNode[]
}

export interface DeptListResponse {
  rows: Dept[]
  total: number
}