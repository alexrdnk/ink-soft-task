package com.example.localnews_backend.model;

import java.math.BigDecimal;

public class City {
    private Long id;
    private String name;
    private String stateCode;
    private BigDecimal lat;
    private BigDecimal lon;
    private Integer population;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getStateCode() { return stateCode; }
    public void setStateCode(String stateCode) { this.stateCode = stateCode; }

    public BigDecimal getLat() { return lat; }
    public void setLat(BigDecimal lat) { this.lat = lat; }

    public BigDecimal getLon() { return lon; }
    public void setLon(BigDecimal lon) { this.lon = lon; }

    public Integer getPopulation() { return population; }
    public void setPopulation(Integer population) { this.population = population; }
}
