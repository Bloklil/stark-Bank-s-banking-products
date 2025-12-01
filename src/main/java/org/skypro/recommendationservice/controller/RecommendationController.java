package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.repository.RecommendationRuleRepository;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.skypro.recommendationservice.service.RecommendationRuleSet;
import org.skypro.recommendationservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
public class RecommendationController {

    private final RecommendationService service;

    public RecommendationController(RecommendationService service) {
        this.service = service;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getRecomendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = service.getRecommendations(userId);
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userId.toString());
        body.put("recommendations", recommendations);
        return ResponseEntity.ok(body);
    }

    @GetMapping("/user/{userId}/dynamic")
    public ResponseEntity<List<Recommendation>> getDynamicRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = service.getDynamicRecommendationsOnly(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/user/{userId}/legacy")
    public ResponseEntity<List<Recommendation>> getLegacyRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = service.getLegacyRecommendationsOnly(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/user/{userId}/has-recommendations")
    public ResponseEntity<Boolean> hasRecommendations(@PathVariable UUID userId) {
        boolean hasRecommendations = service.hasRecommendations(userId);
        return ResponseEntity.ok(hasRecommendations);
    }

    @GetMapping("/user/{userId}/count")
    public ResponseEntity<Integer> getRecommendationsCount(@PathVariable UUID userId) {
        int count = service.getRecommendationsCount(userId);
        return ResponseEntity.ok(count);
    }
}
