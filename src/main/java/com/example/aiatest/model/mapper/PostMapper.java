package com.example.aiatest.model.mapper;

import com.example.aiatest.model.Media;
import com.example.aiatest.model.dto.PostDto;
import com.example.aiatest.model.entity.Post;
import com.example.aiatest.webclient.model.Item;
import com.example.aiatest.webclient.model.PublicFeeds;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PostMapper {
    public List<Post> fromPublicFeeds(PublicFeeds publicFeeds) {
        return fromItems(publicFeeds.getItems());
    }

    public List<Post> fromItems(List<Item> items) {
        List<Post> posts = new ArrayList<>();

        items.forEach(item -> {
            posts.add(Post.builder()
                    .title(item.getTitle())
                    .link(item.getLink())
                    .media(Media.builder()
                            .url(item.getMedia().getM())
                            .build())
                    .dateTaken(item.getDateTaken())
                    .description(item.getDescription())
                    .published(item.getPublished())
                    .author(item.getAuthor())
                    .authorId(item.getAuthorId())
                    .tags(Set.of(item.getTags().split(" ")))
                    .build());
        });

        return posts;
    }

    public PostDto toDto(Post post) {
        return PostDto.builder()
                .title(post.getTitle())
                .media(post.getMedia())
                .dateTaken(post.getDateTaken())
                .description(post.getDescription())
                .published(post.getPublished())
                .author(post.getAuthor())
                .authorId(post.getAuthorId())
                .tags(post.getTags())
                .build();
    }

    public List<PostDto> toDto(List<Post> posts) {
        List<PostDto> dtoList = new ArrayList<>();

        posts.forEach(post -> dtoList.add(toDto(post)));

        return dtoList;
    }

    public Page<PostDto> toDto(Page<Post> postPage) {
        return postPage.map(this::toDto);
    }
}
