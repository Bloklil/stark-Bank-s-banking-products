package org.skypro.recommendationservice.repository;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.UUID;

@Repository
public class RecommendationsRepository {

    private final JdbcTemplate jdbcTemplate;

    public RecommendationsRepository(
            @Qualifier("writeJdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID userId) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM TRANSACTIONS t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                userId
        );
        return result != null ? result : 0;
    }

    public boolean usesProductType(UUID userId, String productType) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM USER_PRODUCTS WHERE user_id = ? AND product_type = ?",
                Integer.class,
                userId, productType
        );
        return count != null && count > 0;
    }

    public BigDecimal getDepositSumByProductType(UUID userId, String productType) {
        BigDecimal result = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(amount), 0) 
                FROM TRANSACTIONS t 
                JOIN USER_PRODUCTS up ON t.user_id = up.user_id AND t.product_id = up.product_id
                WHERE t.user_id = ? AND up.product_type = ? AND t.transaction_type = 'DEPOSIT'
                """,
                BigDecimal.class,
                userId, productType
        );
        return result != null ? result : BigDecimal.ZERO;
    }

    public BigDecimal getWithdrawSumByProductType(UUID userId, String productType) {
        BigDecimal result = jdbcTemplate.queryForObject(
                """
                SELECT COALESCE(SUM(amount), 0) 
                FROM TRANSACTIONS t 
                JOIN USER_PRODUCTS up ON t.user_id = up.user_id AND t.product_id = up.product_id
                WHERE t.user_id = ? AND up.product_type = ? AND t.transaction_type = 'WITHDRAW'
                """,
                BigDecimal.class,
                userId, productType
        );
        return result != null ? result : BigDecimal.ZERO;
    }
}