package com.huodongpai;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.huodongpai.mapper")
@SpringBootApplication
public class HuodongpaiApplication {

    public static void main(String[] args) {
        SpringApplication.run(HuodongpaiApplication.class, args);
    }
}
