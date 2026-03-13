package com.huodongpai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.huodongpai.mapper")
@SpringBootApplication
public class HuodongpaiApplication {

    /**
     * Spring Boot 应用启动入口。
     * 当前项目为单体后端应用，所有 Web、MyBatis-Plus、Redis 等能力都从这里统一启动。
     */
    public static void main(String[] args) {
        SpringApplication.run(HuodongpaiApplication.class, args);
    }
}
