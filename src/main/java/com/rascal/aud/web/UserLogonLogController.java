package com.rascal.aud.web;

import com.fasterxml.jackson.annotation.JsonView;
import com.rascal.aud.entity.UserLogonLog;
import com.rascal.aud.service.UserLogonLogService;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.service.BaseService;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.json.JsonViews;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@MetaData("配置管理:系统记录:账户登录记录管理")
@Controller
@RequestMapping(value = "/admin/aud/user-logon-log")
public class UserLogonLogController extends BaseController<UserLogonLog, Long> {

    @Autowired
    private UserLogonLogService userLogonLogService;

    @Override
    protected BaseService<UserLogonLog, Long> getEntityService() {
        return userLogonLogService;
    }

    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MenuData("配置管理:系统记录:账户登录记录")
    @RequiresPermissions("配置管理:系统记录:账户登录记录")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/aud/userLogonLog-index";
    }

    @RequiresPermissions("配置管理:系统记录:账户登录记录")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    @JsonView(JsonViews.Admin.class)
    public Page<UserLogonLog> findByPage(HttpServletRequest request) {
        return super.findByPage(UserLogonLog.class, request);
    }

    @RequiresPermissions("配置管理:系统记录:账户登录记录")
    @RequestMapping(value = "/edit", method = RequestMethod.GET)
    public String editShow() {
        return "admin/aud/userLogonLog-inputBasic";
    }
}