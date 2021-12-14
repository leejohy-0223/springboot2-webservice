package com.leejohy.book.springboot.domain.posts;

import com.leejohy.book.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter // 절대 setter는 생성하지 말자. 대신 해당 필드 값 변경 필요 시, 그 목적과 의도를 알 수 있는 메서드 추가 필요
@NoArgsConstructor
@Entity // 클래스의 카멜케이스 이름을 언더스코어 네이밍으로 테이블 이름 매칭
public class Posts extends BaseTimeEntity {

    @Id // pk field
    @GeneratedValue(strategy = GenerationType.IDENTITY) // auto_increment
    private Long id;

    @Column(length = 500, nullable = false) // 굳이 선언 안해도 해당 클래스 필드는 모두 칼럼이 됨. 추가 옵션 필요 시 선언
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) // 타입을 text로 변경
    private String content;

    private String author;

    @Builder // 해당 클래스의 빌더 패턴 클래스를 생성, 생성자 상단에 선언 시 생성자에 포함된 필드만 빌더에 포함
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

}
