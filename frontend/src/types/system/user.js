export interface User {
  userId: number
  deptId: number
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: '0' | '1' | '2'
  avatar: string
  password: string
  status: '0' | '1'
  delFlag: '0' | '1'
  loginIp: string
  loginDate: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  dept: Dept
  roles: Role[]
  roleIds: number[]
  postIds: number[]
  roleId: number
  admin: boolean
}

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
}

export interface Role {
  roleId: number
  roleName: string
  roleKey: string
  roleSort: number
  dataScope: string
  menuCheckStrictly: boolean
  deptCheckStrictly: boolean
  status: '0' | '1'
  delFlag: '0' | '1'
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  flag: boolean
  menuIds: number[]
  deptIds: number[]
}

export interface UserQueryParams {
  pageNum: number
  pageSize: number
  userName?: string
  phonenumber?: string
  status?: string
  deptId?: number | string
  beginTime?: string
  endTime?: string
}

export interface UserFormData {
  userId?: number
  deptId: number | string
  userName: string
  nickName: string
  email: string
  phonenumber: string
  sex: '0' | '1' | '2'
  password?: string
  status: '0' | '1'
  remark: string
  roleIds: number[]
  postIds: number[]
}

export interface ResetPwdForm {
  userId: number
  password: string
  confirmPassword: string
}

export interface AssignRoleForm {
  userId: number
  roleIds: number[]
}

export interface UserListResponse {
  rows: User[]
  total: number
}