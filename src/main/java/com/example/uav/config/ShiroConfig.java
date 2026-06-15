package com.example.uav.config;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Apache Shiro 安全配置 — URL 过滤器链 + Session 管理。
 */
@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager) {
        ShiroFilterFactoryBean factoryBean = new ShiroFilterFactoryBean();
        factoryBean.setSecurityManager(securityManager);
        factoryBean.setLoginUrl("/login");
        factoryBean.setSuccessUrl("/uav/list");
        factoryBean.setUnauthorizedUrl("/403");

        Map<String, String> filterChainMap = new LinkedHashMap<>();
        filterChainMap.put("/static/**", "anon");
        filterChainMap.put("/css/**", "anon");
        filterChainMap.put("/js/**", "anon");
        filterChainMap.put("/images/**", "anon");
        filterChainMap.put("/favicon.ico", "anon");
        filterChainMap.put("/druid/**", "anon");
        filterChainMap.put("/api/auth/login", "anon");
        filterChainMap.put("/api/auth/check", "anon");
        filterChainMap.put("/api/auth/logout", "anon");
        filterChainMap.put("/api/**", "authc");
        filterChainMap.put("/logout", "logout");
        filterChainMap.put("/**", "anon");

        factoryBean.setFilterChainDefinitionMap(filterChainMap);
        return factoryBean;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(Realm realm) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(realm);
        manager.setSessionManager(sessionManager());
        return manager;
    }

    /** 简单的 IniRealm：admin/admin123，无需 UserMapper */
    @Bean
    public Realm iniRealm() {
        IniRealm realm = new IniRealm();
        realm.setResourcePath("classpath:shiro-users.ini");
        realm.setCachingEnabled(false);
        return realm;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sm = new DefaultWebSessionManager();
        sm.setGlobalSessionTimeout(30 * 60 * 1000L);
        sm.setDeleteInvalidSessions(true);
        return sm;
    }
}
