package com.example.localnews_backend.storage;

import com.example.localnews_backend.model.Article;
import com.example.localnews_backend.model.City;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryStorage {
    private final List<City> cities = new ArrayList<>();
    private final List<Article> articles = new ArrayList<>();

    // --- Cities API (you already have these) ---
    public long countCities() {
        return cities.size();
    }

    public void saveAllCities(List<City> list) {
        cities.addAll(list);
    }

    public List<City> findByNameStartingWith(String prefix, int page, int size) {
        return cities.stream()
                .filter(c -> c.getName().toLowerCase().startsWith(prefix.toLowerCase()))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    // --- Articles API (new) ---
    public long countArticles() {
        return articles.size();
    }

    public void saveArticle(Article a) {
        articles.add(a);
    }

    public List<Article> getArticles() {
        return new ArrayList<>(articles);
    }

    public List<Article> findTop20ByLocalHintFalseOrderByPublishedAtDesc() {
        return articles.stream()
                .filter(a -> Boolean.FALSE.equals(a.isLocalHint()))
                .filter(a -> a.getPublishedAt() != null)
                .sorted(Comparator.comparing(Article::getPublishedAt).reversed())
                .limit(20)
                .collect(Collectors.toList());
    }

    public List<Article> findTop80ByLocalHintTrueAndCityOrderByPublishedAtDesc(String cityName) {
        return articles.stream()
                .filter(a -> Boolean.TRUE.equals(a.isLocalHint()))
                .filter(a -> cityName.equalsIgnoreCase(a.getCity()))
                .filter(a -> a.getPublishedAt() != null)
                .sorted(Comparator.comparing(Article::getPublishedAt).reversed())
                .limit(80)
                .collect(Collectors.toList());
    }
}
