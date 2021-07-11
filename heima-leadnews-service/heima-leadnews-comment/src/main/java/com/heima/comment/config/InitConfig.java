package com.heima.comment.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.exception","com.heima.common.config.knife4j","com.heima.common.aliyun"})
public class InitConfig {
}
