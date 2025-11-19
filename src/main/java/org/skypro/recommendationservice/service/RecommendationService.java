package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class RecommendationService {

    private final List<RecommendationRuleSet> releSet;

    public RecommendationService(List<RecommendationRuleSet> releSet) {
        this.releSet = releSet;
    }

    public List<Recommendation> getRecommendations(UUID userId) {
        return releSet.stream()
                .map(rule -> safeCheck(rule, userId))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    private Optional<Recommendation> safeCheck(RecommendationRuleSet rule, UUID userId) {
        try {
            return rule.check(userId);
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

}
