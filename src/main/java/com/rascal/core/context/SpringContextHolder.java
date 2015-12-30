package com.rascal.core.context;

import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;

/**
 * Spring上下文容器
 * <p>
 * Date: 2015/11/28
 * Time: 23:55
 *
 * @author Rascal
 */
public class SpringContextHolder {

    private static ApplicationContext applicationContext;

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext applicationContext) {
        SpringContextHolder.applicationContext = applicationContext;
    }

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        Assert.notNull(applicationContext);
        return applicationContext.getBean(requiredType);
    }
}
