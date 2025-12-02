package org.skypro.recommendationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.recommendationservice.model.RuleCondition;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RecommendationRuleServiceTest {

    private CachedRecommendationsService cachedService;
    private Cache<String, Boolean> ruleCache;
    private RecommendationRuleService service;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        cachedService = mock(CachedRecommendationsService.class);
        ruleCache = mock(Cache.class);
        service = new RecommendationRuleService(cachedService, ruleCache);
    }

    @Test
    @DisplayName("переданные правила для пользователя выполняются успешно")
    void evaluateRule_ShouldReturnTrue_WhenAllRulesPass() {
        RuleCondition rule1 = new RuleCondition("USER_OF",
                List.of("DEBIT"),
                false);

        when(cachedService.usesProductType(userId, "DEBIT")).thenReturn(true);
        when(ruleCache.get(anyString(), any())).thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(invocation.getArgument(0)));

        boolean result = service.evaluateRule(userId, List.of(rule1));

        assertTrue(result);
    }

    @Test
    @DisplayName("переданные правила для пользователя не выполняются")
    void evaluateRule_ShouldReturnFalse_WhenRuleFails() {
        RuleCondition rule1 = new RuleCondition("USER_OF",
                List.of("DEBIT"),
                false);

        when(cachedService.usesProductType(userId, "DEBIT")).thenReturn(false);
        when(ruleCache.get(anyString(), any())).thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(invocation.getArgument(0)));

        boolean result = service.evaluateRule(userId, List.of(rule1));

        assertFalse(result);
    }

    @Test
    @DisplayName("логика инверсии правила через negate")
    void evaluateRule_ShouldInvertResult_WhenNegateTrue() {
        RuleCondition rule1 = new RuleCondition("USER_OF",
                List.of("DEBIT"),
                true);

        when(cachedService.usesProductType(userId, "DEBIT")).thenReturn(true);
        when(ruleCache.get(anyString(), any())).thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(invocation.getArgument(0)));

        boolean result = service.evaluateRule(userId, List.of(rule1));

        assertFalse(result);
    }

    @Test
    @DisplayName("сравнение суммы транзакции с порогом в классе")
    void evaluateTransactionSumCompare_ShouldReturnTrue_WhenDepositGreaterThanThreshold() {
        RuleCondition rule = new RuleCondition("TRANSACTION_SUM_COMPARE",
                List.of("SAVINGS", "DEPOSIT", ">", "1000"),
                false);

        when(cachedService.getDepositSumByProductType(userId, "SAVINGS")).thenReturn(BigDecimal.valueOf(1500));
        when(ruleCache.get(anyString(), any())).thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(invocation.getArgument(0)));

        boolean result = service.evaluateRule(userId, List.of(rule));

        assertTrue(result);
    }

    @Test
    @DisplayName("правильная обработка правила типа, когда сумма депозита меньше суммы снятия")
    void evaluateTransactionSumCompareDepositWithdraw_ShouldReturnFalse_WhenDepositLessThanWithdraw() {
        RuleCondition rule = new RuleCondition(
                "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW",
                List.of("SAVINGS", ">="),
                false);

        when(cachedService.getDepositSumByProductType(userId, "SAVINGS")).thenReturn(BigDecimal.valueOf(500));
        when(cachedService.getWithdrawSumByProductType(userId, "SAVINGS")).thenReturn(BigDecimal.valueOf(1000));
        when(ruleCache.get(anyString(), any())).thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1)).apply(invocation.getArgument(0)));

        boolean result = service.evaluateRule(userId, List.of(rule));

        assertFalse(result);
    }

    @Test
    @DisplayName("генерация уникального ключа для разных правил")
    void createRuleEvaluationKey_ShouldGenerateUniqueKey_ForDifferentRules() {
        RuleCondition rule1 = new RuleCondition("USER_OF", List.of("DEBIT"), false);
        RuleCondition rule2 = new RuleCondition("USER_OF", List.of("CREDIT"), false);

        when(ruleCache.get(anyString(), any()))
                .thenAnswer(invocation -> {
                    java.util.function.Function<String, Boolean> func =
                            invocation.getArgument(1);
                    return func.apply(invocation.getArgument(0));
                });

        when(cachedService.usesProductType(userId, "DEBIT")).thenReturn(true);
        when(cachedService.usesProductType(userId, "CREDIT")).thenReturn(false);

        String key1 = service.evaluateRule(userId, List.of(rule1)) ? "true" : "false";
        String key2 = service.evaluateRule(userId, List.of(rule2)) ? "true" : "false";

        assertNotEquals(key1, key2);
    }


}