package com.leejohy.book.springboot.config.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER) // 이 어노테이션이 생성될 수 있는 위치 지정(여기에서는 메서드의 파라미터로 선언된 객체에서만 사용)
@Retention(RetentionPolicy.RUNTIME) // 런타임 동안에 retention(보유)
public @interface LoginUser {
}
