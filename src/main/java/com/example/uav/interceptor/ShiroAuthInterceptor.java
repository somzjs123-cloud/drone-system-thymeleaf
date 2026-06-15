package com.example.uav.interceptor;

import com.example.uav.common.R;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.nio.charset.StandardCharsets;

/**
 * Shiro 认证补充拦截器，在 Spring MVC 层面校验用户登录态。
 * 对于 /api/** 路径的未登录请求返回 JSON 格式的 401 响应，
 * 对于页面路径的未登录请求重定向到 /login。
 */
@Slf4j
@Component
public class ShiroAuthInterceptor implements HandlerInterceptor {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return true;
        }

        String requestURI = request.getRequestURI();
        if (requestURI.startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            OBJECT_MAPPER.writeValue(response.getWriter(),
                    R.fail(401, "请先登录"));
            return false;
        }

        response.sendRedirect("/login");
        return false;
    }
}
