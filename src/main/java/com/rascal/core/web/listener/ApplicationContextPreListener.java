package com.rascal.core.web.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * Spring容器加载“之前”的ServletContextListener
 */
public class ApplicationContextPreListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(ApplicationContextPreListener.class);

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //强制设置优先采用IPV4协议
        logger.info("Set system property: {} = {}", "java.net.preferIPv4Stack", "true");
        System.setProperty("java.net.preferIPv4Stack", "true");

        logger.debug("Invoke ApplicationContextPreListener contextInitialized");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.debug("Invoke ApplicationContextPreListener contextDestroyed");
    }

}
