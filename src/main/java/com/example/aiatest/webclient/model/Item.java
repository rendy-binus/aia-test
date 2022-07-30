package com.example.aiatest.webclient.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class Item {
    private String title;
    private String link;
    private Media media;
    private OffsetDateTime dateTaken;
    private String description;
    private OffsetDateTime published;
    private String author;
    private String authorId;
    private String tags;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Media {
        private String m;
    }
}
