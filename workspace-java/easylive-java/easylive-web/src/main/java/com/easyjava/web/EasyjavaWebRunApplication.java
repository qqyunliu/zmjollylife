package com.easyjava.web;

import org.mybatis.spring.annotation.MapperScan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.easyjava"})
@MapperScan("com.easyjava.mappers")
@ComponentScan("com.easyjava")
@EnableTransactionManagement
public class EasyjavaWebRunApplication {

    public static void main(String[] args) {
        SpringApplication.run(EasyjavaWebRunApplication.class, args);
    }


}

