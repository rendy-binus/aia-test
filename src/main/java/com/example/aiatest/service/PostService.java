package com.example.aiatest.service;

import com.example.aiatest.model.constant.PostSortField;
import com.example.aiatest.model.constant.SortDirection;
import com.example.aiatest.model.dto.PostDto;
import com.example.aiatest.model.entity.Post;
import com.example.aiatest.model.entity.PostRequest;
import com.example.aiatest.model.mapper.PostMapper;
import com.example.aiatest.repository.PostRepository;
import com.example.aiatest.repository.PostRequestRepository;
import com.example.aiatest.webclient.FlickrWebClient;
import com.example.aiatest.webclient.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static java.time.OffsetDateTime.now;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {
    private final FlickrWebClient flickrWebClient;
    private final PostMapper postMapper;

    private final PostRepository postRepository;
    private final PostRequestRepository postRequestRepository;

    public List<Post> getPosts() {
        return getPosts(Collections.emptyList(), TagMode.ALL);
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

    public Page<Post> getPosts(List<String> tags, TagMode tagMode, int page, int size, SortDirection sortDirection, PostSortField sortBy) {
        tags.sort(Comparator.naturalOrder());

        // find existing request that has same query parameters
        Optional<PostRequest> existingRequestOpt = postRequestRepository.findFirstByTagsAndTagMode(String.join(",", tags), tagMode);

        // if the request is present then check if it is longer than 15 minutes ago
        // if the request is not present then fetch new feeds
        if (existingRequestOpt.isPresent()) {
            PostRequest existingRequest = existingRequestOpt.get();

            // if the request is longer than 15 minutes ago then fetch new feeds
            if (existingRequest.getUpdatedDate().isBefore(now().minusMinutes(15))) {
                getAndSaveFeeds(tags, tagMode, existingRequest);
            }
        } else {
            getAndSaveFeeds(tags, tagMode);
        }

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection.name()), sortBy.getFieldName());

        return postRepository.findAllByTagsIn(tags, pageRequest);
    }

    private void getAndSaveFeeds(List<String> tags, TagMode tagMode) {
        getAndSaveFeeds(tags, tagMode, PostRequest.builder().build());
    }

    private void getAndSaveFeeds(List<String> tags, TagMode tagMode, PostRequest postRequest) {
        List<Post> posts = getPosts(tags, tagMode);

        postRepository.saveAll(posts);
        postRequestRepository.save(PostRequest.builder()
                .id(postRequest.getId())
                .tags(String.join(",", tags))
                .tagMode(tagMode)
                .build());
    }

    public Page<PostDto> getPostsDto(List<String> tags, TagMode tagMode, int page, int size, SortDirection sortDirection, PostSortField sortBy) {
        return postMapper.toDto(getPosts(tags, tagMode, page, size, sortDirection, sortBy));
    }
}
