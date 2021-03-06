package com.rascal.support.web;

import com.google.common.collect.Lists;
import com.rascal.core.annotation.MenuData;
import com.rascal.core.annotation.MetaData;
import com.rascal.core.cons.GlobalConstant;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.security.AuthContextHolder;
import com.rascal.core.security.AuthUserDetails;
import com.rascal.core.web.captcha.ImageCaptchaServlet;
import com.rascal.core.web.filter.WebAppContextInitFilter;
import com.rascal.core.web.view.OperationResult;
import com.rascal.module.auth.entity.SignupUser;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.User.AuthTypeEnum;
import com.rascal.module.auth.service.SignupUserService;
import com.rascal.module.auth.service.UserService;
import com.rascal.module.sys.entity.NotifyMessage;
import com.rascal.module.sys.entity.UserMessage;
import com.rascal.module.sys.service.MenuService;
import com.rascal.module.sys.service.NotifyMessageService;
import com.rascal.module.sys.service.UserMessageService;
import com.rascal.module.sys.vo.NavMenuVO;
import com.rascal.support.service.DynamicConfigService;
import com.rascal.support.service.MailService;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.util.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@Controller
public class AdminController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private UserService userService;

    @Autowired
    private SignupUserService signupUserService;

    @Autowired
    private MailService mailService;

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired
    private NotifyMessageService notifyMessageService;

    @Autowired
    private UserMessageService userMessageService;

    @RequiresRoles(AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/dashboard", method = RequestMethod.GET)
    public String dashboard(Model model) {
        model.addAttribute("signCount", signupUserService.count());
        return "admin/dashboard";
    }

    /**
     * 计算显示用户登录菜单数据
     */
    @RequestMapping(value = "/admin/menus", method = RequestMethod.GET)
    @ResponseBody
    public List<NavMenuVO> navMenu() {
        User user = AuthContextHolder.findAuthUser();
        //如果未登录则直接返回空
        if (user == null) {
            return Lists.newArrayList();
        }
        return menuService.processUserMenu(user);
    }

    @RequestMapping(value = "/admin/password/forget", method = RequestMethod.GET)
    public String forgetPasswordShow(Model model) {
        model.addAttribute("mailServiceEnabled", mailService.isEnabled());
        return "admin/pub/password-forget";
    }

    @RequestMapping(value = "/admin/password/forget", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult forgetPasswordSave(HttpServletRequest request, @RequestParam("uid") String uid, @RequestParam("captcha") String captcha) {
        if (!ImageCaptchaServlet.validateResponse(request, captcha)) {
            return OperationResult.buildFailureResult("验证码不正确，请重新输入");
        }
        User user = userService.findByAuthTypeAndAuthUid(AuthTypeEnum.SYS, uid);
        if (user == null) {
            user = userService.findByProperty("email", uid);
        }
        if (user == null) {
            return OperationResult.buildFailureResult("未找到匹配账号信息，请联系管理员处理");
        }
        String email = user.getEmail();
        if (StringUtils.isBlank(email)) {
            return OperationResult.buildFailureResult("当前账号未设定注册邮箱，请联系管理员先设置邮箱后再进行此操作");
        }

        userService.requestResetPassword(WebAppContextInitFilter.getInitedWebContextFullUrl(), user);
        return OperationResult.buildSuccessResult("找回密码请求处理成功。重置密码邮件已发送至：" + email);
    }

    @RequestMapping(value = "/admin/password/reset", method = RequestMethod.GET)
    public String restPasswordShow() {
        return "admin/pub/password-reset";
    }

    @RequestMapping(value = "/admin/password/reset", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult resetPasswordSave(@RequestParam("uid") String uid,
                                             @RequestParam("code") String code, @RequestParam("newpasswd") String newpasswd) throws IOException {
        User user = userService.findByAuthTypeAndAuthUid(AuthTypeEnum.SYS, uid);
        if (user != null) {
            if (code.equals(user.getUserExt().getRandomCode())) {
                //user.setRandomCode(null);
                //更新密码失效日期为6个月后
                user.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
                userService.save(user, newpasswd);
                return OperationResult.buildSuccessResult("密码重置成功，您可以马上使用新设定密码登录系统啦").setRedirect("/admin/login");
            } else {
                return OperationResult.buildFailureResult("验证码不正确或已失效，请尝试重新找回密码操作");
            }
        }
        return OperationResult.buildFailureResult("操作失败");
    }

    @RequestMapping(value = "/admin/signup", method = RequestMethod.GET)
    public String signupShow(Model model) {
        model.addAttribute("mailServiceEnabled", mailService.isEnabled());
        return "admin/pub/signup";
    }

    @RequestMapping(value = "/admin/signup", method = RequestMethod.POST)
    @ResponseBody
    public OperationResult signupSave(HttpServletRequest request, @RequestParam("captcha") String captcha, @ModelAttribute("entity") SignupUser entity) {
        if (!ImageCaptchaServlet.validateResponse(request, captcha)) {
            return OperationResult.buildFailureResult("验证码不正确，请重新输入");
        }
        if (dynamicConfigService.getBoolean(GlobalConstant.cfg_mgmt_signup_disabled, false)) {
            return OperationResult.buildFailureResult("系统暂未开发账号注册功能，如有疑问请联系管理员");
        }
        signupUserService.signup(entity, request.getParameter("password"));
        return OperationResult.buildSuccessResult("注册成功。需要等待管理员审批通过后方可登录系统。");
    }

    /**
     * 验证手机唯一性
     * <p>
     * 业务输入参数列表：
     * <ul>
     * <li><b>mobile</b> 手机号</li>
     * </ul>
     * </p>
     */
    @RequestMapping(value = "/admin/signup/unique/mobile", method = RequestMethod.GET)
    @ResponseBody
    public boolean authMobileUnique(HttpServletRequest request) {
        String mobile = request.getParameter("mobile");
        if (!CollectionUtils.isEmpty(userService.findByMobile(mobile))) {
            return false;
        }
        return CollectionUtils.isEmpty(signupUserService.findByMobile(mobile));
    }

    /**
     * 验证电子邮件唯一性
     * <p>
     * 业务输入参数列表：
     * <ul>
     * <li><b>email</b> 电子邮件</li>
     * </ul>
     * </p>
     */
    @RequestMapping(value = "/admin/signup/unique/email", method = RequestMethod.GET)
    @ResponseBody
    public boolean authEmailUnique(HttpServletRequest request) {
        String email = request.getParameter("email");
        if (!CollectionUtils.isEmpty(userService.findByEmail(email))) {
            return false;
        }
        return CollectionUtils.isEmpty(signupUserService.findByEmail(email));
    }

    /**
     * 验证电子邮件唯一性
     * <p>
     * 业务输入参数列表：
     * <ul>
     * <li><b>email</b> 电子邮件</li>
     * </ul>
     * </p>
     */
    @RequestMapping(value = "/admin/signup/unique/uid", method = RequestMethod.GET)
    @ResponseBody
    public boolean authUidUnique(HttpServletRequest request) {
        String authUid = request.getParameter("authUid");
        if (userService.findByAuthUid(authUid) != null) {
            return false;
        }
        return signupUserService.findByAuthUid(authUid) == null;
    }

    @MenuData("个人信息:公告消息")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/notify-message", method = RequestMethod.GET)
    public String notifyMessageIndex() {
        return "admin/profile/notifyMessage-index";
    }

    @MetaData("公告消息列表")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/notify-message-list", method = RequestMethod.GET)
    public String notifyMessageList(HttpServletRequest request, Model model) {
        User user = AuthContextHolder.findAuthUser();
        List<NotifyMessage> notifyMessages = null;
        String readed = request.getParameter("readed");
        if (StringUtils.isBlank(readed)) {
            notifyMessages = notifyMessageService.findStatedEffectiveMessages(user, "web_admin", null);
        } else {
            notifyMessages = notifyMessageService.findStatedEffectiveMessages(user, "web_admin",
                    BooleanUtils.toBoolean(request.getParameter("readed")));
        }
        model.addAttribute("notifyMessages", notifyMessages);
        return "admin/profile/notifyMessage-list";
    }

    @MetaData("公告消息读取")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/notify-message-view/{messageId}", method = RequestMethod.GET)
    public String notifyMessageView(@PathVariable("messageId") Long messageId, Model model) {
        User user = AuthContextHolder.findAuthUser();
        NotifyMessage notifyMessage = notifyMessageService.findOne(messageId);
        notifyMessageService.processUserRead(notifyMessage, user);
        model.addAttribute("notifyMessage", notifyMessage);
        return "admin/profile/notifyMessage-view";
    }

    @MenuData("个人信息:个人消息")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/user-message", method = RequestMethod.GET)
    public String userMessageIndex() {
        return "admin/profile/userMessage-index";
    }

    @MetaData("个人消息列表")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/user-message-list", method = RequestMethod.GET)
    public String userMessageList(HttpServletRequest request, Model model) {
        User user = AuthContextHolder.findAuthUser();
        Pageable pageable = PropertyFilter.buildPageableFromHttpRequest(request);
        GroupPropertyFilter groupFilter = GroupPropertyFilter.buildFromHttpRequest(NotifyMessage.class, request);
        groupFilter.append(new PropertyFilter(MatchType.EQ, "targetUser", user));
        String readed = request.getParameter("readed");
        if (StringUtils.isNotBlank(readed)) {
            if (BooleanUtils.toBoolean(request.getParameter("readed"))) {
                groupFilter.append(new PropertyFilter(MatchType.NN, "firstReadTime", Boolean.TRUE));
            } else {
                groupFilter.append(new PropertyFilter(MatchType.NU, "firstReadTime", Boolean.TRUE));
            }
        }
        Page<UserMessage> pageData = userMessageService.findByPage(groupFilter, pageable);
        model.addAttribute("pageData", pageData);
        return "admin/profile/userMessage-list";
    }

    @MetaData("个人消息读取")
    @RequiresRoles(value = AuthUserDetails.ROLE_MGMT_USER)
    @RequestMapping(value = "/admin/profile/user-message-view/{messageId}", method = RequestMethod.GET)
    public String userMessageView(@PathVariable("messageId") Long messageId, Model model) {
        User user = AuthContextHolder.findAuthUser();
        UserMessage userMessage = userMessageService.findOne(messageId);
        userMessageService.processUserRead(userMessage);
        model.addAttribute("notifyMessage", userMessage);
        return "admin/profile/notifyMessage-view";
    }
}
