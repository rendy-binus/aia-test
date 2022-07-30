package com.example.aiatest.model.constant;

public enum PostSortField {
    TITLE("title"),
    DATE_TAKEN("date_taken"),
    PUBLISHED("published");

    private final String fieldName;

    PostSortField(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }
}
