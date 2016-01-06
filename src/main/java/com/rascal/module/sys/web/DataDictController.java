package com.rascal.module.sys.web;

import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.service.BaseService;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.sys.entity.DataDict;
import com.rascal.module.sys.service.DataDictService;
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
@RequestMapping(value = "/admin/sys/data-dict")
public class DataDictController extends BaseController<DataDict, Long> {

    @Autowired
    private DataDictService dataDictService;

    @Override
    protected BaseService<DataDict, Long> getEntityService() {
        return dataDictService;
    }

    @MenuData("配置管理:系统管理:数据字典")
    @RequiresPermissions("配置管理:系统管理:数据字典")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/sys/dataDict-index";
    }

    @Override
    protected void appendFilterProperty(GroupPropertyFilter groupPropertyFilter) {
        if (groupPropertyFilter.isEmptySearch()) {
            groupPropertyFilter.forceAnd(new PropertyFilter(MatchType.NU, "parent", true));
        }
        super.appendFilterProperty(groupPropertyFilter);
    }

    @RequiresPermissions("配置管理:系统管理:数据字典")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<DataDict> findByPage(HttpServletRequest request) {
        return super.findByPage(DataDict.class, request);
    }

    @RequiresPermissions("配置管理:系统管理:数据字典")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow() {
        return "admin/sys/dataDict-inputBasic";
    }

    @RequiresPermissions("配置管理:系统管理:数据字典")
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult editSave(@ModelAttribute("entity") DataDict entity) {
        return super.editSave(entity);
    }

    @RequiresPermissions("配置管理:系统管理:数据字典")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(HttpServletRequest request, @ModelAttribute("entity") DataDict entity) {
        //基于提交请求参数判断用户是否已经进行确认过，如果否则进行后续的业务逻辑校验及反馈提示，如果已确认则直接通过
        if (postNotConfirmedByUser(request)) {
            List<DataDict> children = entity.getChildren();
            //反馈信息待用户进行confirm确认，如果用户确认OK则自动再次发起请求
            if (CollectionUtils.isNotEmpty(children)) {
                return OperationResult.buildConfirmResult("如果删除当前项目将递归删除所有子项");
            }
        }
        return super.delete(entity.getId());
    }

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MetaData(value = "级联子数据集合")
    @RequestMapping(value = "/children", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> children(@RequestParam(value = "id") Long id) {
        return dataDictService.findMapDataById(id);
    }
}
