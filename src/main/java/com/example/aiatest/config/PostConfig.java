package com.example.aiatest.config;

import lombok.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Configuration
@ConfigurationProperties(prefix = "post")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostConfig {
    @Min(5)
    @Max(60)
    private long existingRequestBefore = 5;
    private RecurringJob recurringJob;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecurringJob {
        private FetchFeedsAndSavePost fetchFeedsAndSavePost;
        private GetPastRequestsAndDelete getPastRequestsAndDelete;
        private GetPostsAndDelete getPostsAndDelete;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class FetchFeedsAndSavePost {
            private String interval = "PT1M";
            @Min(15)
            @Max(120)
            private long minusMinutes = 15;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GetPastRequestsAndDelete {
            private String cron = "30 01 0 * * *";
            @Min(1)
            @Max(14)
            private long minusDays = 2;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        public static class GetPostsAndDelete {
            private String cron = "30 01 0 * * *";
            @Min(1)
            @Max(14)
            private long minusDays = 2;
        }
    }
}
