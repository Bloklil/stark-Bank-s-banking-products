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
@RequestMapping("/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final RecommendationRuleRepository ruleRepository;

    // Убираем проблемные зависимости
    public RecommendationController(RecommendationService recommendationService,
                                    RecommendationRuleRepository ruleRepository) {
        this.recommendationService = recommendationService;
        this.ruleRepository = ruleRepository;
    }

    // Основные endpoints
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Recommendation>> getUserRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = recommendationService.getRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/user/{userId}/dynamic")
    public ResponseEntity<List<Recommendation>> getDynamicRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = recommendationService.getDynamicRecommendationsOnly(userId);
        return ResponseEntity.ok(recommendations);
    }

    @GetMapping("/user/{userId}/legacy")
    public ResponseEntity<List<Recommendation>> getLegacyRecommendations(@PathVariable UUID userId) {
        List<Recommendation> recommendations = recommendationService.getLegacyRecommendationsOnly(userId);
        return ResponseEntity.ok(recommendations);
    }

    // Простой debug endpoint
    @GetMapping("/debug/{userId}")
    public ResponseEntity<Map<String, Object>> simpleDebug(@PathVariable UUID userId) {
        Map<String, Object> debugInfo = new HashMap<>();

        try {
            // 1. Проверяем динамические правила
            List<RecommendationRule> rules = ruleRepository.findAll();
            debugInfo.put("dynamicRulesInDatabase", rules.size());

            // 2. Проверяем рекомендации
            List<Recommendation> dynamicRecs = recommendationService.getDynamicRecommendationsOnly(userId);
            List<Recommendation> legacyRecs = recommendationService.getLegacyRecommendationsOnly(userId);
            List<Recommendation> allRecs = recommendationService.getRecommendations(userId);

            debugInfo.put("dynamicRecommendationsCount", dynamicRecs.size());
            debugInfo.put("legacyRecommendationsCount", legacyRecs.size());
            debugInfo.put("allRecommendationsCount", allRecs.size());

            debugInfo.put("dynamicRecommendations", dynamicRecs);
            debugInfo.put("legacyRecommendations", legacyRecs);

            return ResponseEntity.ok(debugInfo);

        } catch (Exception e) {
            debugInfo.put("error", e.getMessage());
            return ResponseEntity.status(500).body(debugInfo);
        }
    }

    // Отдельный endpoint для проверки правил
    @GetMapping("/debug/rules")
    public ResponseEntity<List<RecommendationRule>> debugRules() {
        List<RecommendationRule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(rules);
    }
}
