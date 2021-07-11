package com.heima.user.config;


import com.heima.user.filter.UserFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    @Autowired
    private UserFilter userFilter;

    @Bean
    public FilterRegistrationBean filterRegistrationBean() {
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
        // 指定使用过滤器
        filterRegistrationBean.setFilter(userFilter);
        // 指定满足过滤的路径 所有路径都过滤
        filterRegistrationBean.addUrlPatterns("/*");
        return filterRegistrationBean;
    }
}
