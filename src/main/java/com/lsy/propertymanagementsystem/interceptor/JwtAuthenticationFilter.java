package com.lsy.propertymanagementsystem.interceptor;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lsy.propertymanagementsystem.common.utils.JwtUtils;
import com.lsy.propertymanagementsystem.module.system.domain.SysRoleDomain;
import com.lsy.propertymanagementsystem.module.system.domain.SysUserRoleDomain;
import com.lsy.propertymanagementsystem.module.system.mapper.SysMenuMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysRoleMapper;
import com.lsy.propertymanagementsystem.module.system.mapper.SysUserRoleMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private SysMenuMapper menuMapper;

    @Autowired
    private SysUserRoleMapper userRoleMapper;

    @Autowired
    private SysRoleMapper roleMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();

        if (path.equals("/api/auth/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        String tabId = request.getHeader("X-Tab-Id");
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token != null && JwtUtils.isTokenValid(token, tabId)) {
            Claims claims = JwtUtils.parseClaims(token);
            if (claims != null) {
                Long userId = JwtUtils.getUserIdFromClaims(claims);
                String username = JwtUtils.getUsernameFromClaims(claims);

                request.setAttribute("userId", userId);
                request.setAttribute("username", username);

                List<SysUserRoleDomain> userRoles = userRoleMapper.selectList(
                        new LambdaQueryWrapper<SysUserRoleDomain>()
                                .eq(SysUserRoleDomain::getUserId, userId));
                String roleKey = null;
                if (!userRoles.isEmpty()) {
                    SysRoleDomain role = roleMapper.selectById(userRoles.get(0).getRoleId());
                    if (role != null) roleKey = role.getRoleKey();
                }
                request.setAttribute("roleKey", roleKey);

                List<String> permissions = menuMapper.selectPermsByUserId(userId);
                List<SimpleGrantedAuthority> authorities = permissions.stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
