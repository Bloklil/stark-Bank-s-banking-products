package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.model.RulesListResponse;
import org.skypro.recommendationservice.repository.RecommendationRuleRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/rule")
public class RecommendationRuleController {
    private final RecommendationRuleRepository ruleRepository;

    public RecommendationRuleController(RecommendationRuleRepository ruleRepository) {
        this.ruleRepository = ruleRepository;
    }

    @PostMapping
    public ResponseEntity<RecommendationRule> createRule(@RequestBody RecommendationRule rule) {
        RecommendationRule savedRule = ruleRepository.save(rule);
        return ResponseEntity.ok(savedRule);
    }

    @GetMapping
    public ResponseEntity<RulesListResponse> getAllRules() {
        List<RecommendationRule> rules = ruleRepository.findAll();
        return ResponseEntity.ok(new RulesListResponse(rules));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteRuleByProductId(@PathVariable UUID productId) {
        boolean deleted = ruleRepository.deleteByProductId(productId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/id/{ruleId}")
    public ResponseEntity<Void> deleteRuleById(@PathVariable UUID ruleId) {
        boolean deleted = ruleRepository.deleteById(ruleId);
        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}
