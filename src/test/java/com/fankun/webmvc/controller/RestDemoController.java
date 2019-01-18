package com.fankun.webmvc.controller;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController//==@Controller+@ResponseBody
public class RestDemoController {
    //测试URL:http://localhost:8080/hello
    @GetMapping("/hello")
    public String index(){
        return "hello,world";
    }

    /**
     * 处理页面找不到的情况
     * @return
     */
    @GetMapping("/404.html")
    public Map<String, Object> handlerPageNotFond(HttpServletRequest request){
        Map<String, Object> errors = new HashMap<>();
        errors.put("status_code",request.getAttribute("javax.servlet.error.status_code"));
        errors.put("request_uri",request.getAttribute("javax.servlet.error.request_uri"));
        return errors;
    }

    /**
     * 处理页面找不到的情况
     * @return
     */
    @GetMapping("/npe")
    public Object npe(HttpServletRequest request){
        throw new NullPointerException("故意抛出空指针");
    }
}
