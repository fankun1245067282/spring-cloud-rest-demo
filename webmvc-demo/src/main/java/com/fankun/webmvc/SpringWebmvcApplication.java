package com.fankun.webmvc;

import com.fankun.webmvc.interceptor.DefaultHandlerInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class SpringWebmvcApplication extends WebMvcConfigurerAdapter implements
        ErrorPageRegistrar{

    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new DefaultHandlerInterceptor());
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringWebmvcApplication.class,args);
    }


    /**
     * spring boot 异常处理器
     * @param registry
     */
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
//        registry.addErrorPages(new ErrorPage(HttpStatus.NOT_FOUND,"/404.html"));
    }
}
