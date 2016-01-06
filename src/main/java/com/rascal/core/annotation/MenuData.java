package com.rascal.core.annotation;

import java.lang.annotation.*;

/**
 * 用于注解标识当前Controller方法生成菜单数据的元数据
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface MenuData {

    /**
     * 菜单路径
     */
    String[] value();

    /**
     * 注释说明：用于描述代码内部用法说明
     */
    String comments() default "";
}
