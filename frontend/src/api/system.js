import request from '@/utils/request'

export function getUserList(params) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params
  })
}

export function getUserInfo(userId) {
  return request({
    url: `/system/user/${userId}`,
    method: 'get'
  })
}

export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data
  })
}

export function updateUser(data) {
  return request({
    url: '/system/user',
    method: 'put',
    data
  })
}

export function deleteUser(userIds) {
  return request({
    url: `/system/user/${userIds}`,
    method: 'delete'
  })
}

export function resetPassword(userId, password) {
  return request({
    url: `/system/user/resetPwd`,
    method: 'put',
    data: { userId, password }
  })
}

export function updateProfile(data) {
  return request({
    url: '/system/user/profile',
    method: 'put',
    data
  })
}

export function updatePassword(data) {
  return request({
    url: '/system/user/profile/updatePwd',
    method: 'put',
    data
  })
}

export function updateAvatar(avatar) {
  return request({
    url: '/system/user/profile/avatar',
    method: 'post',
    data: { avatar }
  })
}

export function getRoleList(params) {
  return request({
    url: '/system/role/list',
    method: 'get',
    params
  })
}

export function getRoleInfo(roleId) {
  return request({
    url: `/system/role/${roleId}`,
    method: 'get'
  })
}

export function addRole(data) {
  return request({
    url: '/system/role',
    method: 'post',
    data
  })
}

export function updateRole(data) {
  return request({
    url: '/system/role',
    method: 'put',
    data
  })
}

export function deleteRole(roleIds) {
  return request({
    url: `/system/role/${roleIds}`,
    method: 'delete'
  })
}

export function getMenuList(params) {
  return request({
    url: '/system/menu/list',
    method: 'get',
    params
  })
}

export function getMenuTree(params) {
  return request({
    url: '/system/menu/tree',
    method: 'get',
    params
  })
}

export function getMenuInfo(menuId) {
  return request({
    url: `/system/menu/${menuId}`,
    method: 'get'
  })
}

export function addMenu(data) {
  return request({
    url: '/system/menu',
    method: 'post',
    data
  })
}

export function updateMenu(data) {
  return request({
    url: '/system/menu',
    method: 'put',
    data
  })
}

export function deleteMenu(menuIds) {
  return request({
    url: `/system/menu/${menuIds}`,
    method: 'delete'
  })
}

export function getDeptList(params) {
  return request({
    url: '/system/dept/list',
    method: 'get',
    params
  })
}

export function getDeptTree(params) {
  return request({
    url: '/system/dept/tree',
    method: 'get',
    params
  })
}

export function getDeptInfo(deptId) {
  return request({
    url: `/system/dept/${deptId}`,
    method: 'get'
  })
}

export function addDept(data) {
  return request({
    url: '/system/dept',
    method: 'post',
    data
  })
}

export function updateDept(data) {
  return request({
    url: '/system/dept',
    method: 'put',
    data
  })
}

export function deleteDept(deptIds) {
  return request({
    url: `/system/dept/${deptIds}`,
    method: 'delete'
  })
}

export function getDictTypeList(params) {
  return request({
    url: '/system/dict/type/list',
    method: 'get',
    params
  })
}

export function getDictTypeInfo(dictId) {
  return request({
    url: `/system/dict/type/${dictId}`,
    method: 'get'
  })
}

export function addDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'post',
    data
  })
}

export function updateDictType(data) {
  return request({
    url: '/system/dict/type',
    method: 'put',
    data
  })
}

export function deleteDictType(dictIds) {
  return request({
    url: `/system/dict/type/${dictIds}`,
    method: 'delete'
  })
}

export function getDictDataList(params) {
  return request({
    url: '/system/dict/data/list',
    method: 'get',
    params
  })
}

export function getDictDataInfo(dictCode) {
  return request({
    url: `/system/dict/data/${dictCode}`,
    method: 'get'
  })
}

export function addDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'post',
    data
  })
}

export function updateDictData(data) {
  return request({
    url: '/system/dict/data',
    method: 'put',
    data
  })
}

export function deleteDictData(dictCodes) {
  return request({
    url: `/system/dict/data/${dictCodes}`,
    method: 'delete'
  })
}

export function getConfigList(params) {
  return request({
    url: '/system/config/list',
    method: 'get',
    params
  })
}

export function getConfigInfo(configId) {
  return request({
    url: `/system/config/${configId}`,
    method: 'get'
  })
}

export function addConfig(data) {
  return request({
    url: '/system/config',
    method: 'post',
    data
  })
}

export function updateConfig(data) {
  return request({
    url: '/system/config',
    method: 'put',
    data
  })
}

export function deleteConfig(configIds) {
  return request({
    url: `/system/config/${configIds}`,
    method: 'delete'
  })
}

export function getLoginLogList(params) {
  return request({
    url: '/monitor/logininfo/list',
    method: 'get',
    params
  })
}

export function deleteLoginLog(infoIds) {
  return request({
    url: `/monitor/logininfo/${infoIds}`,
    method: 'delete'
  })
}

export function getOperationLogList(params) {
  return request({
    url: '/monitor/operlog/list',
    method: 'get',
    params
  })
}

export function deleteOperationLog(operIds) {
  return request({
    url: `/monitor/operlog/${operIds}`,
    method: 'delete'
  })
}