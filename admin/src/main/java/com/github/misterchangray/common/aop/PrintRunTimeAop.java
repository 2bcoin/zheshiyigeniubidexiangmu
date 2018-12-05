package com.github.misterchangray.common.aop;

import com.github.misterchangray.common.utils.DateUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 自定义注解
 * 统计并打印函数执行时间
 *
 * @author Rui.Zhang/misterchangray@hotmail.com
 * @author Created on 3/22/2018.
 */
@Component
@Aspect
public class PrintRunTimeAop {

    @Pointcut(value = "@annotation(com.github.misterchangray.common.annotation.PrintRunTime)")
    private void pointcut() {}

    @Around(value = "pointcut() && @annotation(com.github.misterchangray.common.annotation.PrintRunTime)")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        Long time, time2;
        Object res;
        time = new Date().getTime();
        res = point.proceed();

        time2 = new Date().getTime();
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();

        System.out.println("[" + DateUtils.dateToStr(new Date()) + "] " + method + "- has spend " + ((time2 - time) / 1000) + "s");

        return  res;
    }

}
