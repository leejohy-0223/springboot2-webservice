package com.leejohy.book.springboot.web;

import com.leejohy.book.springboot.service.posts.PostsService;
import com.leejohy.book.springboot.web.dto.PostsResponseDto;
import com.leejohy.book.springboot.web.dto.PostsSaveRequestDto;
import com.leejohy.book.springboot.web.dto.PostsUpdateRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostsService postsService;

    // 저장(Create)
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) { // id 반환
        return postsService.save(requestDto);
    }

    // 업데이트(Update)
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) { // id 반환
        return postsService.update(id, requestDto);
    }

    // 찾기(Read)
    @GetMapping("/api/v1/posts/{id}") // dto를 반환해서, 브라우저로 요청하면 json 반환
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    // 삭제(Delete)
    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }
}
