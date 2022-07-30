package com.example.aiatest.model.dto;

import com.example.aiatest.model.Media;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private String title;

    private Media media;

    private OffsetDateTime dateTaken;

    private String description;

    private OffsetDateTime published;

    private String author;

    private String authorId;

    private Set<String> tags = new LinkedHashSet<>();
}
