package com.example.aiatest.webclient;

import com.adelean.inject.resources.junit.jupiter.*;
import com.example.aiatest.webclient.model.PublicFeeds;
import com.example.aiatest.webclient.model.PublicFeedsQueryParam;
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

import java.util.concurrent.ExecutionException;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@TestWithResources
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FlickrWebClientTest {
    @Autowired
    RestTemplate restTemplate;

    @Autowired
    FlickrWebClient flickrWebClient;

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
    @DisplayName(value = "Default Request")
    void givenMockingIsDoneByMockRestServiceServer_whenGetFeedsIsCalled_thenReturnsMockedObject() throws JsonProcessingException, ExecutionException, InterruptedException {
        mockServer.expect(ExpectedCount.once(),
                        requestTo("https://www.flickr.com/services/feeds/photos_public.gne?tagmode=ALL&format=json&nojsoncallback=1&lang=en-us")
                )
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(objectMapper.writeValueAsString(feedsResponseBase))
                );

        PublicFeeds publicFeeds = flickrWebClient.getPublicFeeds(PublicFeedsQueryParam.builder().build()).get();

        mockServer.verify();

        Assertions.assertEquals(feedsResponseBase, publicFeeds);
    }
}
