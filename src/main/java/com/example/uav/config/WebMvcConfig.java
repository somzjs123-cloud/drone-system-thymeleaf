package com.example.uav.config;

import com.example.uav.interceptor.RequestIdInterceptor;
import com.example.uav.interceptor.RequestLoggingInterceptor;
import com.example.uav.interceptor.ShiroAuthInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC 配置，注册请求 ID、请求日志和 Shiro 认证三个拦截器，配置静态资源映射。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /** 所有拦截器共用的排除路径（静态资源 + Druid 监控） */
    private static final String[] COMMON_EXCLUDES = {
            "/static/**", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/druid/**"
    };

    /** Shiro 认证拦截器排除的路径（登录/检查/登出无需认证） */
    private static final String[] AUTH_EXCLUDES = {
            "/api/auth/login", "/api/auth/check", "/api/auth/logout"
    };

    private final RequestIdInterceptor requestIdInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final ShiroAuthInterceptor shiroAuthInterceptor;

    @Autowired
    public WebMvcConfig(RequestIdInterceptor requestIdInterceptor,
                        RequestLoggingInterceptor requestLoggingInterceptor,
                        ShiroAuthInterceptor shiroAuthInterceptor) {
        this.requestIdInterceptor = requestIdInterceptor;
        this.requestLoggingInterceptor = requestLoggingInterceptor;
        this.shiroAuthInterceptor = shiroAuthInterceptor;
    }

    /**
     * 注册拦截器，排除静态资源和 Druid 监控。
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestIdInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(COMMON_EXCLUDES);
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(COMMON_EXCLUDES);
        registry.addInterceptor(shiroAuthInterceptor)
                .addPathPatterns("/uav/**", "/api/**")
                .excludePathPatterns(AUTH_EXCLUDES);
    }

    /**
     * 配置静态资源映射。
     *
     * @param registry 静态资源注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
