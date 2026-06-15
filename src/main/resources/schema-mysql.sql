-- MySQL 数据库初始化脚本
-- 使用 application-mysql.yml 前，请先创建 uav_db 并执行本脚本
CREATE TABLE IF NOT EXISTS `t_uav` (
    `id`              BIGINT        NOT NULL AUTO_INCREMENT COMMENT '主键',
    `uav_code`        VARCHAR(64)   NOT NULL COMMENT '注册编号',
    `model`           VARCHAR(100)  NOT NULL COMMENT '型号',
    `manufacturer`    VARCHAR(100)  DEFAULT NULL COMMENT '制造商',
    `max_payload`     DOUBLE        DEFAULT NULL COMMENT '最大载重(kg)',
    `max_altitude`    INT           DEFAULT NULL COMMENT '最大飞行高度(m)',
    `max_flight_time` INT           DEFAULT NULL COMMENT '最大续航(min)',
    `max_speed`       DOUBLE        DEFAULT NULL COMMENT '最大速度(m/s)',
    `wingspan`        DOUBLE        DEFAULT NULL COMMENT '翼展(cm)',
    `weight`          DOUBLE        DEFAULT NULL COMMENT '自重(kg)',
    `status`          TINYINT       NOT NULL DEFAULT 1 COMMENT '状态:1正常,0停用',
    `remark`          VARCHAR(500)  DEFAULT NULL COMMENT '备注',
    `ai_generated`    TINYINT       NOT NULL DEFAULT 0 COMMENT '0手动,1AI生成',
    `created_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at`      DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted`         TINYINT       NOT NULL DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_uav_code` (`uav_code`),
    KEY `idx_model` (`model`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='无人机信息表';

-- ---------- 系统用户表 ----------
-- 用于 Shiro Realm 从数据库验证登录凭证
-- 系统用户表密码扩展至 256 字符（BCrypt 哈希最长约 60 字符，留足余地）
CREATE TABLE IF NOT EXISTS `t_user` (
    `id`       BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` VARCHAR(64)  NOT NULL COMMENT '用户名',
    `password` VARCHAR(256) NOT NULL COMMENT 'BCrypt 哈希密码',
    `role`     VARCHAR(32)  NOT NULL DEFAULT 'admin' COMMENT '角色',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统用户表';

-- admin123 的 BCrypt 哈希（Salt=10），首次启动 PasswordMigrationRunner 会自动升级旧明文
-- 若为全新部署，直接用哈希值；若从旧版升级，明文密码会被自动哈希
INSERT IGNORE INTO `t_user` (`username`, `password`, `role`)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin');
