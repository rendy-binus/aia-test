package com.example.aiatest.webclient;

import com.example.aiatest.webclient.config.FlickrWebClientConfig;
import com.example.aiatest.webclient.model.PublicFeeds;
import com.example.aiatest.webclient.model.PublicFeedsQueryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class FlickrWebClient {
    private final RestTemplate restTemplate;
    private final FlickrWebClientConfig config;

    @Async
    public CompletableFuture<PublicFeeds> getPublicFeeds(PublicFeedsQueryParam request) {
        UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.fromUriString(config.getUrl())
                .path(config.getServices().getPublicFeeds())
                .queryParams(request.getParams());

        PublicFeeds publicFeeds = restTemplate.getForObject(uriComponentsBuilder.build().toUriString(), PublicFeeds.class);

        return CompletableFuture.completedFuture(publicFeeds);
    }
}
