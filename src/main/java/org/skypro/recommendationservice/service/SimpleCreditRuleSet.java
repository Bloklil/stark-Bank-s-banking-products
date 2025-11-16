package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
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
            String text = """
                    Откройте мир выгодных кредитов с нами! Ищете способ быстро и без лишних хлопот получить нужную сумму? 
                    Тогда наш выгодный кредит — именно то, что вам нужно! Мы предлагаем низкие процентные ставки, гибкие условия и индивидуальный 
                    подход к каждому клиенту. Почему выбирают нас: Быстрое рассмотрение заявки. Мы ценим ваше время, поэтому процесс рассмотрения заявки 
                    занимает всего несколько часов. Удобное оформление. Подать заявку на кредит можно онлайн на нашем сайте или в мобильном приложении. 
                    Широкий выбор кредитных продуктов. Мы предлагаем кредиты на различные цели: покупку недвижимости, автомобиля, образование, 
                    лечение и многое другое. Не упустите возможность воспользоваться выгодными условиями кредитования от нашей компании!
                    """;
            return Optional.of(new Recommendation(ID, "Простой кредит", text));
        }
        return Optional.empty();
    }

}
