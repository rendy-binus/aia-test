package com.example.aiatest.webclient.model;

import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicFeedsQueryParam {
    private MultiValueMap<String, String> params;

    private String id;
    @Builder.Default
    private List<String> ids = new ArrayList<>();
    @Builder.Default
    private List<String> tags = new ArrayList<>();
    @Builder.Default
    private TagMode tagmode = TagMode.ALL;
    @Builder.Default
    private Format format = Format.JSON;
    @Builder.Default
    private Language language = Language.ENGLISH;

    public MultiValueMap<String, String> getParams() {
        this.params = new LinkedMultiValueMap<>();

        if (StringUtils.isNotBlank(this.id)) {
            params.put("id", Collections.singletonList(this.id));
        }
        if (!this.ids.isEmpty()) {
            params.put("ids", this.ids);
        }
        if (!this.tags.isEmpty()) {
            params.put("tags", Collections.singletonList(String.join(",", this.tags)));
        }
//        params.put("tagmode", Optional.ofNullable(this.tagmode).orElse(TagMode.ALL).name());
//        params.put("format", Optional.ofNullable(this.format).orElse(Format.JSON).getValue());
//        params.put("lang", Optional.ofNullable(this.language).orElse(Language.ENGLISH).getCode());
        params.put("tagmode", Collections.singletonList(this.tagmode.name()));
        params.put("format", Collections.singletonList(this.format.getValue()));
        if (this.format == Format.JSON) {
            params.put("nojsoncallback", Collections.singletonList("1"));
        }
        params.put("lang", Collections.singletonList(this.language.getCode()));

        return params;
    }
}
