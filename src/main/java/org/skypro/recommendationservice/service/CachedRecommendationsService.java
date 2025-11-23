package org.skypro.recommendationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.skypro.recommendationservice.repository.RecommendationsRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CachedRecommendationsService {
    private final RecommendationsRepository repository;
    private final Cache<String, Boolean> productUsageCache;
    private final Cache<String, BigDecimal> transactionSumCache;

    public CachedRecommendationsService(RecommendationsRepository repository,
                                        Cache<String, Boolean> productUsageCache,
                                        Cache<String, BigDecimal> transactionSumCache) {
        this.repository = repository;
        this.productUsageCache = productUsageCache;
        this.transactionSumCache = transactionSumCache;
    }

    public boolean usesProductType(UUID userId, String productType) {
        String cacheKey = createProductUsageKey(userId, productType);

        return productUsageCache.get(cacheKey, key ->
                repository.usesProductType(userId, productType)
        );
    }

    public BigDecimal getDepositSumByProductType(UUID userId, String productType) {
        String cacheKey = createTransactionSumKey(userId, productType, "DEPOSIT");

        return transactionSumCache.get(cacheKey, key ->
                repository.getDepositSumByProductType(userId, productType)
        );
    }

    public BigDecimal getWithdrawSumByProductType(UUID userId, String productType) {
        String cacheKey = createTransactionSumKey(userId, productType, "WITHDRAW");

        return transactionSumCache.get(cacheKey, key ->
                repository.getWithdrawSumByProductType(userId, productType)
        );
    }

    private String createProductUsageKey(UUID userId, String productType) {
        return String.format("product_usage:%s:%s", userId, productType);
    }

    private String createTransactionSumKey(UUID userId, String productType, String transactionType) {
        return String.format("transaction_sum:%s:%s:%s", userId, productType, transactionType);
    }

    public void printCacheStats() {
        System.out.println("Product Usage Cache Stats: " + productUsageCache.stats());
        System.out.println("Transaction Sum Cache Stats: " + transactionSumCache.stats());
    }

    public void clearCaches() {
        productUsageCache.invalidateAll();
        transactionSumCache.invalidateAll();
    }
}
