package com.offcn.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
@MapperScan("com.offcn.user.mapper")/*扫描注解*/
public class UserStartApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserStartApplication.class, args);
	}

}
