package com.rascal.module.auth.web;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.service.BaseService;
import com.rascal.core.service.Validation;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.Department;
import com.rascal.module.auth.service.DepartmentService;
import org.apache.commons.collections.CollectionUtils;
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

@Controller
@RequestMapping(value = "/admin/auth/department")
public class DepartmentController extends BaseController<Department, Long> {

    @Autowired
    private DepartmentService departmentService;

    @Override
    protected BaseService<Department, Long> getEntityService() {
        return departmentService;
    }

    @MenuData("配置管理:权限管理:部门配置")
    @RequiresPermissions("配置管理:权限管理:部门配置")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/auth/department-index";
    }

    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmptySearch()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @RequiresPermissions("配置管理:权限管理:部门配置")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<Department> findByPage(HttpServletRequest request) {
        return super.findByPage(Department.class, request);
    }

    @RequiresPermissions("配置管理:权限管理:部门配置")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") Department entity) {
        if (entity.getParent() != null && entity.getParent().getId() != null) {
            Department parent = departmentService.findOne(entity.getParent().getId());
            Validation.isTrue(entity.getCode().startsWith(parent.getCode()), "下级节点代码必须以父节点代码作为前缀");
        }
        return super.editSave(entity);
    }

    @RequestMapping(value = "/select", method = RequestMethod.GET)
    @ResponseBody
    public Object treeData() {
        List<Map<String, Object>> treeDatas = Lists.newArrayList();
        Iterable<Department> items = departmentService.findRoots();
        for (Department item : items) {
            loopTreeData(treeDatas, item);
        }
        return treeDatas;
    }

    private void loopTreeData(List<Map<String, Object>> treeDatas, Department item) {
        Map<String, Object> row = Maps.newHashMap();
        treeDatas.add(row);
        row.put("id", item.getId());
        row.put("name", item.getDisplay());
        row.put("open", false);
        List<Department> children = departmentService.findChildren(item);
        if (!CollectionUtils.isEmpty(children)) {
            List<Map<String, Object>> childrenList = Lists.newArrayList();
            row.put("children", childrenList);
            for (Department child : children) {
                loopTreeData(childrenList, child);
            }
        }
    }

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }
}
