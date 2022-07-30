package com.example.aiatest.controller;

import com.example.aiatest.controller.request.PostRequest;
import com.example.aiatest.model.constant.PostSortField;
import com.example.aiatest.model.constant.SortDirection;
import com.example.aiatest.model.dto.PostDto;
import com.example.aiatest.service.PostService;
import com.example.aiatest.webclient.model.TagMode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;

@Slf4j
@Validated
@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostDto>> getPosts(@Valid PostRequest request) {
        log.debug("request: {}", request);
        return ResponseEntity.ok(postService
                .getPostsDto(
                        new ArrayList<>(request.getTags()),
                        TagMode.valueOf(request.getTagMode()),
                        request.getPage(),
                        request.getSize(),
                        SortDirection.valueOf(request.getSortDirection()),
                        PostSortField.valueOf(request.getSortBy())
                )
        );
    }
}
