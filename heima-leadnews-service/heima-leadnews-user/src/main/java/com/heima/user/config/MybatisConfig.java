package com.heima.user.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.heima.user.mapper")
public class MybatisConfig {

    @Bean // 支持分页
    public PaginationInterceptor paginationInterceptor(){
        return new PaginationInterceptor();
    }
}
