package com.leejohy.book.springboot.web;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class IndexControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩() {
        //when
        String body = this.restTemplate.getForObject("/", String.class); // 주어진 url 주소로 http get 메서드로 응답된 객체 결과를 받는다


        //then
        assertThat(body).contains("스프링부트로 시작하는 웹 서비스"); // 결과 html에 이게 포함되어 있다.
       assertThat(body).contains("meta http-equiv="); // 결과 html에 이게 포함되어 있다.
       assertThat(body).doesNotContain("xmlns:th="); // 타임리프 문법은 다 사라져서 반환되므로 포함되지 않는게 맞다.
    }
}