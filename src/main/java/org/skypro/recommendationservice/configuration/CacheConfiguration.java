package org.skypro.recommendationservice.configuration;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfiguration {
    //пользование продуктами
    @Bean
    public Cache<String, Boolean> productUsageCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(1, TimeUnit.HOURS)
                .recordStats()
                .build();
    }

    //суммы операций
    @Bean
    public Cache<String, BigDecimal> transactionSumCache() {
        return Caffeine.newBuilder()
                .maximumSize(10_000)
                .expireAfterWrite(30, TimeUnit.MINUTES) // Суммы могут меняться чаще
                .recordStats()
                .build();
    }
}
