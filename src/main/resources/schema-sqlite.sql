-- ============================================================
-- SQLite 数据库初始化脚本
-- 由 Spring Boot 首次启动时自动执行（initialization-mode: always）
-- ============================================================

-- ---------- 无人机信息表 ----------
-- 16 个字段，uav_code 唯一约束，deleted 逻辑删除标记
-- CREATE TABLE IF NOT EXISTS 确保重复执行不会报错
CREATE TABLE IF NOT EXISTS t_uav (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    uav_code        VARCHAR(64)  NOT NULL UNIQUE,
    model           VARCHAR(100) NOT NULL,
    manufacturer    VARCHAR(100),
    max_payload     REAL,
    max_altitude    INTEGER,
    max_flight_time INTEGER,
    max_speed       REAL,
    wingspan        REAL,
    weight          REAL,
    status          INTEGER      NOT NULL DEFAULT 1,
    remark          VARCHAR(500),
    ai_generated    INTEGER      NOT NULL DEFAULT 0,
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    updated_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted         INTEGER      NOT NULL DEFAULT 0
);

-- 系统用户表，password 字段扩展至 256 字符存放 BCrypt 哈希
-- 首次启动时 PasswordMigrationRunner 会自动将明文密码升级为 BCrypt 哈希
CREATE TABLE IF NOT EXISTS t_user (
    id       INTEGER PRIMARY KEY AUTOINCREMENT,
    username VARCHAR(64)  NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL,
    role     VARCHAR(32)  NOT NULL DEFAULT 'admin'
);
-- 初始密码为 admin123 的 BCrypt 哈希（Salt=10，成本因子 10）
INSERT OR IGNORE INTO t_user (username, password, role)
VALUES ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin');
