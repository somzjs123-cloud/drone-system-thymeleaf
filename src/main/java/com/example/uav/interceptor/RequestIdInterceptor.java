package com.example.uav.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * 请求 ID 拦截器，为每个 HTTP 请求生成或透传唯一追踪 ID。
 *
 * <p>在请求到达 Controller 之前（{@code preHandle}）：
 * <ol>
 *   <li>读取请求头 {@code X-Request-Id}——若上游（前端/网关）已携带则沿用，否则生成 UUID</li>
 *   <li>将 ID 存入 request 属性（{@code REQUEST_ID}），供当前链路任意位置取出</li>
 *   <li>将 ID 写回响应头，确保调用方也能获取</li>
 *   <li>输出 debug 日志，便于按 ID 串联所有相关日志</li>
 * </ol>
 *
 * <p>配合 {@code WebMvcConfigurer.addInterceptors()} 注册后生效。
 */
@Slf4j
@Component
public class RequestIdInterceptor implements HandlerInterceptor {

    /** 请求头/响应头名称 */
    public static final String HEADER_NAME = "X-Request-Id";

    /** request 属性键，用于在当前请求链路中传递 ID */
    private static final String ATTR = "REQUEST_ID";

    /**
     * 请求处理前：获取或生成追踪 ID，绑定到请求与响应。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @return 始终返回 {@code true}，不拦截任何请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 优先使用上游传入的 ID，不存在则生成新的 UUID
        String existing = request.getHeader(HEADER_NAME);
        String rid = (existing != null && !existing.isEmpty())
                ? existing.trim()
                : UUID.randomUUID().toString();

        // 绑定到 request 属性，供当前链路使用
        request.setAttribute(ATTR, rid);

        // 写回响应头，确保调用方能获取 ID
        response.setHeader(HEADER_NAME, rid);

        log.debug("[REQ-ID] {} {} | id={}", request.getMethod(), request.getRequestURI(), rid);
        return true;
    }
}
