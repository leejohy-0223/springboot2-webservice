package com.leejohy.book.springboot.web;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class) // 테스트 진행 시 Junit에 내장된 실행자 외에 다른 실행자를 실행시킨다.
@WebMvcTest(controllers = HelloController.class) // 여러 스프링 어노테이션 중, Web(Spring MVC)에 집중할 수 있는 어노테이션. @Controller 등 사용 가능하다.(service, component, repository는 사용 불가)
public class HelloControllerTest { // public 써줘야 함;

    @Autowired
    private MockMvc mvc; // web mvc 테스트용, 테스트의 시작점, 이 클래스 통해 GET / POST API 테스트 가능

    @Test
    public void hello가_리턴된다() throws Exception {
        String hello = "hello";

        mvc.perform(get("/hello")) // mock mvc를 통해 /hello 주소로 get 요청을 한다.
                .andExpect(status().isOk()) // 200 ok인지 검증
                .andExpect(content().string(hello)); // 응답 본문의 내용 검증
    }

    @Test
    public void helloDto가_리턴된다() throws Exception {
        String name = "hello";
        int amount = 1000;

        mvc.perform(get("/hello/dto")
                .param("name", name)
                .param("amount", String.valueOf(amount))) // param은 모두 String만 허용된다.
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name))) // JSON 응답값을 필드별로 검증할 수 있다.
                .andExpect(jsonPath("$.amount", is(amount)));
    }
}