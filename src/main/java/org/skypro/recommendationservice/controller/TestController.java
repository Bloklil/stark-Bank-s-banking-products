package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class TestController {
    private final RecommendationsRepository repository;

    public TestController(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/test/{userId}")
    public int test(@PathVariable UUID userId) {
        return repository.getRandomTransactionAmount(userId);
    }
}