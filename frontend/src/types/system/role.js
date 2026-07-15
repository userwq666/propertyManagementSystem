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

export interface RoleQueryParams {
  pageNum?: number
  pageSize?: number
  roleName?: string
  roleKey?: string
  status?: string
}

export interface RoleFormData {
  roleId?: number
  roleName: string
  roleKey: string
  roleSort: number
  status: '0' | '1'
  menuIds: number[]
  deptIds: number[]
  remark: string
}

export interface MenuTreeNode {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  query: string
  isFrame: number
  isCache: number
  menuType: string
  visible: string
  status: string
  perms: string
  icon: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  children: MenuTreeNode[]
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

export interface RoleMenuTreeselectVo {
  menus: MenuTreeNode[]
  checkedKeys: number[]
}