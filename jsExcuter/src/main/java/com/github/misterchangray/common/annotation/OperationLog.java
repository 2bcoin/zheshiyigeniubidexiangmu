package com.github.misterchangray.common.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 自定义注解
 * 为此注解得service方法增加操作日志
 * 注意此注解只用于service方法中，简易在service实现类中增删改方法添加此注解
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 4/26/2018.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Component
public @interface OperationLog {
    String businessName();
}