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

    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) { // id 반환
        return postsService.save(requestDto);
    }

    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostsUpdateRequestDto requestDto) { // id 반환
        return postsService.update(id, requestDto);
    }

    @GetMapping("/api/v1/posts/{id}") // dto를 반환해서, 브라우저로 요청하면 json 반환
    public PostsResponseDto findById (@PathVariable Long id) {
        return postsService.findById(id);
    }

    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id) {
        postsService.delete(id);
        return id;
    }
}
