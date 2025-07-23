package com.mybank;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MyBankApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyBankApplication.class, args);
    }
} 