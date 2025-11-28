package com.lingli.product;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 商品服务启动类
 *
 * @author lingli
 * @since 2023-11-28
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.lingli.product.mapper")
public class ProductServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
        System.out.println("商品服务启动成功！");
    }
}