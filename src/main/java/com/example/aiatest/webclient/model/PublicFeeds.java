package com.example.aiatest.webclient.model;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicFeeds {
    private String title;
    private String link;
    private String description;
    private OffsetDateTime modified;
    private String generator;
    private List<Item> items;
}
