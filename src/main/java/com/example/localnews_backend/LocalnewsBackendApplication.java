package com.example.localnews_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication   // this will now scan com.example.localnews and all sub-packages
public class LocalnewsBackendApplication {
	public static void main(String[] args) {
		SpringApplication.run(LocalnewsBackendApplication.class, args);
	}
}
