package com.fankun.rest.config;

import com.fankun.rest.http.message.PerpertiesPersonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {



    /**
     * 添加自己的mediaType
     * @param converters
     */
//    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
//        System.out.println("converters不为空，所有的默认的都已经加载进来了:"+converters);
//        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
//        converters.add(new MappingJackson2HttpMessageConverter(builder.build()));
//        System.out.println("");
//    }

    /**
     * 添加自己的mediaType,把xml默认添加到第一个
     * @param converters
     */
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        System.out.println("converters不为空，所有的默认的都已经加载进来了:"+converters);
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        converters.add(0,new MappingJackson2XmlHttpMessageConverter());
        converters.add(0,new MappingJackson2HttpMessageConverter(builder.build()));
        converters.add(new PerpertiesPersonHttpMessageConverter());
        System.out.println("");
    }
}
