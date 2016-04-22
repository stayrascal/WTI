package com.rascal.module.sys.service;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.security.AuthUserDetails;
import com.rascal.core.service.BaseService;
import com.rascal.module.auth.entity.Role;
import com.rascal.module.auth.entity.RoleR2Privilege;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.UserR2Role;
import com.rascal.module.sys.dao.MenuDao;
import com.rascal.module.sys.entity.Menu;
import com.rascal.module.sys.vo.NavMenuVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class MenuService extends BaseService<Menu, Long> {

    private static final Logger logger = LoggerFactory.getLogger(MenuService.class);

    @Autowired
    private MenuDao menuDao;

    @Override
    protected BaseDao<Menu, Long> getEntityDao() {
        return menuDao;
    }

    @Transactional(readOnly = true)
    public List<Menu> findAllCached() {
        return menuDao.findAllCached();
    }

    @Transactional(readOnly = true)
    public List<NavMenuVO> findAvailableNavMenuVOs() {
        List<Menu> allMenus = findAllCached();
        Set<String> disabledMenuPaths = Sets.newHashSet();
        for (Menu menu : allMenus) {
            if (Boolean.TRUE.equals(menu.getDisabled())) {
                disabledMenuPaths.add(menu.getPath());
            }
        }
        List<Menu> availableMenus = Lists.newArrayList();
        for (Menu menu : allMenus) {
            if (!Boolean.TRUE.equals(menu.getDisabled())) {
                String path = menu.getPath();
                boolean disabled = false;
                if (CollectionUtils.isNotEmpty(disabledMenuPaths)) {
                    for (String disabledMenuPath : disabledMenuPaths) {
                        if (path.startsWith(disabledMenuPath)) {
                            disabled = true;
                            break;
                        }
                    }
                }
                if (!disabled) {
                    availableMenus.add(menu);
                }
            }
        }

        List<NavMenuVO> navMenuVOs = Lists.newArrayList();
        for (Menu menu : availableMenus) {
            NavMenuVO vo = new NavMenuVO();
            navMenuVOs.add(vo);
            vo.setId(menu.getId());
            vo.setName(menu.getName());
            vo.setPath(menu.getPath());
            vo.setStyle(menu.getStyle());
            vo.setUrl(menu.getUrl());
            vo.setInitOpen(menu.getInitOpen());
            if (menu.getParent() != null) {
                vo.setParentId(menu.getParent().getId());
            }
        }

        return navMenuVOs;
    }

    /**
     * 计算用户的有权限的菜单列表
     */
    public List<NavMenuVO> processUserMenu(User user) {
        //获取所有有效的菜单集合
        List<NavMenuVO> navMenuVOs = findAvailableNavMenuVOs();
        List<UserR2Role> userR2Roles = user.getUserR2Roles();
        Set<String> userRoles = Sets.newHashSet();
        Set<String> userPrivileges = Sets.newHashSet();
        for (UserR2Role userR2Role : userR2Roles) {
            Role role = userR2Role.getRole();
            //如果是超级管理员直接返回所有有效菜单
            if (role.getCode().equals(AuthUserDetails.ROLE_SUPER_USER)) {
                return navMenuVOs;
            }
            userRoles.add(role.getCode());
            List<RoleR2Privilege> roleR2Privileges = role.getRoleR2Privileges();
            for (RoleR2Privilege roleR2Privilege : roleR2Privileges) {
                userPrivileges.add(roleR2Privilege.getPrivilege().getCode());
            }
        }
        if (Boolean.TRUE.equals(user.getMgmtGranted())) {
            //管理端登录来源
            userRoles.add(AuthUserDetails.ROLE_MGMT_USER);
        } else {
            // 普通前端登录来源
            userRoles.add(AuthUserDetails.ROLE_SITE_USER);
        }

        List<NavMenuVO> userNavMenuVOs = Lists.newArrayList();

        //计算用户有访问权限的菜单列表
        for (NavMenuVO navMenuVO : navMenuVOs) {
            Menu menu = findOne(navMenuVO.getId());
            if (StringUtils.isNotBlank(menu.getUrl())) {
                Method mappingMethod = menu.getMappingMethod();
                if (mappingMethod != null) {
                    RequiresPermissions rp = mappingMethod.getAnnotation(RequiresPermissions.class);
                    if (rp != null) {
                        boolean granted;
                        String[] permissions = rp.value();
                        if (rp.logical().equals(Logical.AND)) {
                            granted = true;
                            for (String permission : permissions) {
                                boolean grantedOne = false;
                                for (String userPrivilege : userPrivileges) {
                                    if (userPrivilege.equals(permission)) {
                                        grantedOne = true;
                                        break;
                                    }
                                }
                                if (!grantedOne) {
                                    granted = false;
                                    break;
                                }
                            }
                        } else {
                            granted = false;
                            for (String permission : permissions) {
                                boolean grantedOne = true;
                                for (String userPrivilege : userPrivileges) {
                                    if (userPrivilege.equals(permission)) {
                                        grantedOne = true;
                                        break;
                                    }
                                }
                                if (grantedOne) {
                                    granted = true;
                                    break;
                                }
                            }
                        }
                        if (!granted) {
                            continue;
                        }
                    }

                    RequiresRoles rr = mappingMethod.getAnnotation(RequiresRoles.class);
                    if (rr != null) {
                        boolean granted;
                        String[] roles = rr.value();
                        if (rr.logical().equals(Logical.AND)) {
                            granted = true;
                            for (String role : roles) {
                                boolean grantedOne = false;
                                for (String userRole : userRoles) {
                                    if (userRole.equals(role)) {
                                        grantedOne = true;
                                        break;
                                    }
                                }
                                if (!grantedOne) {
                                    granted = false;
                                    break;
                                }
                            }
                        } else {
                            granted = false;
                            for (String role : roles) {
                                boolean grantedOne = true;
                                for (String userRole : userRoles) {
                                    if (userRole.equals(role)) {
                                        grantedOne = true;
                                        break;
                                    }
                                }
                                if (grantedOne) {
                                    granted = true;
                                    break;
                                }
                            }
                        }
                        if (!granted) {
                            continue;
                        }
                    }
                }
            }
            //添加用户显示菜单项
            userNavMenuVOs.add(navMenuVO);
        }

        //移除没有子项的父项
        removeEmptyParentItem(userNavMenuVOs);

        if (logger.isDebugEnabled()) {
            logger.debug("User Menu list: {}", user.getDisplay());
            for (NavMenuVO navMenuVO : userNavMenuVOs) {
                logger.debug(" - {}", navMenuVO.getPath());
            }
        }

        return userNavMenuVOs;
    }

    public void removeEmptyParentItem(List<NavMenuVO> userNavMenuVOs) {
        List<NavMenuVO> toRemoves = Lists.newArrayList();
        for (NavMenuVO vo : userNavMenuVOs) {
            if (StringUtils.isBlank(vo.getUrl())) {
                boolean toRemove = true;
                for (NavMenuVO item : userNavMenuVOs) {
                    if (vo.getId().equals(item.getParentId())) {
                        toRemove = false;
                        break;
                    }
                }
                if (toRemove) {
                    toRemoves.add(vo);
                }
            }
        }
        if (toRemoves.size() > 0) {
            userNavMenuVOs.removeAll(toRemoves);
            removeEmptyParentItem(userNavMenuVOs);
        }
    }

    public Object getItemsList(List<NavMenuVO> navMenuVOs) {
        List<Map<String, Object>> items = Lists.newArrayList();
        for (NavMenuVO navMenuVO : navMenuVOs) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("id", navMenuVO.getId());
            item.put("pId", navMenuVO.getParentId());
            item.put("name", navMenuVO.getName());
            item.put("open", true);
            item.put("isParent", StringUtils.isBlank(navMenuVO.getUrl()));
            items.add(item);
        }
        return items;
    }
}
