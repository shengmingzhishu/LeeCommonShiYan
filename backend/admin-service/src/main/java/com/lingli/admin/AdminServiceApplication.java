package com.lingli.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 管理后台服务启动类
 *
 * @author lingli
 * @since 2023-11-28
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.lingli.admin.mapper")
public class AdminServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
        System.out.println("管理后台服务启动成功！");
    }
}