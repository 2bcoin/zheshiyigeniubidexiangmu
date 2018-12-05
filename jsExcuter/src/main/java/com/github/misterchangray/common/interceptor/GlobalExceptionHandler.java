package com.github.misterchangray.common.interceptor;

import com.github.misterchangray.common.ResultSet;
import com.github.misterchangray.common.enums.ResultEnum;
import com.github.misterchangray.common.exception.ServiceException;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;

/**
 * 提供全局统一异常处理
 * @author Created by rui.zhang on 2018/6/4.
 * @author rui.zhang
 * @version ver1.0
 * @email misterchangray@hotmail.com
 * @description
 * 注意：
 * 如果你使用异常方式返回信息;请不要将错误信息try-catch进行处理;应该逐级上抛并统一到此处处理
 */
@ControllerAdvice()
public class GlobalExceptionHandler {
    org.slf4j.Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResultSet serviceExceptionHandler(Exception ex, HttpServletResponse httpServletResponse) {
        //允许跨域访问
        httpServletResponse.setHeader("Access-Control-Allow-Origin","*");

        //对捕获的异常进行处理并打印日志等，之后返回json数据，方式与Controller相同
        ex.printStackTrace();
        ServiceException serviceException = null;

        //如果抛出的是系统自定义的异常则直接转换
        if(ex instanceof ServiceException) {
            serviceException = (ServiceException) ex;
            logger.info(ex.getMessage(), ex);
        } else {
            //如果抛出的不是系统自定义的异常则重新构造一个未知错误异常
            serviceException = new ServiceException(ResultEnum.SERVER_ERROR);
            logger.error(ex.getMessage(), ex);

        }

        ResultSet resultSet = ResultSet.build(serviceException.getResultEnum());
        if(null != serviceException.getMsg()) resultSet.setMsg(serviceException.getMsg());

        return resultSet;
    }


}