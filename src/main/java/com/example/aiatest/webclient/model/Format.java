package com.example.aiatest.webclient.model;

public enum Format {
    RSS_200("rss_200"),
    ATOM_1("atom_1"),
    RSS_091("rss_091"),
    RSS_092("rss_092"),
    RDF("rdf"),
    RSS_200_ENC("rss_200_enc"),
    PHP("php"),
    PHP_SERIAL("php_serial"),
    CSV("csv"),
    JSON("json"),
    SQL("sql"),
    YAML("yaml"),
    CDF("cdf");

    private final String value;

    Format(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
