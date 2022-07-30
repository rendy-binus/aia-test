package com.example.aiatest.service;

import com.example.aiatest.config.PostConfig;
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

    private final PostConfig config;

    @Recurring(id = "recurring-fetch-feeds-and-save-posts-job", interval = "${post.recurring-job.fetch-feeds-and-save-post.interval}")
    @Job(name = "Fetch Feeds and Save Posts Job")
    @Transactional
    public void fetchFeedsAndSavePostsJob() {
        // get past request that is being called at least x minutes ago
        Optional<PostRequest> postRequestOptional = postRequestRepository
                .findFirstByUpdatedDateBeforeOrderByUpdatedDateAsc(now()
                        .minusMinutes(config.getRecurringJob().getFetchFeedsAndSavePost().getMinusMinutes()));

        if (postRequestOptional.isPresent()) {
            PostRequest postRequest = postRequestOptional.get();

            log.info("Updating previous fetched feeds...\n" +
                    "request: {}", postRequest);

            getAndSaveFeeds(List.of(postRequest.getTags().split(",")), postRequest.getTagMode(), postRequest);

            log.info("\"Fetch Feeds and Save Posts Job\" is DONE!");
            return;
        }

        log.info("[\"Fetch Feeds and Save Posts Job\"]: No request that was being called at least {} minutes ago",
                config.getRecurringJob().getFetchFeedsAndSavePost().getMinusMinutes());
    }

    @Recurring(id = "recurring-get-past-requests-and-delete-job", cron = "${post.recurring-job.get-past-requests-and-delete.cron}")
    @Job(name = "Get Past Requests And Delete Job")
    @Transactional
    public void getPastRequestsAndDeleteJob() {
        postRequestRepository.deleteAllByCreatedDateBefore(now()
                .minusDays(config.getRecurringJob().getGetPastRequestsAndDelete().getMinusDays()));
        log.info("\"Get Past Requests And Delete Job\" is DONE!");
    }

    @Recurring(id = "recurring-get-posts-and-delete-job", cron = "${post.recurring-job.get-posts-and-delete.cron}")
    @Job(name = "Get Posts And Delete Job")
    @Transactional
    public void getPostsAndDeleteJob() {
        postRepository.deleteAllByCreatedDateBefore(now()
                .minusDays(config.getRecurringJob().getGetPostsAndDelete().getMinusDays()));
        log.info("\"Get Posts And Delete\" Job is DONE!");
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

        // if the request is present then check if it is longer than x minutes ago
        // if the request is not present then fetch new feeds
        if (existingRequestOpt.isPresent()) {
            PostRequest existingRequest = existingRequestOpt.get();

            // if the request is longer than x minutes ago then fetch new feeds
            if (existingRequest.getUpdatedDate().isBefore(now().minusMinutes(config.getExistingRequestBefore()))) {
                log.info("existing request is longer than {} minutes ago", config.getExistingRequestBefore());
                getAndSaveFeeds(tags, tagMode, existingRequest);
            } else {
                log.info("existing request is shorter than {} minutes ago", config.getExistingRequestBefore());
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
