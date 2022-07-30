package com.example.aiatest.service;

import com.adelean.inject.resources.junit.jupiter.*;
import com.example.aiatest.model.entity.Post;
import com.example.aiatest.model.mapper.PostMapper;
import com.example.aiatest.webclient.model.PublicFeeds;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@TestWithResources
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PostServiceTest {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    PostService postService;

    @Autowired
    PostMapper postMapper;

    MockRestServiceServer mockServer;

    @WithJacksonMapper
    ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @GivenJsonResource("feeds-response-base.json")
    PublicFeeds feedsResponseBase;

    @BeforeEach
    void init() {
        mockServer = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("Default Request")
    void givenMockingIsDoneByMockRestServiceServer_whenGetFeedsIsCalled_thenReturnsMockedObject() throws JsonProcessingException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo("https://www.flickr.com/services/feeds/photos_public.gne?tagmode=ALL&format=json&nojsoncallback=1&lang=en-us")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(feedsResponseBase))
                );

        List<Post> posts = postService.getPosts();

        mockServer.verify();

        Assertions.assertEquals(postMapper.fromPublicFeeds(feedsResponseBase), posts);
    }
}
