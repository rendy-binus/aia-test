package com.example.aiatest.controller.request;

import com.example.aiatest.model.constant.PostSortField;
import com.example.aiatest.model.constant.SortDirection;
import com.example.aiatest.validation.constraints.ValueOfEnum;
import com.example.aiatest.webclient.model.TagMode;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class PostRequest implements Serializable {
    private static final long serialVersionUID = 8761993241787909696L;

    private Set<String> tags = new HashSet<>();

    @ValueOfEnum(enumClass = TagMode.class, message = "{validation.post.tagMode.message}")
    private String tagMode = TagMode.ANY.name();

    @Min(0)
    private int page = 0;

    @Min(5)
    @Max(50)
    private int size = 5;

    @ValueOfEnum(enumClass = PostSortField.class, message = "{validation.post.sortBy.message}")
    private String sortBy = PostSortField.PUBLISHED.name();
    @ValueOfEnum(enumClass = SortDirection.class, message = "{validation.sortDirection.message}")
    private String sortDirection = SortDirection.ASC.name();

    public void setTags(Set<String> tags) {
        this.tags = tags != null ? tags : new HashSet<>();
    }

    public void setTagMode(String tagMode) {
        this.tagMode = tagMode != null ? tagMode.toUpperCase() : TagMode.ANY.name();
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy != null ? sortBy.toUpperCase() : PostSortField.PUBLISHED.name();
    }

    public void setSortDirection(String sortDirection) {
        this.sortDirection = sortDirection != null ? sortDirection.toUpperCase() : SortDirection.ASC.name();
    }
}
