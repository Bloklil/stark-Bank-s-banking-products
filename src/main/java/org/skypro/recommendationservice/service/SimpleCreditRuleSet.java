package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.util.TextUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final CachedRecommendationsService cachedService;
    private static final UUID ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");


    public SimpleCreditRuleSet(CachedRecommendationsService cachedService) {
        this.cachedService = cachedService;
    }

    @Override
    public Optional<Recommendation> check(UUID userId) {
        boolean hasCredit = cachedService.usesProductType(userId, "CREDIT");
        BigDecimal debitDepositSum = cachedService.getDepositSumByProductType(userId, "DEBIT");
        BigDecimal debitWithdrawSum = cachedService.getWithdrawSumByProductType(userId, "DEBIT");

        if (!hasCredit &&
            debitDepositSum.compareTo(debitWithdrawSum) > 0 &&
            debitWithdrawSum.compareTo(BigDecimal.valueOf(100_000)) > 0) {
            return Optional.of(new Recommendation(ID, "Простой кредит", TextUtils.SimpleCreditText));
        }
        return Optional.empty();
    }
}
