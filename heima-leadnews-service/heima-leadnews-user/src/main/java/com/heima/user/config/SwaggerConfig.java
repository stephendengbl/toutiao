package com.heima.user.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
// @ComponentScan("com.heima.common.config.swagger")   // 指定包扫描
@ComponentScan("com.heima.common.config.knife4j")   // 指定包扫描
public class SwaggerConfig {
}
