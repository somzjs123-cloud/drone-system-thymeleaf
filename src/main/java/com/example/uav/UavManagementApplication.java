package com.example.uav;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 无人机信息管理系统启动类，Spring Boot 应用入口，自动扫描 com.example.uav 包下的组件。
 */
@SpringBootApplication
public class UavManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(UavManagementApplication.class, args);
    }
}
