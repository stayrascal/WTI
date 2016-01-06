package com.rascal.core.web.annotation;

import java.lang.annotation.*;

/**
 * 标识忽略访问权限控制
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface SecurityControlIgnore {

}
