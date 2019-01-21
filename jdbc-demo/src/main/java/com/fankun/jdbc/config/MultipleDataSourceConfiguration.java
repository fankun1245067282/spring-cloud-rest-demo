package com.fankun.jdbc.config;

import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class MultipleDataSourceConfiguration {

    @Bean
    @Primary
    public DataSource masterDataSource(){
        DataSource dataSource = null;
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSource = dataSourceBuilder
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=UTF-8")
                .username("root")
                .password("123456")
                .build();
        return dataSource;
    }

    @Bean
    public DataSource slaveDataSource(){
        DataSource dataSource = null;
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
        dataSource = dataSourceBuilder
                .driverClassName("com.mysql.jdbc.Driver")
                .url("jdbc:mysql://localhost:3306/test2?useUnicode=true&characterEncoding=UTF-8")
                .username("root")
                .password("123456")
                .build();
        return dataSource;
    }
}
