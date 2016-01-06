package com.rascal.core.security;

import com.rascal.module.auth.service.UserService;
import org.apache.shiro.web.filter.authc.LogoutFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class AppLogoutFilter extends LogoutFilter {

    private UserService userService;

    protected void issueRedirect(ServletRequest request, ServletResponse response, String redirectUrl) throws Exception {
        //TODO 基于accessToken找到登录记录相关处理
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
}
