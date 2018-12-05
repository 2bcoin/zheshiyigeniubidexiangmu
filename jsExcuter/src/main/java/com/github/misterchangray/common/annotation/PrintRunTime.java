package com.github.misterchangray.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;


/**
 * 自定义注解
 * 在需要打印方法执行时间得函数上使用此注解
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/22/2018.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface PrintRunTime {
    String value() default "";
}
