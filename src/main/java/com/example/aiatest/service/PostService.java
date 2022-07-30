package com.example.aiatest.service;

import com.example.aiatest.model.dto.PostDto;
import com.example.aiatest.model.entity.Post;
import com.example.aiatest.model.mapper.PostMapper;
import com.example.aiatest.webclient.FlickrWebClient;
import com.example.aiatest.webclient.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final FlickrWebClient flickrWebClient;
    private final PostMapper postMapper;

    public List<Post> getPosts() {
        return getPosts(Collections.emptyList(), TagMode.ANY);
    }

    public List<Post> getPosts(List<String> tags, TagMode tagMode) {
        PublicFeedsQueryParam queryParam = PublicFeedsQueryParam.builder()
                .tags(tags)
                .tagmode(tagMode)
                .build();

        CompletableFuture<PublicFeeds> res = flickrWebClient.getPublicFeeds(queryParam);

        List<Post> posts;

        try {
            posts = postMapper.fromPublicFeeds(res.get());
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        return posts;
    }

    public List<PostDto> getPostsDto(List<String> tags, TagMode tagMode) {
        return postMapper.toDto(getPosts(tags, tagMode));
    }
}
