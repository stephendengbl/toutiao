package com.heima.search.filter;

import com.heima.common.dto.User;
import com.heima.common.util.AppThreadLocalUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Service
public class UserFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // 需求 从请求头中解析用户id
        // 获取请求对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // 获取请求头
        String userId = request.getHeader("userId");
        if (!StringUtils.isEmpty(userId) && !userId.equals("0")) {
            // 将用户id放入到本地线程中
            User user = new User();
            user.setUserId(Integer.parseInt(userId));
            AppThreadLocalUtil.set(user);
        }
        // 放行请求
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
