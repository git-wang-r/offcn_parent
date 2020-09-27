package com.offcn.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.offcn.project.mapper")
public class ProjectStartApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProjectStartApplication.class);
    }
}
