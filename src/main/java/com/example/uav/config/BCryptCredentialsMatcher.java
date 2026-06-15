package com.example.uav.config;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.mindrot.jbcrypt.BCrypt;

/**
 * BCrypt 凭证匹配器，使用 jBCrypt 库验证登录密码。
 *
 * <p>将用户输入的明文密码与 t_user 表中存储的 BCrypt 哈希值进行比较。
 * 安全优势：数据库不存储明文密码；BCrypt 自带 Salt 且计算成本可调（Salt=10）。</p>
 */
public class BCryptCredentialsMatcher implements CredentialsMatcher {

    /**
     * 验证用户提交的密码是否与数据库中的 BCrypt 哈希匹配。
     *
     * @param token 用户提交的认证令牌（包含明文密码）
     * @param info  从数据库查询到的认证信息（包含 BCrypt 哈希）
     * @return true 表示密码匹配，false 表示不匹配
     */
    @Override
    public boolean doCredentialsMatch(AuthenticationToken token, AuthenticationInfo info) {
        if (!(token instanceof UsernamePasswordToken)) {
            return false;
        }

        UsernamePasswordToken upToken = (UsernamePasswordToken) token;
        String submittedPassword = new String(upToken.getPassword());

        // info.getCredentials() 返回的是 Realm 中 SimpleAuthenticationInfo 存入的 BCrypt 哈希
        String storedHash = info.getCredentials().toString();

        return BCrypt.checkpw(submittedPassword, storedHash);
    }
}
