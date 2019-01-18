package com.fankun.webmvc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ThymeleafController {

    // http://localhost:8080/thymeleaf/index.do
    @RequestMapping("/thymeleaf/index.do")
    public String index(){
        return "index";
    }
}
