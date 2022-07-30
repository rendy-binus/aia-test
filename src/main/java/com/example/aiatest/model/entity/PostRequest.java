package com.example.aiatest.model.entity;

import com.example.aiatest.model.entity.support.BaseEntity;
import com.example.aiatest.webclient.model.TagMode;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PostRequest extends BaseEntity {
    @Column(unique = true)
    private String tags;

    @Enumerated(EnumType.STRING)
    private TagMode tagMode;
}
