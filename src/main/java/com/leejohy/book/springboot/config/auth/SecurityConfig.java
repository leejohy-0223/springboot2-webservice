package com.leejohy.book.springboot.config.auth;

import com.leejohy.book.springboot.domain.user.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;

@RequiredArgsConstructor
@EnableWebSecurity // spring security 설정 활성화(spring security filter chain 포함된다)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // csrf = 사이트간 요청 위조 -> disable
                .headers().frameOptions().disable() // h2 사용하기 위해 해당 옵션들 disable

                .and()
                .authorizeRequests() // url 별 권한 관리를 설정하는 옵션의 시작점, 이게 선언되어야 antMatchers 옵션 사용 가능
                .antMatchers("/", "/css/**", "/images/**",
                        "/js/**", "/h2-console/**", "/profile").permitAll() // 전체 열람 권한 부여
                .antMatchers("/api/v1/**").hasRole(Role.USER.name()) // api는 user 권한 가진 사람만 가능
                .anyRequest().authenticated() // 나머지는 로그인한 사용자들에게만 허용

                .and()
                    .logout() // 로그아웃 기능 설정 진입점
                        .logoutSuccessUrl("/") // 로그아웃 성공 시 / 로 이동한다.

                .and()
                    .oauth2Login() // OAuth2 로그인 설정 진입점
                        .userInfoEndpoint() // 로그인 성공 후 사용자 정보를 가져올 때의 설정을 담당
                            .userService(customOAuth2UserService); // 소셜 로그인 성공 시 후속 조치를 진행할 UserService 인터페이스의 구현체 등록
                                                                   // 리소스 서버(즉, 소셜 서비스들)에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능 명시

    }

}
