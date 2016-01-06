package com.rascal.module.auth.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.security.AuthUserDetails;
import com.rascal.core.service.BaseService;
import com.rascal.core.service.Validation;
import com.rascal.core.util.EnumUtils;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.Privilege;
import com.rascal.module.auth.entity.RoleR2Privilege;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.User.AuthTypeEnum;
import com.rascal.module.auth.entity.UserR2Role;
import com.rascal.module.auth.service.PrivilegeService;
import com.rascal.module.auth.service.RoleService;
import com.rascal.module.auth.service.UserService;
import com.rascal.module.sys.service.MenuService;
import com.rascal.module.sys.vo.NavMenuVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Controller
@RequestMapping(value = "/admin/auth/user")
public class UserController extends BaseController<User, Long> {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private MenuService menuService;

    @Override
    protected BaseService<User, Long> getEntityService() {
        return userService;
    }

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MenuData("配置管理:权限管理:用户账号")
    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("authTypeMap", EnumUtils.getEnumDataMap(AuthTypeEnum.class));
        return "admin/auth/user-index";
    }

    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<User> findByPage(HttpServletRequest request) {
        return super.findByPage(User.class, request);
    }

    @RequestMapping(value = "/edit-tabs", method = RequestMethod.GET)
    public String editTabs() {
        return "admin/auth/user-inputTabs";
    }

    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow(Model model) {
        model.addAttribute("authTypeMap", EnumUtils.getEnumDataMap(AuthTypeEnum.class));
        model.addAttribute("roles", roleService.findAllCached());
        return "admin/auth/user-inputBasic";
    }

    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") User entity,
                                    @RequestParam(value = "rawPassword", required = false) String rawPassword) {
        Validation.notDemoMode();
        if (entity.isNew()) {
            Validation.isTrue(StringUtils.isNotBlank(rawPassword), "创建用户必须设置初始密码");
        }
        userService.saveCascadeR2Roles(entity, rawPassword);
        return OperationResult.buildSuccessResult("数据保存处理完成", entity);
    }

    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@RequestParam("ids") Long... ids) {
        Validation.notDemoMode();
        return super.delete(ids);
    }

    @MetaData(value = "汇总用户关联权限集合")
    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/privileges", method = RequestMethod.GET)
    public String privileges(Model model, @ModelAttribute("entity") User entity) {
        Set<Long> r2PrivilegeIds = Sets.newHashSet();
        List<Privilege> privileges = privilegeService.findAllCached();
        List<UserR2Role> userR2Roles = entity.getUserR2Roles();
        boolean isAdmin = false;
        for (UserR2Role r2 : userR2Roles) {
            if (r2.getRole().getCode().equals(AuthUserDetails.ROLE_SUPER_USER)) {
                isAdmin = true;
                break;
            }
        }

        if (isAdmin) {
            for (Privilege privilege : privileges) {
                r2PrivilegeIds.add(privilege.getId());
            }
        } else {
            for (UserR2Role userR2Role : userR2Roles) {
                List<RoleR2Privilege> roleR2Privileges = userR2Role.getRole().getRoleR2Privileges();
                for (RoleR2Privilege roleR2Privilege : roleR2Privileges) {
                    r2PrivilegeIds.add(roleR2Privilege.getPrivilege().getId());
                }
            }
        }
        model.addAttribute("r2PrivilegeIds", StringUtils.join(r2PrivilegeIds, ","));
        model.addAttribute("readonly", true);
        return "admin/auth/role-privileges";
    }

    @MetaData(value = "汇总用户关联菜单集合")
    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/menus", method = RequestMethod.GET)
    public String menusShow() {
        return "admin/auth/user-menus";
    }

    @RequiresPermissions("配置管理:权限管理:用户账号")
    @RequestMapping(value = "/menus/data", method = RequestMethod.GET)
    @ResponseBody
    public Object menusData(@ModelAttribute("entity") User entity) {
        List<Map<String, Object>> items = Lists.newArrayList();
        List<NavMenuVO> navMenuVOs = menuService.processUserMenu(entity);
        for (NavMenuVO navMenuVO : navMenuVOs) {
            //组装zTree结构数据
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

    @RequestMapping(value = "/tags", method = RequestMethod.GET)
    @ResponseBody
    public List<Map<String, Object>> tagsData(@RequestParam("q") String q) {
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildDefaultOrGroupFilter();
        groupFilter.append(new PropertyFilter(MatchType.CN, "authUid", q));
        groupFilter.append(new PropertyFilter(MatchType.CN, "nickName", q));
        groupFilter.append(new PropertyFilter(MatchType.CN, "email", q));
        groupFilter.append(new PropertyFilter(MatchType.CN, "mobile", q));
        List<User> users = userService.findByFilters(groupFilter);
        List<Map<String, Object>> items = Lists.newArrayList();
        for (User user : users) {
            Map<String, Object> item = Maps.newHashMap();
            item.put("id", user.getAlias());
            item.put("text", user.getAlias());
            items.add(item);
        }
        return items;
    }
}
