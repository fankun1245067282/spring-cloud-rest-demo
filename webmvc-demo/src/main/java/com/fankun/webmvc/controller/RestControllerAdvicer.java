package com.fankun.webmvc.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice(basePackages = "com.fankun.webmvc.controller")
public class RestControllerAdvicer {

    @ExceptionHandler(NullPointerException.class)//要处理的异常
    @ResponseBody
    public Object npe(HttpServletRequest request, Throwable throwable){
        return throwable.getMessage();
    }

    @ExceptionHandler(value = {NoHandlerFoundException.class})//要处理的异常
    @ResponseBody
    public Object pageNotPage(HttpServletRequest request, Throwable throwable){
        Map<String, Object> errors = new HashMap<>();
        //没有获取到信息，好像request的信息都被删除了
        errors.put("status_code",request.getAttribute("javax.servlet.error.status_code"));
        errors.put("request_uri",request.getAttribute("javax.servlet.error.request_uri"));
//        javax.servlet.error.status_code                 java.lang.Integer
//        javax.servlet.error.exception_type           java.lang.Class
//        javax.servlet.error.message                      java.lang.String
//        javax.servlet.error.exception                    java.lang.Throwable
//        javax.servlet.error.request_uri                 java.lang.String
//        javax.servlet.error.servlet_name              java.lang.String
        return errors;
    }

}
