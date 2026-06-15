package com.example.uav.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 系统用户实体，对应 t_user 表。
 *
 * <p>用于 Shiro Realm 从数据库验证登录凭证，替代原先硬编码的 admin/admin123。
 * 包含 4 个字段：id、username、password、role。</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /** 主键 */
    private Long id;

    /** 用户名（登录账号） */
    private String username;

    /** 密码（明文存储，生产环境应使用 BCrypt 加密） */
    private String password;

    /** 角色标识（admin / user） */
    private String role;
}
