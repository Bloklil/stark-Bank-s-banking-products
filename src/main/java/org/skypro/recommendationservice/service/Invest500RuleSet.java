package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
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
            String text = """
                    Откройте свой путь к успеху с индивидуальным инвестиционным счетом (ИИС) от нашего банка!
                    Воспользуйтесь налоговыми льготами и начните инвестировать с умом. Пополните счет до конца года и
                    получите выгоду в виде вычета на взнос в следующем налоговом периоде. Не упустите возможность разнообразить свой портфель,
                    снизить риски и следить за актуальными рыночными тенденциями. Откройте ИИС сегодня и станьте ближе к финансовой независимости!
                    """;
            return Optional.of(new Recommendation(ID, "Invest 500", text));
        }
        return Optional.empty();
    }
}
