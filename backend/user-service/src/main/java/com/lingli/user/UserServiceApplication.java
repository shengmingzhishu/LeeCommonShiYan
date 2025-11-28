package com.lingli.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 用户服务启动类
 *
 * @author lingli
 * @since 2023-11-28
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.lingli.user.mapper")
public class UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
        System.out.println("用户服务启动成功！");
    }
}