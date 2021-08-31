package com.leejohy.book.springboot.domain.posts;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsRepository extends JpaRepository<Posts, Long> { // <Entity class, pk 타입>, Dao 역할, 기본 CRUD 자동 생성됨

    @Query("SELECT p From Posts p ORDER BY p.id DESC")
    List<Posts> findAllDesc();
}
