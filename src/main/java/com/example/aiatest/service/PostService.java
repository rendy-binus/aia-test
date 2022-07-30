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
import org.jobrunr.jobs.annotations.Job;
import org.jobrunr.spring.annotations.Recurring;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
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

    @Recurring(id = "recurring-get-feeds-and-save-posts-job", interval = "PT1M")
    @Job(name = "Get Feeds and Save Posts Job")
    @Transactional
    public void getFeedsAndSavePostsJob() {
        // get past request that is being called at least 15 minutes ago
        Optional<PostRequest> postRequestOptional = postRequestRepository
                .findFirstByUpdatedDateBeforeOrderByUpdatedDateAsc(now().minusMinutes(15));

        if (postRequestOptional.isPresent()) {
            PostRequest postRequest = postRequestOptional.get();

            log.info("Updating previous fetched feeds...\n" +
                    "request: {}", postRequest);

            getAndSaveFeeds(List.of(postRequest.getTags().split(",")), postRequest.getTagMode(), postRequest);

            log.info("\"Get Feeds and Save Posts Job\" is DONE!");
            return;
        }

        log.info("[\"Get Feeds and Save Posts Job\"]: No request that is being called at least 15 minutes ago");
    }

    @Transactional
    public List<Post> getPosts() {
        return getPosts(Collections.emptyList(), TagMode.ALL);
    }

    @Transactional
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

    @Transactional
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
                log.info("existing request is longer than 15 minutes ago");
                getAndSaveFeeds(tags, tagMode, existingRequest);
            } else {
                log.info("existing request is shorter than 15 minutes ago");
            }
        } else {
            log.info("there is no existing request with similar query");
            getAndSaveFeeds(tags, tagMode);
        }

        Pageable pageRequest = PageRequest.of(page, size, Sort.Direction.valueOf(sortDirection.name()), sortBy.getFieldName());

        if (tagMode == TagMode.ALL) {
            return postRepository.findAllByAllTags(tags, tags.size(), pageRequest);
        }

        return postRepository.findAllByAnyTags(tags, pageRequest);
    }

    @Transactional
    public void getAndSaveFeeds(List<String> tags, TagMode tagMode) {
        getAndSaveFeeds(tags, tagMode, PostRequest.builder().build());
    }

    @Transactional
    public void getAndSaveFeeds(List<String> tags, TagMode tagMode, PostRequest postRequest) {
        List<Post> posts = getPosts(tags, tagMode);

        List<Post> existingPosts = new ArrayList<>();
        List<Post> newPosts = new ArrayList<>();

        posts.forEach(post -> {
            Optional<Post> existingPost = postRepository.findByLink(post.getLink());

            if (existingPost.isPresent()) {
                existingPosts.add(existingPost.get());
            } else {
                newPosts.add(post);
            }
        });

        if (!existingPosts.isEmpty()) {
            postRepository.saveAll(existingPosts);
        }
        if (!newPosts.isEmpty()) {
            postRepository.saveAll(newPosts);
        }

        if (postRequest.getId() != null) {
            postRequestRepository.save(postRequest);
        } else {
            postRequestRepository.save(PostRequest.builder()
                    .tags(String.join(",", tags))
                    .tagMode(tagMode)
                    .build());
        }
    }

    @Transactional
    public Page<PostDto> getPostsDto(List<String> tags, TagMode tagMode, int page, int size, SortDirection sortDirection, PostSortField sortBy) {
        return postMapper.toDto(getPosts(tags, tagMode, page, size, sortDirection, sortBy));
    }
}
