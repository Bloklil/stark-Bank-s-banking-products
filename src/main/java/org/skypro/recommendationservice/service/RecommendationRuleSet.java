package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;

import java.util.Optional;
import java.util.UUID;

public interface RecommendationRuleSet {
    Optional<Recommendation> check(UUID userId);
}
