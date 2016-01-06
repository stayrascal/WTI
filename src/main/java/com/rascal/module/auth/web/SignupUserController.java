package com.rascal.module.auth.web;

import com.rascal.core.annotation.MenuData;
import com.rascal.core.service.BaseService;
import com.rascal.core.service.Validation;
import com.rascal.core.web.BaseController;
import com.rascal.core.web.EntityProcessCallbackHandler;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.SignupUser;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.service.RoleService;
import com.rascal.module.auth.service.SignupUserService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresUser;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/admin/auth/signup-user")
public class SignupUserController extends BaseController<SignupUser, Long> {

    @Autowired
    private SignupUserService signupUserService;

    @Autowired
    private RoleService roleService;

    @Override
    protected BaseService<SignupUser, Long> getEntityService() {
        return signupUserService;
    }

    @RequiresUser
    @ModelAttribute
    public void prepareModel(HttpServletRequest request, Model model, @RequestParam(value = "id", required = false) Long id) {
        super.initPrepareModel(request, model, id);
    }

    @MenuData("配置管理:权限管理:注册用户管理")
    @RequiresPermissions("配置管理:权限管理:注册用户管理")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String index() {
        return "admin/auth/signupUser-index";
    }

    @RequiresPermissions("配置管理:权限管理:注册用户管理")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Page<SignupUser> findByPage(HttpServletRequest request) {
        return super.findByPage(SignupUser.class, request);
    }

    @RequiresPermissions("配置管理:权限管理:注册用户管理")
    @RequestMapping(value = "/audit", method = RequestMethod.GET)
    public String auditShow(Model model, @ModelAttribute("entity") SignupUser entity) {
        User user = new User();
        user.setMgmtGranted(true);
        //默认6个月后密码失效，到时用户登录强制要求重新设置密码
        user.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
        entity.setUser(user);
        model.addAttribute("roles", roleService.findAllCached());
        return "admin/auth/signupUser-audit";
    }

    @RequiresPermissions("配置管理:权限管理:注册用户管理")
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult auditSave(@ModelAttribute("entity") SignupUser entity) {
        Validation.notDemoMode();
        signupUserService.auditNewUser(entity);
        return OperationResult.buildSuccessResult("数据保存处理完成", entity);
    }

    @RequiresPermissions("配置管理:权限管理:注册用户管理")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult delete(@RequestParam("ids") Long... ids) {
        Validation.notDemoMode();
        return super.delete(ids, entity -> {
            if (entity.getAuditTime() != null) {
                throw new EntityProcessCallbackHandler.EntityProcessCallbackException("已审核数据不允许删除");
            }
        });
    }
}
