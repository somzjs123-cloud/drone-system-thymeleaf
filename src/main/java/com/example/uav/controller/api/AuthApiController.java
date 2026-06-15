package com.example.uav.controller.api;

import com.example.uav.common.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

/**
 * 认证 REST API 控制器，提供登录/登出/认证检查接口，供 Vue 前端以 JSON 方式完成认证流程。
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    @PostMapping("/login")
    public R<Map<String, String>> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || username.trim().isEmpty()) {
            return R.fail(400, "用户名不能为空");
        }
        if (password == null || password.trim().isEmpty()) {
            return R.fail(400, "密码不能为空");
        }
        try {
            Subject subject = SecurityUtils.getSubject();
            subject.login(new UsernamePasswordToken(username, password));
            log.info("用户 {} 登录成功", username);
            return R.ok("登录成功", Collections.singletonMap("username", username));
        } catch (AuthenticationException e) {
            log.warn("用户 {} 登录失败：{}", username, e.getMessage());
            return R.fail(401, "用户名或密码错误");
        }
    }

    @PostMapping("/logout")
    public R<Void> logout() {
        SecurityUtils.getSubject().logout();
        return R.ok("退出成功", null);
    }

    @GetMapping("/check")
    public R<Void> check() {
        if (SecurityUtils.getSubject().isAuthenticated()) {
            return R.ok();
        }
        return R.fail(401, "未登录");
    }
}
