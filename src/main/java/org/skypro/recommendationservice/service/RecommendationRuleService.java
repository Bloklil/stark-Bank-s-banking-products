package org.skypro.recommendationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.skypro.recommendationservice.model.RuleCondition;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class RecommendationRuleService {

    private final CachedRecommendationsService cachedService;
    private final Cache<String, Boolean> ruleEvaluationCache;

    public RecommendationRuleService(CachedRecommendationsService cachedService,
                                     Cache<String, Boolean> ruleEvaluationCache) {
        this.cachedService = cachedService;
        this.ruleEvaluationCache = ruleEvaluationCache;
    }

    public boolean evaluateRule(UUID userId, List<RuleCondition> rules) {
        String cacheKey = createRuleEvaluationKey(userId, rules);

        return ruleEvaluationCache.get(cacheKey, key -> {
            for (RuleCondition rule : rules) {
                boolean result = evaluateSingleRule(userId, rule);
                // Если правило negate=true, инвертируем результат
                if (rule.getNegate() != null && rule.getNegate()) {
                    if (result) return false;
                } else {
                    if (!result) return false;
                }
            }
            return true;
        });
    }

    private boolean evaluateSingleRule(UUID userId, RuleCondition rule) {
        return switch (rule.getQuery()) {
            case "USER_OF" -> evaluateUserOf(userId, rule.getArguments());
            case "TRANSACTION_SUM_COMPARE_DEPOSIT_WITHDRAW" -> evaluateTransactionSumCompareDepositWithdraw(userId, rule.getArguments());
            case "TRANSACTION_SUM_COMPARE" -> evaluateTransactionSumCompare(userId, rule.getArguments());
            default -> false;
        };
    }

    private boolean evaluateUserOf(UUID userId, List<String> arguments) {
        if (arguments.size() < 1) return false;
        String productType = arguments.get(0);
        return cachedService.usesProductType(userId, productType);
    }

    private boolean evaluateTransactionSumCompareDepositWithdraw(UUID userId, List<String> arguments) {
        if (arguments.size() < 2) return false;

        String productType = arguments.get(0);
        String operator = arguments.get(1);

        BigDecimal depositSum = cachedService.getDepositSumByProductType(userId, productType);
        BigDecimal withdrawSum = cachedService.getWithdrawSumByProductType(userId, productType);

        return compareAmounts(depositSum, withdrawSum, operator);
    }

    private boolean evaluateTransactionSumCompare(UUID userId, List<String> arguments) {
        if (arguments.size() < 4) return false;

        String productType = arguments.get(0);
        String transactionType = arguments.get(1);
        String operator = arguments.get(2);
        String threshold = arguments.get(3);

        BigDecimal amount;
        if ("DEPOSIT".equals(transactionType)) {
            amount = cachedService.getDepositSumByProductType(userId, productType);
        } else if ("WITHDRAW".equals(transactionType)) {
            amount = cachedService.getWithdrawSumByProductType(userId, productType);
        } else {
            return false;
        }

        BigDecimal thresholdValue = new BigDecimal(threshold);
        return compareAmounts(amount, thresholdValue, operator);
    }

    private boolean compareAmounts(BigDecimal amount1, BigDecimal amount2, String operator) {
        return switch (operator) {
            case ">" -> amount1.compareTo(amount2) > 0;
            case ">=" -> amount1.compareTo(amount2) >= 0;
            case "<" -> amount1.compareTo(amount2) < 0;
            case "<=" -> amount1.compareTo(amount2) <= 0;
            case "==" -> amount1.compareTo(amount2) == 0;
            default -> false;
        };
    }

    private String createRuleEvaluationKey(UUID userId, List<RuleCondition> rules) {
        StringBuilder keyBuilder = new StringBuilder("rule_eval:" + userId);
        for (RuleCondition rule : rules) {
            keyBuilder.append(":").append(rule.getQuery())
                    .append(":").append(String.join(",", rule.getArguments()))
                    .append(":").append(rule.getNegate());
        }
        return keyBuilder.toString();
    }

    public void clearRuleEvaluationCache() {
        ruleEvaluationCache.invalidateAll();
        System.out.println("✅ Rule Evaluation Cache очищен");
    }
}
