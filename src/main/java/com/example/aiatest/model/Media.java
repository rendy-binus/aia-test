package com.example.aiatest.model;

import lombok.Builder;
import lombok.Data;

@Data
public class Media {
    private String small;
    private String medium;
    private String large;

    @Builder
    public Media(String url) {
        if (url.endsWith("_m.jpg")) {
            this.small = url;
            this.medium = url.replace("_m.jpg", ".jpg");
            this.large = url.replace("_m.jpg", "_b.jpg");
        } else if (url.endsWith("_b.jpg")) {
            this.small = url.replace("_b.jpg", "_m.jpg");
            this.medium = url.replace("_b.jpg", ".jpg");
            this.large = url;
        } else {
            this.small = url.replace(".jpg", "_m.jpg");
            this.medium = url;
            this.large = url.replace(".jpg", "_b.jpg");
            ;
        }
    }
}
