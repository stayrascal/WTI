package com.rascal.core.web.listener;

import com.google.common.collect.Maps;
import com.rascal.core.cons.GlobalConstant;
import com.rascal.core.context.SpringContextHolder;
import com.rascal.support.service.DynamicConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * Spring容器加载“之后”的ServletContextListener
 */
public class ApplicationContextPostListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextPostListener.class);

    public final static String Application_Configuation_Value_Key = "cfg";

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.debug("Invoke ApplicationContextPostListener contextInitialized");
        try {
            ApplicationContext applicationContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());

            SpringContextHolder.setApplicationContext(applicationContext);

            ServletContext sc = event.getServletContext();
            String appName = sc.getServletContextName();
            logger.info("[{}] init context ...", appName);

            //构建版本
            sc.setAttribute("build_version", Boolean.valueOf(DynamicConfigService.getBuildVersion()));

            DynamicConfigService dynamicConfigService = SpringContextHolder.getBean(DynamicConfigService.class);
            Map<String, Object> globalCfg = Maps.newHashMap();
            sc.setAttribute(Application_Configuation_Value_Key, globalCfg);
            //系统标题
            globalCfg.put("cfg_system_title", dynamicConfigService.getString("cfg_system_title"));
            //开发模式boolean参数
            globalCfg.put("dev_mode", Boolean.valueOf(dynamicConfigService.getString("dev_mode")));

            Map<String, Object> globalConstant = Maps.newHashMap();
            sc.setAttribute("cons", globalConstant);
            globalConstant.put("booleanLabelMap", GlobalConstant.booleanLabelMap);
        } catch (Exception e) {
            logger.error("error detail:", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        logger.debug("Invoke ApplicationContextPostListener contextDestroyed");
    }
}
