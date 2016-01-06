package com.rascal.core.web.listener;

import com.rascal.aud.entity.UserLogonLog;
import com.rascal.aud.service.UserLogonLogService;
import com.rascal.core.context.SpringContextHolder;
import com.rascal.core.pagination.GroupPropertyFilter;
import com.rascal.core.pagination.PropertyFilter;
import com.rascal.core.pagination.PropertyFilter.MatchType;
import com.rascal.core.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Date;
import java.util.List;

/**
 * 通过监听器更新相关登录记录的登录时间
 */
public class AuthLogonHistRefreshListener implements HttpSessionListener, ServletContextListener {

    private final static Logger logger = LoggerFactory.getLogger(AuthLogonHistRefreshListener.class);

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        //Do nothing
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        HttpSession session = se.getSession();
        //基于Shiro判断session是否已登录过，由于未提供public常量访问因此直接参考HttpServletSession取代码中字符串
        if (session.getAttribute("org.apache.shiro.web.session.HttpServletSession.HOST_SESSION_KEY") != null) {
            String sessionId = session.getId();
            UserLogonLogService userLogonLogService = SpringContextHolder.getBean(UserLogonLogService.class);
            UserLogonLog userLogonLog = userLogonLogService.findBySessionId(sessionId);
            if (userLogonLog != null) {
                logger.debug("Setup logout time for session ID: {}", sessionId);
                userLogonLog.setLogoutTime(DateUtils.currentDate());
                userLogonLog.setLogonTimeLength(userLogonLog.getLogoutTime().getTime() - userLogonLog.getLogonTime().getTime());
                userLogonLogService.save(userLogonLog);
            }
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //Do nothing
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //在容器销毁时把未正常结束遗留的登录记录信息强制设置登出时间
        logger.info("ServletContext destroy force setup session user logout time...");

        UserLogonLogService userLogonLogService = SpringContextHolder.getBean(UserLogonLogService.class);
        GroupPropertyFilter groupPropertyFilter = GroupPropertyFilter.buildDefaultAndGroupFilter();
        groupPropertyFilter.append(new PropertyFilter(MatchType.NU, "logoutTime", Boolean.TRUE));
        List<UserLogonLog> userLogonLogs = userLogonLogService.findByFilters(groupPropertyFilter);
        if (!CollectionUtils.isEmpty(userLogonLogs)) {
            Date yesterday = new DateTime().minusDays(1).toDate();
            for (UserLogonLog userLogonLog : userLogonLogs) {
                //超过一天都没有登出的，直接强制设置登出时间
                if (userLogonLog.getLogonTime().before(yesterday)) {
                    Date logoutTime = new DateTime(userLogonLog.getLogonTime()).plusHours(1).toDate();
                    userLogonLog.setLogoutTime(logoutTime);

                    logger.debug(" - Setup logout time for session ID: {}", userLogonLog.getHttpSessionId());
                    userLogonLog.setLogonTimeLength(userLogonLog.getLogoutTime().getTime() - userLogonLog.getLogonTime().getTime());
                    userLogonLogService.save(userLogonLog);
                }
            }
        }
    }
}
