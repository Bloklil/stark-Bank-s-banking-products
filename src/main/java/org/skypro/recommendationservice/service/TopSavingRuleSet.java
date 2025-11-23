package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.skypro.recommendationservice.util.TextUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class TopSavingRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;
    private static final UUID ID = UUID.fromString("59efc529-2fff-41af-baff-90ccd7402925");

    public TopSavingRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> check(UUID userId) {
        boolean usesDebit = repository.productNam(userId, "DEBIT");
        BigDecimal debitDeposit = repository.depositSumType(userId, "DEBIT");
        BigDecimal savingDeposits = repository.depositSumType(userId, "SAVING");
        BigDecimal windrowSumType = repository.windrowSumType(userId, "DEBIT");

        boolean enougtDep = debitDeposit.compareTo(BigDecimal.valueOf(50_000)) >= 0
                || savingDeposits.compareTo(BigDecimal.valueOf(50_000)) >= 0;
        boolean depositsGreaterThanWithdraws = debitDeposit.compareTo(windrowSumType) > 0;

        if (usesDebit && enougtDep && depositsGreaterThanWithdraws) {
            return Optional.of(new Recommendation(ID, "Top Saving", TextUtils.TopSavingText));
        }
        return Optional.empty();
    }
}