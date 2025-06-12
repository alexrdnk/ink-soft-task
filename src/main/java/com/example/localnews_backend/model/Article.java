package com.example.localnews_backend.model;

import java.time.Instant;

public class Article {
    private Long id;
    private String title;
    private String body;

    private String url;

    private String source;

    private Instant publishedAt;

    private Boolean localHint;

    private String city;

    // --- Getters & setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Instant publishedAt) { this.publishedAt = publishedAt; }

    public Boolean isLocalHint() { return localHint; }
    public void setLocalHint(Boolean localHint) { this.localHint = localHint; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
}
