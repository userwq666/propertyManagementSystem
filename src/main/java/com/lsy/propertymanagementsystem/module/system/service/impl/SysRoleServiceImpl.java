package com.lsy.propertymanagementsystem.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lsy.propertymanagementsystem.common.exception.BusinessException;
import com.lsy.propertymanagementsystem.module.system.dto.RoleDTO;
import com.lsy.propertymanagementsystem.module.system.dto.RoleVO;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleMenuDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import com.lsy.propertymanagementsystem.module.system.service.SysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//角色服务实现类
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRoleDomain> implements SysRoleService {
    //注入角色菜单映射器
    @Autowired
    private SysRoleMenuMapper roleMenuMapper;
    //注入用户角色映射器
    @Autowired
    private SysUserRoleMapper userRoleMapper;

    //转换角色为VO
    @Override
    public List<RoleVO> getRoleList() {
        LambdaQueryWrapper<SysRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysRoleDomain::getId);
        return this.list(wrapper).stream().map(this::convertToVO).collect(Collectors.toList());
    }

    //添加角色
    @Override
    @Transactional
    public void addRole(RoleDTO request) {
        LambdaQueryWrapper<SysRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDomain::getRoleName, request.getRoleName());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        SysRoleDomain role = new SysRoleDomain();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.save(role);
    }

    //更新角色
    @Override
    @Transactional
    public void updateRole(RoleDTO request) {
        SysRoleDomain role = this.getById(request.getId());
        if (role == null) {
            throw new BusinessException("角色不存在");
        }

        LambdaQueryWrapper<SysRoleDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleDomain::getRoleName, request.getRoleName());
        wrapper.ne(SysRoleDomain::getId, request.getId());
        if (this.count(wrapper) > 0) {
            throw new BusinessException("角色名称已存在");
        }

        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRemark(request.getRemark());
        this.updateById(role);
    }

    //删除角色
    @Override
    @Transactional
    public void deleteRole(Long id) {
        LambdaQueryWrapper<SysUserRoleDomain> userRoleWrapper = new LambdaQueryWrapper<>();
        userRoleWrapper.eq(SysUserRoleDomain::getRoleId, id);
        if (userRoleMapper.selectCount(userRoleWrapper) > 0) {
            throw new BusinessException("该角色下存在用户，不允许删除");
        }

        LambdaQueryWrapper<SysRoleMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDomain::getRoleId, id);
        roleMenuMapper.delete(wrapper);

        this.removeById(id);
    }

    //根据ID获取角色
    @Override
    public RoleVO getRoleById(Long id) {
        SysRoleDomain domain = this.getById(id);
        return domain != null ? convertToVO(domain) : null;
    }

    //为角色分配菜单
    @Override
    @Transactional
    public void assignMenus(Long roleId, List<Long> menuIds) {
        LambdaQueryWrapper<SysRoleMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDomain::getRoleId, roleId);
        roleMenuMapper.delete(wrapper);

        for (Long menuId : menuIds) {
            SysRoleMenuDomain roleMenu = new SysRoleMenuDomain();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenuMapper.insert(roleMenu);
        }
    }

    //根据角色ID获取菜单ID列表
    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        LambdaQueryWrapper<SysRoleMenuDomain> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRoleMenuDomain::getRoleId, roleId);
        List<SysRoleMenuDomain> roleMenus = roleMenuMapper.selectList(wrapper);
        return roleMenus.stream().map(SysRoleMenuDomain::getMenuId).collect(Collectors.toList());
    }

    //转换角色为VO
    private RoleVO convertToVO(SysRoleDomain domain) {
        RoleVO vo = new RoleVO();
        BeanUtils.copyProperties(domain, vo);
        return vo;
    }
}