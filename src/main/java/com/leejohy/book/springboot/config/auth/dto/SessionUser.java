package com.leejohy.book.springboot.config.auth.dto;

import com.leejohy.book.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable { // 인증된 사용자 정보만 필요하다. 그 외의 필요한 정보는 없으니 아래 것만 필드로 선언(role 제외시킴)
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
