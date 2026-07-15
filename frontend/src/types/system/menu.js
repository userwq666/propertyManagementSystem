export interface Menu {
  menuId: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  query: string
  isFrame: number
  isCache: number
  menuType: 'M' | 'C' | 'F'
  visible: '0' | '1'
  status: '0' | '1'
  perms: string
  icon: string
  createBy: string
  createTime: string
  updateBy: string
  updateTime: string
  remark: string
  children?: Menu[]
}

export interface MenuQueryParams {
  pageNum?: number
  pageSize?: number
  menuName?: string
  status?: string
  visible?: string
}

export interface MenuFormData {
  menuId?: number
  menuName: string
  parentId: number
  orderNum: number
  path: string
  component: string
  query: string
  isFrame: number
  isCache: number
  menuType: 'M' | 'C' | 'F'
  visible: '0' | '1'
  status: '0' | '1'
  perms: string
  icon: string
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

export interface MenuTreeselectVo {
  menus: MenuTreeNode[]
  checkedKeys: number[]
}