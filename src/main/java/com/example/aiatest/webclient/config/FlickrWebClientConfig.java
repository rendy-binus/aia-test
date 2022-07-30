package com.example.aiatest.webclient.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

@Configuration
@ConfigurationProperties(prefix = "web-client.flickr")
@Getter
@Setter
public class FlickrWebClientConfig {
    @NotBlank
    private String url;

    private Services services;

    @Getter
    @Setter
    public static class Services {
        @NotBlank
        private String publicFeeds;
    }
}
