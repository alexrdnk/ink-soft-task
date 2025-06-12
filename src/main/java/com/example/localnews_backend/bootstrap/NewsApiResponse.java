// src/main/java/com/example/localnews_backend/bootstrap/NewsApiResponse.java
package com.example.localnews_backend.bootstrap;

import java.util.List;

public class NewsApiResponse {
    private List<NewsApiArticle> articles;

    public List<NewsApiArticle> getArticles() {
        return articles;
    }

    public void setArticles(List<NewsApiArticle> articles) {
        this.articles = articles;
    }
}
