package com.leejohy.book.springboot.domain.posts;

import org.assertj.core.api.Assertions;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest // h2 데이터베이스 자동 실행해준다.
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After // 단위 테스트 끝날 때마다 수행되는 메서드 지정 // 여러 테스트 동시에 수행 시 테스트용 데이터베이스 h2에 데이터가 남아있으면 실패할 수 있다.
    public void cleanup() {
        System.out.println("======before delete======="); // delete하기전에 select 날아가는 듯. id를 알아야하기 때문
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        //given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        // id가 있으면 update, 없으면 insert 실행된다.
        postsRepository.save(Posts.builder() // builder 패턴 사용, save를 통해 테이블 posts에 insert/update 쿼리 실행한다.
                .title(title)
                .content(content)
                .author("leejohy@gmail.com")
                .build());

        //when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);

    }
}