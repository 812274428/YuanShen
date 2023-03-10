package com.xw.test;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "com.xw")
@MapperScan(basePackages = "com.xw.mapper")
public class UpdateApplication{
	
	public static void main(String[] args) {
		SpringApplication.run(UpdateApplication.class, args);
		
	}
}
