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
            @Qualifier("recommendationsJdbcTemplate") JdbcTemplate jdbcTemplate
    ) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public int getRandomTransactionAmount(UUID userId) {
        Integer result = jdbcTemplate.queryForObject(
                "SELECT amount FROM transactions t WHERE t.user_id = ? LIMIT 1",
                Integer.class,
                userId
        );
        return result != null ? result : 0;
    }

    public boolean productNam(UUID userId, String productType) {
        String sql = """
                SELECT COUNT(*) FROM TRANSACTIONS t
                            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                            WHERE t.USER_ID = ? AND p.TYPE = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId.toString(), productType);
        return count != null && count > 0;
    }

    public BigDecimal depositSumType(UUID userId, String productType) {
        String sql = """
                SELECT COALESCE(SUM(t.AMOUNT), 0)
                            FROM TRANSACTIONS t
                            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                            WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'DEPOSIT'
                """;
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId.toString(), productType);
        return sum == null ? BigDecimal.ZERO : sum;
    }

    public BigDecimal windrowSumType(UUID userId, String productType) {
        String sql = """
                SELECT COALESCE(SUM(t.AMOUNT), 0)
                            FROM TRANSACTIONS t
                            JOIN PRODUCTS p ON t.PRODUCT_ID = p.ID
                            WHERE t.USER_ID = ? AND p.TYPE = ? AND t.TYPE = 'WITHDRAW'
                """;
        BigDecimal sum = jdbcTemplate.queryForObject(sql, BigDecimal.class, userId.toString(), productType);
        return sum == null ? BigDecimal.ZERO : sum;
    }
}