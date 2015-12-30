package com.rascal.module.auth.service;

import com.google.common.collect.Maps;
import com.rascal.aud.dao.UserLoginLogDao;
import com.rascal.aud.entity.UserLoginLog;
import com.rascal.core.dao.jpa.BaseDao;
import com.rascal.core.security.PasswordService;
import com.rascal.core.service.BaseService;
import com.rascal.core.service.Validation;
import com.rascal.core.util.UidUtils;
import com.rascal.module.auth.dao.PrivilegeDao;
import com.rascal.module.auth.dao.RoleDao;
import com.rascal.module.auth.dao.UserDao;
import com.rascal.module.auth.entity.Privilege;
import com.rascal.module.auth.entity.Role;
import com.rascal.module.auth.entity.User;
import com.rascal.module.auth.entity.User.AuthTypeEnum;
import com.rascal.support.service.DynamicConfigService;
import com.rascal.support.service.FreemakerService;
import com.rascal.support.service.MailService;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Transactional
public class UserService extends BaseService<User, Long> {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserLoginLogDao userLoginLogDao;

    @Autowired
    private PrivilegeDao privilegeDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private PasswordService passwordService;

    @Autowired
    private DynamicConfigService dynamicConfigService;

    @Autowired
    private MailService mailService;

    @Autowired(required = false)
    private FreemakerService freemakerService;

    @Override
    protected BaseDao<User, Long> getEntityDao() {
        return userDao;
    }

    @Transactional(readOnly = true)
    public User findBySysAuthUid(String authUid) {
        return findByAuthTypeAndAuthUid(AuthTypeEnum.SYS, authUid);
    }

    @Transactional(readOnly = true)
    public User findByAuthTypeAndAuthUid(AuthTypeEnum authType, String authUid) {
        return userDao.findByAuthTypeAndAuthUid(authType, authUid);
    }

    @Transactional(readOnly = true)
    public User findBySysAccessToken(String accessToken) {
        return findByAuthTypeAndAccessToken(AuthTypeEnum.SYS, accessToken);
    }

    @Transactional(readOnly = true)
    public User findByAuthTypeAndAccessToken(AuthTypeEnum authType, String accessToken) {
        return userDao.findByAuthTypeAndAccessToken(authType, accessToken);
    }

    public String encodeUserPasswd(User user, String rawPassword) {
        return passwordService.entryptPassword(rawPassword, user.getAuthGuid());
    }

    public User save(User entity) {
        return super.save(entity);
    }

    public User save(User entity, final String rawPassword) {
        if (entity.isNew()) {
            Validation.notBlank(rawPassword, "创建账号必须提供初始密码");
            Date now = new Date();
            if (entity.getCredentialsExpireTime() == null) {
                //默认六个月后密码失效，到时用户登录强制要求重置密码
                entity.setCredentialsExpireTime(new DateTime().plusMonths(6).toDate());
            }
            entity.setSignupTime(now);
            entity.setAuthGuid(UidUtils.UID());
        }
        if (StringUtils.isBlank(entity.getNickName())) {
            entity.setNickName(entity.getAuthGuid());
        }
        if (StringUtils.isNotBlank(rawPassword)) {
            String encodedPassword = encodeUserPasswd(entity, rawPassword);
            if (StringUtils.isNotBlank(entity.getPassword())) {
                Validation.isTrue(!entity.getPassword().equals(encodedPassword), "变更密码不能与当前密码一样");
            }
            entity.setPassword(encodedPassword);
        }
        return userDao.save(entity);
    }

    public User saveCascadeR2Roles(User entity, String rawPassword) {
        updateRelatedR2s(entity, entity.getSelectedRoleIds(), "userR2Rolese", "role");
        return save(entity, rawPassword);
    }

    @Transactional(readOnly = true)
    public List<Role> findRoles(User user) {
        return roleDao.findRolesForUser(user);
    }

    @Transactional(readOnly = true)
    public List<Privilege> findPrivileges(Set<String> roleCodes) {
        return privilegeDao.findPrivileges(roleCodes);
    }

    public void requestResetPassword(String webContextUrl, User user) {
        String email = user.getEmail();
        Assert.isTrue(StringUtils.isNotBlank(email), "User email required");
        String subject = dynamicConfigService.getString("cfg.user.reset.pwd.notify.email.title", "申请重置密码邮件");
        user.setRandomcode(UidUtils.UID());
        userDao.save(user);

        webContextUrl += ("/admin/password/reset?uid=" + user.getAuthGuid() + "&emial=" + email + "&code" + user.getRandomcode());
        if (freemakerService != null) {
            Map<String, Object> params = Maps.newHashMap();
            params.put("user", user);
            params.put("resetPasswordLink", webContextUrl);
            String contents = freemakerService.processTemplateByFileName("PASSWORD_RESET_NOTUFY_EMIAL", params);
            mailService.sendHtmlMail(subject, contents, true, email);
        } else {
            mailService.sendHtmlMail(subject, webContextUrl, true, email);
        }
    }

    @Async
    public void userLoginLog(User user, UserLoginLog userLoginLog) {
        String httpSessionId = userLoginLog.getHttpSessionId();
        if (StringUtils.isNotBlank(httpSessionId)) {
            if (userLoginLogDao.findByHttpSessionId(httpSessionId) != null) {
                return;
            }
        }

        //登陆记录
        user.setLogonTimes(userLoginLog.getLoginTimes() + 1);
        user.setLastLogonIp(userLoginLog.getRemoteAddr());
        user.setLastLogonHost(userLoginLog.getRemoteHost());
        user.setLastLogonTime(new Date());

        //重置失败次数计数
        user.setLogonFailureTimes(null);
        user.setLogonTimes(0);
        userDao.save(user);

        userLoginLog.setLoginTimes(user.getLogonTimes());
        userLoginLogDao.save(userLoginLog);
    }

    public User findByAuthUid(String authUid) {
        return userDao.findByAuthUid(authUid);
    }

    public User findByRandomCodeAndAuthUid(String randomCode, String mobile) {
        return userDao.findByRandomCodeAndAuthUid(randomCode, mobile);
    }


}
