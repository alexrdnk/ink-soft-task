package com.example.localnews_backend.bootstrap;

import com.example.localnews_backend.model.City;
import com.example.localnews_backend.storage.InMemoryStorage;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

@Component
@Order(0)
public class CityLoader implements ApplicationRunner {
    private final InMemoryStorage storage;
    private static final Pattern SPLIT_OUTSIDE_QUOTES =
            Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

    public CityLoader(InMemoryStorage storage) {
        this.storage = storage;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (storage.countCities() > 0) {
            return; // already loaded
        }

        System.out.println("Loading cities...");
        List<City> cities = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource("uscities.csv").getInputStream()))) {
            // skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = SPLIT_OUTSIDE_QUOTES.split(line);
                if (parts.length < 9) {
                    continue;
                }

                String name = parts[0].replace("\"", "").trim();
                String stateCode = parts[2].replace("\"", "").trim();
                String rawLat = parts[6].replace("\"", "").trim();
                String rawLon = parts[7].replace("\"", "").trim();
                String rawPop = parts[8].replace("\"", "").trim();

                try {
                    BigDecimal lat = new BigDecimal(rawLat);
                    BigDecimal lon = new BigDecimal(rawLon);
                    int population = Integer.parseInt(rawPop);

                    City city = new City();
                    city.setName(name);
                    city.setStateCode(stateCode);
                    city.setLat(lat);
                    city.setLon(lon);
                    city.setPopulation(population);

                    cities.add(city);
                } catch (NumberFormatException e) {
                    System.err.println("Skipping city with invalid numeric data: " + line);
                }
            }
        }

        storage.saveAllCities(cities);
        System.out.println("Loaded " + cities.size() + " cities");
    }
}
