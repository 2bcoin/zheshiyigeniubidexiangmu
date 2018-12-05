package com.github.misterchangray.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


/**
 * 自定义注解
 * 在需要进行权限认证的方法或类上使用此注解
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 2018/4/23.
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface Authorization {
    String value() default "";
}
