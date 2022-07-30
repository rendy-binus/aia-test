package com.example.aiatest.model.entity;

import com.example.aiatest.model.Media;
import com.example.aiatest.model.entity.support.BaseEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Post extends BaseEntity {
    private String title;

    @Type(type = "json")
    @Column(columnDefinition = "json")
    private Media media;

    private OffsetDateTime dateTaken;

    @Column(columnDefinition = "text")
    private String description;

    private OffsetDateTime published;

    private String author;

    private String authorId;

    @ElementCollection
    @Column(name = "tag")
    @CollectionTable(name = "post_tags")
    private Set<String> tags = new LinkedHashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Post post = (Post) o;
        return Objects.equals(getId(), post.getId()) &&
                Objects.equals(getTitle(), post.getTitle()) &&
                Objects.equals(getMedia(), post.getMedia()) &&
                Objects.equals(getDateTaken(), post.getDateTaken()) &&
                Objects.equals(getPublished(), post.getPublished()) &&
                Objects.equals(getAuthorId(), post.getAuthorId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
