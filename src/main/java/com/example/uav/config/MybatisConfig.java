package com.example.uav.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * MyBatis + Druid 数据源配置（合并原 MybatisConfig 和 DruidConfig）。
 */
@Configuration
@org.mybatis.spring.annotation.MapperScan("com.example.uav.mapper")
public class MybatisConfig {

    /** Druid 连接池，参数由 yml 注入 */
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

    /** 多数据库方言识别 */
    @Bean
    public org.apache.ibatis.mapping.DatabaseIdProvider databaseIdProvider() {
        org.apache.ibatis.mapping.VendorDatabaseIdProvider provider =
                new org.apache.ibatis.mapping.VendorDatabaseIdProvider();
        java.util.Properties props = new java.util.Properties();
        props.setProperty("MySQL", "mysql");
        props.setProperty("SQLite", "sqlite");
        props.setProperty("H2", "h2");
        provider.setProperties(props);
        return provider;
    }
}
