package com.pet.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 宠知 AI - 后端启动类
 */
@SpringBootApplication
@MapperScan("com.pet.platform.mapper")
public class PetPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetPlatformApplication.class, args);
    }
}
