package com.heima.search.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"com.heima.common.exception","com.heima.common.config.knife4j"})
public class InitConfig {
}
