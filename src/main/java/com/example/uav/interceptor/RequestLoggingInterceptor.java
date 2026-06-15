package com.example.uav.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 请求日志拦截器，记录每次请求的方法、URI、客户端 IP 和处理耗时，
 * 在请求完成后输出响应日志。在 WebMvcConfig 中注册，拦截所有业务请求。
 */
@Slf4j
@Component
public class RequestLoggingInterceptor implements HandlerInterceptor {

    /** 用于存储请求开始时间的 request 属性键 */
    private static final String START_TIME_ATTR = "REQUEST_START_TIME";

    /**
     * 请求处理前：记录开始时间，打印请求信息。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @return 始终返回 true，不拦截请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        long startTime = System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTR, startTime);
        String ip = getClientIp(request);
        String params = request.getQueryString() != null ? "?" + request.getQueryString() : "";
        log.info("[REQ] {} {} | IP={} | Params={}", request.getMethod(), request.getRequestURI() + params, ip, params);
        return true;
    }
    /**
     * 请求完成后：计算耗时并打印响应日志。
     *
     * @param request  HTTP 请求
     * @param response HTTP 响应
     * @param handler  处理器对象
     * @param ex       处理过程中的异常（如有）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTR);
        long cost = startTime != null ? System.currentTimeMillis() - startTime : -1;
        log.info("[RES] {} {} | Status={} | Cost={}ms",
                request.getMethod(), request.getRequestURI(), response.getStatus(), cost);
        if (ex != null) {
            log.error("[ERR] {} {} | Exception={}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        }
    }

    /**
     * 获取客户端真实 IP（兼容代理场景）。
     *
     * @param request HTTP 请求
     * @return 客户端 IP 地址
     */
    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 多级代理情况，取第一个 IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
