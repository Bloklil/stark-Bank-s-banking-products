package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.model.RuleStat;
import org.skypro.recommendationservice.model.RuleStatsResponse;
import org.skypro.recommendationservice.service.RecommendationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/rule")
public class RuleStatsController {
    private final RecommendationService recommendationService;

    public RuleStatsController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @GetMapping("/stats")
    public ResponseEntity<RuleStatsResponse> getRuleStats() {
        List<RuleStat> stats = recommendationService.getRuleStats();
        return ResponseEntity.ok(new RuleStatsResponse(stats));
    }
}
