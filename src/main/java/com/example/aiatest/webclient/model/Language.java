package com.example.aiatest.webclient.model;

public enum Language {
    GERMAN("de-de"),
    ENGLISH("en-us"),
    SPANISH("es-us"),
    FRENCH("fr-fr"),
    ITALIAN("it-it"),
    KOREAN("ko-kr"),
    BRAZILIAN_PORTUGUESE("pt-br"),
    HONGKONG_CHINESE("zh-hk");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
