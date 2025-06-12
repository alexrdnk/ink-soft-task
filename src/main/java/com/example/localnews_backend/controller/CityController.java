package com.example.localnews_backend.controller;

import com.example.localnews_backend.model.City;
import com.example.localnews_backend.storage.InMemoryStorage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cities")
public class CityController {

    private final InMemoryStorage storage;

    public CityController(InMemoryStorage storage) {
        this.storage = storage;
    }

    /**
     * GET /api/cities?prefix={prefix}&page={page}&size={size}
     * Returns a paginated list of cities whose names start with the given prefix.
     */
    @GetMapping
    public List<City> getCities(
            @RequestParam(defaultValue = "") String prefix,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return storage.findByNameStartingWith(prefix, page, size);
    }
}
