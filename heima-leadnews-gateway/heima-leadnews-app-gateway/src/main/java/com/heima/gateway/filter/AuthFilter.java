package com.heima.gateway.filter;

import com.heima.gateway.util.AppJwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {
    /**
     * 过滤器执行逻辑
     *
     * @param exchange
     * @param chain
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 需求分析
        // 1. 判断请求地址是否需要鉴权  如果请求地址中包含 login 直接放行
        // 获取请求对象和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        if (request.getURI().getPath().contains("/login")) {
            // 请求地址中包含 login 直接放行
            return chain.filter(exchange);
        }
        // 2. 判断请求头中是否包含token
        // 获取token
        String token = request.getHeaders().getFirst("token");
        if (StringUtils.isEmpty(token)) {
            // 返回401 未授权状态  结束响应
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 3. 校验token
        // 使用JWT校验
        try {
            // 解析token
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            int result = AppJwtUtil.verifyToken(claimsBody);
            if (result == 0 || result == -1) {
                // 4. 校验通过 将UserId 放入到请求头中,转发给后端的微服务
                // 获取申明中的userId
                Object userId = claimsBody.get("id");
                // 将UserId 放入到请求头中
                request.mutate().header("userId", userId.toString());
                return chain.filter(exchange);
            }
        } catch (Exception e) {
            // 记录错误日志
            log.error(e.getMessage());

        }
        // 返回401 未授权状态  结束响应
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return response.setComplete();
    }

    /**
     * 过滤器执行的顺序 数值越小 优先级越高
     *
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
