package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.skypro.recommendationservice.util.TextUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class Invest500RuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;
    private static final UUID ID = UUID.fromString("147f6a0f-3b91-413b-ab99-87f081d60d5a");

    public Invest500RuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> check(UUID userId) {
        boolean usesDebit = repository.productNam(userId, "DEBIT");
        boolean usesInvest = repository.productNam(userId, "INVEST");
        BigDecimal savingDeposit = repository.depositSumType(userId, "SAVING");

        if (usesDebit && !usesInvest && savingDeposit.compareTo(BigDecimal.valueOf(1000)) > 0) {
            return Optional.of(new Recommendation(
                    ID, "Invest 500", TextUtils.Invest500Text));
        }
        return Optional.empty();
    }
}
