# 无人机信息管理系统

基于 Spring Boot + Thymeleaf 的前后端不分离无人机信息管理平台，支持无人机信息的增删改查、AI 属性智能生成和双数据库无缝切换。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 2.2.13 |
| 模板引擎 | Thymeleaf | — |
| 安全认证 | Apache Shiro | 1.7.1 |
| ORM | MyBatis | 2.1.4 |
| 分页 | PageHelper | 1.3.1 |
| 连接池 | Druid | 1.2.8 |
| 数据库 | SQLite / MySQL | — |
| 前端 UI | Bootstrap 4.6 | CDN |

## 功能模块

- **用户认证** — Shiro IniRealm 登录/登出，Session 超时 30 分钟
- **无人机列表** — 分页展示、型号/编号/状态组合搜索、逻辑删除、AI 标记徽章
- **新增无人机** — 表单校验、注册编号实时唯一性校验、AI 智能填充
- **编辑无人机** — 表单回填、注册编号只读、状态下拉切换
- **AI 属性生成** — 三档等级（消费/工业/军用）+ 15 家厂商随机 + 关键词智能推断
- **双数据库** — 默认 SQLite 零配置启动，Profile 切换 MySQL
- **Druid 监控** — 内置 SQL 监控面板 `/druid/`
- **全局异常处理** — 覆盖 9 种异常类型，统一 JSON 响应

## 快速开始

### 环境要求

- JDK 8+
- Maven 3.x

### 启动（SQLite 默认）

```bash
git clone https://github.com/somzjs123-cloud/drone-system-thymeleaf.git
cd drone-system-thymeleaf
mvn spring-boot:run
```

浏览器访问 `http://localhost:8080/login`

**默认账号**：`admin` / `admin123`

### 切换 MySQL

1. 创建数据库：
```sql
CREATE DATABASE uav_db DEFAULT CHARACTER SET utf8mb4;
```

2. 执行 `src/main/resources/schema-mysql.sql` 建表

3. 修改 `src/main/resources/application.yml`：
```yaml
spring:
  profiles:
    active: mysql
```

## 项目结构

```
drone-system-thymeleaf/
├── pom.xml
├── src/main/java/com/example/uav/
│   ├── UavManagementApplication.java    # 启动入口
│   ├── common/                          # 通用工具
│   │   ├── Constants.java               # 全局常量
│   │   ├── PageResult.java              # 分页封装
│   │   └── R.java                       # 统一响应体
│   ├── config/                          # 配置层
│   │   ├── ShiroConfig.java             # Shiro 安全配置
│   │   ├── MybatisConfig.java           # MyBatis + Druid 配置
│   │   ├── WebMvcConfig.java            # 拦截器注册
│   │   ├── BCryptCredentialsMatcher.java # BCrypt 密码验证器
│   │   └── typehandler/                 # 自定义类型处理器
│   ├── controller/                      # 控制器层
│   │   ├── UavController.java           # Thymeleaf 视图控制器
│   │   ├── AiGenerateController.java    # AI 生成 API
│   │   └── api/                         # REST API
│   ├── service/                         # 服务层
│   │   ├── UavService.java
│   │   ├── AiAttributeService.java
│   │   └── impl/
│   ├── dao/                             # 数据访问层
│   ├── mapper/                          # MyBatis Mapper
│   ├── domain/                          # 领域模型
│   │   ├── entity/                      # 数据库实体
│   │   ├── dto/                         # 数据传输对象
│   │   └── query/                       # 查询条件
│   ├── exception/                       # 异常处理
│   └── interceptor/                     # 拦截器
├── src/main/resources/
│   ├── templates/                       # Thymeleaf 模板
│   │   ├── login.html                   # 登录页
│   │   ├── list.html                    # 列表页
│   │   ├── add.html                     # 新增页
│   │   ├── edit.html                    # 编辑页
│   │   └── 403.html                     # 无权限页
│   ├── application.yml                  # 主配置
│   ├── application-sqlite.yml           # SQLite 数据源
│   ├── application-mysql.yml            # MySQL 数据源
│   ├── schema-sqlite.sql                # SQLite 建表脚本
│   └── schema-mysql.sql                 # MySQL 建表脚本
└── src/test/                            # 测试代码
    └── java/com/example/uav/
        ├── controller/
        ├── mapper/
        └── service/impl/
```

## 数据库设计

### t_uav — 无人机信息表

| 列名 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键，自增 |
| uav_code | VARCHAR(64) | 注册编号，唯一 |
| model | VARCHAR(100) | 型号 |
| manufacturer | VARCHAR(100) | 制造商 |
| max_payload | DOUBLE | 最大载重(kg) |
| max_altitude | INT | 最大高度(m) |
| max_flight_time | INT | 续航(min) |
| max_speed | DOUBLE | 最大速度(m/s) |
| wingspan | DOUBLE | 翼展(cm) |
| weight | DOUBLE | 自重(kg) |
| status | TINYINT | 1=正常, 0=停用 |
| remark | VARCHAR(500) | 备注 |
| ai_generated | TINYINT | AI 生成标记 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |
| deleted | TINYINT | 逻辑删除标记 |

### t_user — 系统用户表

| 列名 | 类型 | 说明 |
|------|------|------|
| id | BIGINT | 主键 |
| username | VARCHAR(64) | 用户名 |
| password | VARCHAR(256) | 密码（BCrypt 哈希） |
| role | VARCHAR(32) | 角色 |

## AI 属性等级

| 等级 | 载重(kg) | 高度(m) | 续航(min) | 速度(m/s) |
|------|----------|---------|-----------|-----------|
| 消费级 | 0.5–5 | 100–3000 | 20–60 | 8–25 |
| 工业级 | 5–30 | 2000–6000 | 40–120 | 15–50 |
| 军用级 | 30–200 | 5000–20000 | 120–480 | 50–300 |

## 相关项目

- [drone-system](https://github.com/somzjs123-cloud/drone-system) — Vue 3 前后端分离版本
