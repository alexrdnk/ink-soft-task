package com.example.localnews_backend.controller;

import com.example.localnews_backend.model.Article;
import com.example.localnews_backend.storage.InMemoryStorage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
    private final InMemoryStorage storage;

    public ArticleController(InMemoryStorage storage) {
        this.storage = storage;
    }

    @GetMapping("/global")
    public List<Article> getGlobal() {
        return storage.findTop20ByLocalHintFalseOrderByPublishedAtDesc();
    }

    @GetMapping("/local/{cityName}")
    public List<Article> getLocal(@PathVariable String cityName) {
        return storage.findTop80ByLocalHintTrueAndCityOrderByPublishedAtDesc(cityName);
    }
}
