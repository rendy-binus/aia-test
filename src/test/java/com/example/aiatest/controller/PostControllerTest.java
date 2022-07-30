package com.example.aiatest.controller;

import com.example.aiatest.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.stream.Stream;

@ContextConfiguration(classes = {PostController.class})
@ExtendWith(SpringExtension.class)
public class PostControllerTest {
    @Autowired
    PostController postController;

    @MockBean
    PostService postService;

    static Stream<Arguments> getValidQueryParams() {
        return Stream.of(Arguments.of("design", "ALL", "title", "asc"),
                Arguments.of("design,culture", "all", null, "DESC"),
                Arguments.of("Politics,Health", null, "DATE_taken", "Asc"),
                Arguments.of("SCIENCE", null, null, null),
                Arguments.of(null, null, null, null)
        );
    }

    @DisplayName("All Valid Query Params")
    @ParameterizedTest(name = "[{index}] - {arguments}")
    @MethodSource("getValidQueryParams")
    void whenAllValid_thenShouldReturnOK(String tags, String tagMode, String sortBy, String sortDirection) throws Exception {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("tags", tags);
        queryParams.add("tagMode", tagMode);
        queryParams.add("sortBy", sortBy);
        queryParams.add("sortDirection", sortDirection);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/posts")
                .queryParams(queryParams);

        ResultActions performResult = MockMvcBuilders.standaloneSetup(this.postController)
                .build()
                .perform(requestBuilder);

        performResult.andExpect(MockMvcResultMatchers.status().isOk());
    }
}
