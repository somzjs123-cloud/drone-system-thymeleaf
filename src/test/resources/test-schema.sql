-- H2 测试数据库 Schema（用于 @MybatisTest）
CREATE TABLE IF NOT EXISTS t_uav (
    id              BIGINT       AUTO_INCREMENT PRIMARY KEY,
    uav_code        VARCHAR(64)  NOT NULL UNIQUE,
    model           VARCHAR(100) NOT NULL,
    manufacturer    VARCHAR(100),
    max_payload     DOUBLE,
    max_altitude    INT,
    max_flight_time INT,
    max_speed       DOUBLE,
    wingspan        DOUBLE,
    weight          DOUBLE,
    status          TINYINT      NOT NULL DEFAULT 1,
    remark          VARCHAR(500),
    ai_generated    TINYINT      NOT NULL DEFAULT 0,
    created_at      DATETIME,
    updated_at      DATETIME,
    deleted         TINYINT      NOT NULL DEFAULT 0
);

-- 系统用户表（供 UserMapper 测试）
CREATE TABLE IF NOT EXISTS t_user (
    id       BIGINT      AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    role     VARCHAR(32) NOT NULL DEFAULT 'admin'
);
INSERT INTO t_user (username, password, role) VALUES ('admin', 'admin123', 'admin');
