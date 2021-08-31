package com.leejohy.book.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing // Jpa Auditing 활성화
@SpringBootApplication // 스프링부트 자동 설정, 스프링 빈 읽기 / 생성 모두 자동 설정됨
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
