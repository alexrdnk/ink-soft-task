package com.example.localnews_backend.service;

import com.example.localnews_backend.bootstrap.NewsApiArticle;
import com.example.localnews_backend.bootstrap.NewsApiResponse;
import com.example.localnews_backend.model.Article;
import com.example.localnews_backend.model.City;
import com.example.localnews_backend.service.LlmClassifier.Classification;
import com.example.localnews_backend.storage.InMemoryStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
@Order(1)
public class NewsFetchService implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(NewsFetchService.class);

    private static final int DESIRED_GLOBAL = 20;
    private static final int DESIRED_LOCAL  = 80;
    private static final int PAGE_SIZE      = 5;
    private static final long RATE_LIMIT_DELAY = TimeUnit.HOURS.toMillis(12); // 12 hours delay for rate limit
    private static final int MAX_RETRIES = 3;

    private final InMemoryStorage storage;
    private final LlmClassifier classifier;
    private final RestTemplate rt = new RestTemplate();
    private final String apiKey;
    private Instant lastRateLimitHit = Instant.MIN;

    public NewsFetchService(
            InMemoryStorage storage,
            LlmClassifier classifier,
            @Value("${newsapi.key}") String apiKey
    ) {
        this.storage    = storage;
        this.classifier = classifier;
        this.apiKey     = apiKey;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (storage.countArticles() > 0) {
            log.info("Articles already loaded, skipping NewsFetchService");
            return;
        }

        int loadedGlobal = fetchGlobal();
        int loadedLocal  = fetchLocal();
        log.info("NewsFetchService loaded {} global and {} local articles", loadedGlobal, loadedLocal);
    }

    private int fetchGlobal() {
        int count = 0;
        String url = "https://newsapi.org/v2/top-headlines"
                + "?language=en"
                + "&pageSize=" + DESIRED_GLOBAL
                + "&apiKey=" + apiKey;

        try {
            if (shouldSkipDueToRateLimit()) {
                log.warn("Skipping global fetch due to recent rate limit");
                return 0;
            }

            ResponseEntity<NewsApiResponse> resp = rt.getForEntity(url, NewsApiResponse.class);
            for (NewsApiArticle na : resp.getBody().getArticles()) {
                if (count >= DESIRED_GLOBAL) break;
                Article a = mapToArticle(na);

                // classify, but fallback to GLOBAL on error
                Classification label = safeClassify(a);
                if ("GLOBAL".equalsIgnoreCase(label.scope())) {
                    a.setLocalHint(false);
                    a.setCity(null);
                    storage.saveArticle(a);
                    count++;
                }
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            handleRateLimit();
            log.error("Rate limit hit while fetching global headlines", e);
        } catch (Exception e) {
            log.error("Failed to fetch global headlines, falling back to 0 globals", e);
        }
        log.info("Fetched {} global articles", count);
        return count;
    }

    private int fetchLocal() {
        int count = 0;
        int citiesToQuery = (DESIRED_LOCAL + PAGE_SIZE - 1) / PAGE_SIZE;
        List<City> cities = storage.findByNameStartingWith("", 0, citiesToQuery);

        outer:
        for (City city : cities) {
            if (shouldSkipDueToRateLimit()) {
                log.warn("Skipping local fetch due to recent rate limit");
                break;
            }

            String cityName = city.getName();
            String q = URLEncoder.encode(cityName, StandardCharsets.UTF_8);
            String url = "https://newsapi.org/v2/everything"
                    + "?language=en"
                    + "&pageSize=" + PAGE_SIZE
                    + "&q=" + q
                    + "&apiKey=" + apiKey;

            int retries = 0;
            while (retries < MAX_RETRIES) {
                try {
                    ResponseEntity<NewsApiResponse> resp = rt.getForEntity(url, NewsApiResponse.class);
                    for (NewsApiArticle na : resp.getBody().getArticles()) {
                        if (count >= DESIRED_LOCAL) break outer;

                        Article a = mapToArticle(na);
                        a.setLocalHint(true);
                        a.setCity(cityName);
                        storage.saveArticle(a);
                        count++;
                    }
                    break; // Success, exit retry loop
                } catch (HttpClientErrorException.TooManyRequests e) {
                    handleRateLimit();
                    log.warn("Rate limit hit while fetching local news for {}, waiting...", cityName);
                    retries++;
                    if (retries < MAX_RETRIES) {
                        try {
                            Thread.sleep(1000 * retries); // Exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch local for {}, skipping: {}", cityName, e.toString());
                    break;
                }
            }
        }
        log.info("Fetched {} local articles", count);
        return count;
    }

    private boolean shouldSkipDueToRateLimit() {
        return lastRateLimitHit.plusMillis(RATE_LIMIT_DELAY).isAfter(Instant.now());
    }

    private void handleRateLimit() {
        lastRateLimitHit = Instant.now();
    }

    /** Safely calls the LLM, defaulting to GLOBAL/no-city on any error. */
    private Classification safeClassify(Article a) {
        try {
            return classifier.classify(a);
        } catch (Exception e) {
            log.warn("LLM classify failed for '{}', defaulting to GLOBAL: {}", a.getTitle(), e.toString());
            return new Classification("GLOBAL", null);
        }
    }

    /** Map and guard against nulls so we never see NPE on getBody().length() */
    private Article mapToArticle(NewsApiArticle na) {
        Article a = new Article();
        a.setTitle(na.getTitle() != null ? na.getTitle() : "");
        String body = na.getDescription() != null ? na.getDescription() : "";
        a.setBody(body);
        a.setUrl(na.getUrl() != null ? na.getUrl() : "");
        a.setSource(na.getSource() != null && na.getSource().getName() != null
                ? na.getSource().getName() : "");
        a.setPublishedAt(
                na.getPublishedAt() != null
                        ? Instant.parse(na.getPublishedAt())
                        : Instant.now()
        );
        return a;
    }
}
