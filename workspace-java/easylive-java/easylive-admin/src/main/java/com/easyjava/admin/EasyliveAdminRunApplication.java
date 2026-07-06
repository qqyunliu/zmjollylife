package com.easyjava.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.easyjava"})
@MapperScan("com.easyjava.mappers")
@EnableTransactionManagement
@EnableScheduling
public class EasyliveAdminRunApplication {
    public static void main(String[] args) {
        SpringApplication.run(EasyliveAdminRunApplication.class,args);
    }
}
