package com.fankun.jdbc.webflux;

import com.fankun.jdbc.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class WebFluxConfiguration {

    @Bean
    public RouterFunction<ServerResponse> saveUser(UserHandler userHandler){
        System.out.println("web flux =============================");
        return RouterFunctions.route(RequestPredicates.POST("/web/flux/user/save"),userHandler::save);
    }
}
