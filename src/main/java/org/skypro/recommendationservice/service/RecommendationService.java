package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.model.RuleStat;
import org.skypro.recommendationservice.repository.RecommendationRuleRepository;
import org.skypro.recommendationservice.repository.RuleStatsRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> ruleSet;
    private final RecommendationRuleRepository ruleRepository;
    private final RecommendationRuleService ruleService;
    private final RuleStatsRepository ruleStatsRepository;

    public RecommendationService(List<RecommendationRuleSet> ruleSet,
                                 RecommendationRuleRepository ruleRepository,
                                 RecommendationRuleService ruleService,
                                 RuleStatsRepository ruleStatsRepository) {
        this.ruleSet = ruleSet;
        this.ruleRepository = ruleRepository;
        this.ruleService = ruleService;
        this.ruleStatsRepository = ruleStatsRepository;
    }


    public List<Recommendation> getRecommendations(UUID userId) {
        List<Recommendation> recommendations = new ArrayList<>();

        recommendations.addAll(getLegacyRecommendations(userId));

        recommendations.addAll(getDynamicRecommendations(userId));

        return recommendations;
    }


    private List<Recommendation> getLegacyRecommendations(UUID userId) {
        return ruleSet.stream()
                .map(rule -> safeCheck(rule, userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<Recommendation> safeCheck(RecommendationRuleSet rule, UUID userId) {
        try {
            return rule.check(userId);
        } catch (Exception ex) {
            // Логирование ошибки можно добавить здесь при необходимости
            return Optional.empty();
        }
    }

    public List<Recommendation> getDynamicRecommendationsOnly(UUID userId) {
        return getDynamicRecommendations(userId);
    }

    public List<Recommendation> getLegacyRecommendationsOnly(UUID userId) {
        return getLegacyRecommendations(userId);
    }

    public boolean hasRecommendations(UUID userId) {
        return !getRecommendations(userId).isEmpty();
    }

    public int getRecommendationsCount(UUID userId) {
        return getRecommendations(userId).size();
    }

    private List<Recommendation> getDynamicRecommendations(UUID userId) {
        List<Recommendation> recommendations = new ArrayList<>();
        List<RecommendationRule> rules = ruleRepository.findAll();

        for (RecommendationRule rule : rules) {
            if (ruleService.evaluateRule(userId, rule.getRule())) {
                ruleStatsRepository.incrementRuleCounter(rule.getId());
                Recommendation recommendation = new Recommendation(
                        rule.getId(),
                        rule.getProductName(),
                        rule.getProductText()
                );
                recommendations.add(recommendation);
            }
        }

        return recommendations;
    }

    public List<RuleStat> getRuleStats() {
        return ruleStatsRepository.getAllRuleStats();
    }


}
