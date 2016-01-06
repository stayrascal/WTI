package com.rascal.support.web;

import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.security.AuthContextHolder;
import com.rascal.core.security.AuthUserDetails;
import com.rascal.core.service.Validation;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.service.UserService;
import com.rascal.module.sys.entity.UserProfileData;
import com.rascal.module.sys.service.UserProfileDataService;
import com.rascal.support.service.MailService;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
public class UserProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private MailService mailService;

    @Autowired
    private UserProfileDataService userProfileDataService;

    @MenuData("个人信息:个人配置")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/edit", method = RequestMethod.GET)
    public String profileLayoutShow(@ModelAttribute("user") User user) {
        user.addExtraAttributes(userProfileDataService.findMapDataByUser(user));
        return "admin/profile/profile-edit";
    }

    @RequiresRoles(AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/layout", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult profileLayoutSave(@ModelAttribute("user") User user) {
        Map<String, Object> extraAttributes = user.getExtraAttributes();
        for (Map.Entry<String, Object> me : extraAttributes.entrySet()) {
            String code = me.getKey();
            UserProfileData entity = userProfileDataService.findByUserAndCode(user, code);
            if (entity == null) {
                entity = new UserProfileData();
                entity.setUser(user);
            }
            entity.setCode(code);
            entity.setValue(me.getValue().toString());
            userProfileDataService.save(entity);
        }
        return OperationResult.buildSuccessResult("界面布局配置参数保存成功");
    }

    @RequiresRoles(AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/password", method = RequestMethod.GET)
    public String modifyPasswordShow(Model model) {
        model.addAttribute("mailServiceEnabled", mailService.isEnabled());
        return "admin/profile/password-edit";
    }

    @RequiresRoles(AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/password", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult modifyPasswordSave(HttpServletRequest request, @RequestParam("oldpasswd") String oldpasswd,
            @RequestParam("newpasswd") String newpasswd) {
        User user = AuthContextHolder.findAuthUser();
        String encodedPasswd = userService.encodeUserPasswd(user, oldpasswd);
        if (!encodedPasswd.equals(user.getPassword())) {
            return OperationResult.buildFailureResult("原密码不正确,请重新输入");
        } else {
            Validation.notDemoMode();
            //更新密码失效日期为6个月后
            user.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
            userService.save(user, newpasswd);
            return OperationResult.buildSuccessResult("密码修改成功,请在下次登录使用新密码");
        }
    }

    @MetaData("密码过期强制重置-显示")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/credentials-expire", method = RequestMethod.GET)
    public String profileCredentialsExpireShow() {
        return "admin/profile/credentials-expire";
    }

    @MetaData("密码过期强制重置-更新")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/credentials-expire", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult profileCredentialsExpireSave(@RequestParam("newpasswd") String newpasswd) {
        User user = AuthContextHolder.findAuthUser();
        Validation.notDemoMode();
        //更新密码失效日期为6个月后
        user.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
        userService.save(user, newpasswd);
        return OperationResult.buildSuccessResult("密码修改成功,请在下次登录使用新密码").setRedirect("/admin");
    }

    @ModelAttribute
    public void prepareModel(Model model) {
        User user = AuthContextHolder.findAuthUser();
        model.addAttribute("user", user);
    }
}
