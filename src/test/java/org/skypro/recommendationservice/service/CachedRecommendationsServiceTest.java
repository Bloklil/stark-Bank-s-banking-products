package org.skypro.recommendationservice.service;

import com.github.benmanes.caffeine.cache.Cache;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.recommendationservice.repository.RecommendationsRepository;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class CachedRecommendationsServiceTest {

    private RecommendationsRepository repository;
    private Cache<String, Boolean> productUsageCache;
    private Cache<String, BigDecimal> transactionSumCache;
    private CachedRecommendationsService service;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        repository = mock(RecommendationsRepository.class);
        productUsageCache = mock(Cache.class);
        transactionSumCache = mock(Cache.class);

        service = new CachedRecommendationsService(repository, productUsageCache, transactionSumCache);
    }

    @Test
    @DisplayName("корректная работа с кэшем и репозиторием")
    void usesProductType_ShouldReturnTrue_WhenRepositoryReturnsTrue() {
        when(productUsageCache.get(anyString(), any()))
                .thenAnswer(invocation -> ((java.util.function.Function<String, Boolean>) invocation.getArgument(1))
                        .apply(invocation.getArgument(0)));

        when(repository.usesProductType(userId, "DEBIT")).thenReturn(true);

        boolean result = service.usesProductType(userId, "DEBIT");

        assertTrue(result);
        verify(repository).usesProductType(userId, "DEBIT");
    }

    @Test
    @DisplayName("корректно получает значение суммы депозитов из репозитория и работает через кэш")
    void getDepositSumByProductType_ShouldReturnCachedValue() {
        when(transactionSumCache.get(anyString(), any()))
                .thenAnswer(invocation -> ((java.util.function.Function<String, BigDecimal>) invocation.getArgument(1))
                        .apply(invocation.getArgument(0)));

        when(repository.getDepositSumByProductType(userId, "SAVINGS")).thenReturn(BigDecimal.valueOf(1500));

        BigDecimal result = service.getDepositSumByProductType(userId, "SAVINGS");

        assertEquals(BigDecimal.valueOf(1500), result);
        verify(repository).getDepositSumByProductType(userId, "SAVINGS");
    }

    @Test
    @DisplayName("правильно получает сумму снятий через кэш и репозиторий")
    void getWithdrawSumByProductType_ShouldReturnCachedValue() {
        when(transactionSumCache.get(anyString(), any()))
                .thenAnswer(invocation -> ((java.util.function.Function<String, BigDecimal>) invocation.getArgument(1))
                        .apply(invocation.getArgument(0)));

        when(repository.getWithdrawSumByProductType(userId, "SAVINGS")).thenReturn(BigDecimal.valueOf(500));

        BigDecimal result = service.getWithdrawSumByProductType(userId, "SAVINGS");

        assertEquals(BigDecimal.valueOf(500), result);
        verify(repository).getWithdrawSumByProductType(userId, "SAVINGS");
    }

    @Test
    @DisplayName("очистка всех кэш")
    void clearCaches_ShouldInvalidateAllCaches() {
        service.clearCaches();

        verify(productUsageCache).invalidateAll();
        verify(transactionSumCache).invalidateAll();
    }

    @Test
    @DisplayName("если кеш пуст, всё работает")
    void printCacheStats_ShouldNotThrow() {
        assertDoesNotThrow(service::printCacheStats);
    }


}