package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.skypro.recommendationservice.util.TextUtils;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Component
public class SimpleCreditRuleSet implements RecommendationRuleSet {

    private final RecommendationsRepository repository;
    private static final UUID ID = UUID.fromString("ab138afb-f3ba-4a93-b74f-0fcee86d447f");


    public SimpleCreditRuleSet(RecommendationsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Optional<Recommendation> check(UUID userId) {
        boolean hasCredit = repository.productNam(userId, "CREDIT");
        BigDecimal debitDep = repository.depositSumType(userId, "DEBIT");
        BigDecimal debitWindrows = repository.windrowSumType(userId, "DEBIT");

        if (!hasCredit && debitDep.compareTo(debitWindrows) > 0 && debitWindrows.compareTo(BigDecimal.valueOf(100_000)) > 0) {
            return Optional.of(new Recommendation(ID, "Простой кредит", TextUtils.SimpleCreditText));
        }
        return Optional.empty();
    }

}
