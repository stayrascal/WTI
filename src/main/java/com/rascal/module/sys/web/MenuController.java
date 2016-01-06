package com.rascal.module.sys.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.security.AuthUserDetails;
import com.rascal.core.service.BaseService;
import com.rascal.core.service.Validation;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.sys.entity.Menu;
import com.rascal.module.sys.service.MenuService;
import com.rascal.module.sys.vo.NavMenuVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/admin/sys/menu")
public class MenuController extends BaseController<Menu, Long> {

    @Autowired
    private MenuService menuService;

    @Override
    protected BaseService<Menu, Long> getEntityService() {
        return menuService;
    }

    @Override
    protected Menu buildDetachedBindingEntity(Long id) {
        return menuService.findDetachedOne(id, "parent");
    }

    @MenuData("配置管理:系统管理:菜单配置")
    @RequiresPermissions("配置管理:系统管理:菜单配置")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/sys/menu-index";
    }

    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmptySearch()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @RequiresPermissions("配置管理:系统管理:菜单配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<Menu> findByPage(HttpServletRequest request) {
        return super.findByPage(Menu.class, request);
    }

    @RequiresPermissions("配置管理:系统管理:菜单配置")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") Menu entity) {
        Validation.notDemoMode();
        return super.editSave(entity);
    }

    @RequiresPermissions("配置管理:系统管理:菜单配置")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@RequestParam("id") Long... id) {
        return super.delete(id);
    }

    @RequiresRoles(AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/data", method = RequestMethod.GET)
    @ResponseBody
    public Object menusData() {
        List<Map<String, Object>> items = Lists.newArrayList();
        List<NavMenuVO> navMenuVOs = menuService.findAvailableNavMenuVOs();
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

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }
}
