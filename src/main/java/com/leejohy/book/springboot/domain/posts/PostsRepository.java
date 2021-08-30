package com.leejohy.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> { // <Entity class, pk 타입>, Dao 역할, 기본 CRUD 자동 생성됨

}
