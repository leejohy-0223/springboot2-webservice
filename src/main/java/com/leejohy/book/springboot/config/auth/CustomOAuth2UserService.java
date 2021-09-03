package com.leejohy.book.springboot.config.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.leejohy.book.springboot.config.auth.dto.OAuthAttributes;
import com.leejohy.book.springboot.config.auth.dto.SessionUser;
import com.leejohy.book.springboot.domain.user.User;
import com.leejohy.book.springboot.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @SneakyThrows
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService(); // 기본 userService 생성(delegage = 대리자)
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        System.out.println("==========userRequest============");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(userRequest));
        System.out.println("==========OAuth2User============");
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(oAuth2User));
        System.out.println("==========OAuth2User end============");

        // 1. 현재 로그인 진행 중인 서비스 구분하는 코드. 네이버 / 카카오 등 추가 연동 시 어떤 로그인인지 구분하기 위해 사용
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        System.out.println("registrationId = " + registrationId);

        // 2. OAuth2 로그인 진행 시 키가 되는 필드값을 이야기한다. PK 같은 의미
        // 구글의 경우 기본적으로 코드를 지원하지만, 네카는 지원 안한다. 기본 코드는 "sub" 이다.
        // 이후 네이버, 구글 로그인 동시 지원시 사용한다.
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        System.out.println("userNameAttributeName = " + userNameAttributeName);

        // 3. OAuth2User를 통해 가져온 OAuth2User의 attribute를 담을 클래스이다. 이후 다른 소셜 로그인도 이 클래스를 사용한다.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        Map<String, Object> attributes1 = oAuth2User.getAttributes();

        System.out.println("========attribute=============");
        Set<String> strings = attributes1.keySet();
        for (String string : strings) {
            System.out.println(attributes1.get(string));
            System.out.println("=======Object==============");
        }
        System.out.println("========attribute end=============");
        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new
                SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture())) // 존재한다면 업데이트
                .orElse(attributes.toEntity()); // 아니면 새로운 엔티티 만들어 반환

        return userRepository.save(user);
    }
}
