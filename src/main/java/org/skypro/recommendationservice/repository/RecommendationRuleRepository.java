package org.skypro.recommendationservice.repository;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.model.RuleCondition;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class RecommendationRuleRepository {
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public RecommendationRuleRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

//    public void createTableIfNotExists() {
//        String sql = """
//            CREATE TABLE IF NOT EXISTS recommendation_rule (
//                id UUID PRIMARY KEY,
//                product_name VARCHAR(255) NOT NULL,
//                product_id UUID NOT NULL,
//                product_text TEXT,
//                rule_json TEXT NOT NULL,
//                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
//            )
//            """;
//        jdbcTemplate.execute(sql);
//    }

    public RecommendationRule save(RecommendationRule rule) {
        if (rule.getId() == null) {
            rule.setId(UUID.randomUUID());
        }

        try {
            String ruleJson = objectMapper.writeValueAsString(rule.getRule());

            String sql = """
                INSERT INTO recommendation_rule (id, product_name, product_id, product_text, rule_json)
                VALUES (?, ?, ?, ?, ?)
                """;

            jdbcTemplate.update(sql,
                    rule.getId(),
                    rule.getProductName(),
                    rule.getProductId(),
                    rule.getProductText(),
                    ruleJson
            );

            return rule;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing rule to JSON", e);
        }
    }

    public List<RecommendationRule> findAll() {
        String sql = "SELECT id, product_name, product_id, product_text, rule_json FROM recommendation_rule";
        return jdbcTemplate.query(sql, new RecommendationRuleRowMapper());
    }

    public Optional<RecommendationRule> findById(UUID id) {
        String sql = "SELECT id, product_name, product_id, product_text, rule_json FROM recommendation_rule WHERE id = ?";
        List<RecommendationRule> rules = jdbcTemplate.query(sql, new RecommendationRuleRowMapper(), id);
        return rules.isEmpty() ? Optional.empty() : Optional.of(rules.get(0));
    }

    public Optional<RecommendationRule> findByProductId(UUID productId) {
        String sql = "SELECT id, product_name, product_id, product_text, rule_json FROM recommendation_rule WHERE product_id = ?";
        List<RecommendationRule> rules = jdbcTemplate.query(sql, new RecommendationRuleRowMapper(), productId);
        return rules.isEmpty() ? Optional.empty() : Optional.of(rules.get(0));
    }

    public boolean deleteById(UUID id) {
        String sql = "DELETE FROM recommendation_rules WHERE id = ?";
        int affectedRows = jdbcTemplate.update(sql, id);
        return affectedRows > 0;
    }

    public boolean deleteByProductId(UUID productId) {
        String sql = "DELETE FROM recommendation_rules WHERE product_id = ?";
        int affectedRows = jdbcTemplate.update(sql, productId);
        return affectedRows > 0;
    }

    private class RecommendationRuleRowMapper implements RowMapper<RecommendationRule> {
        @Override
        public RecommendationRule mapRow(ResultSet rs, int rowNum) throws SQLException {
            try {
                UUID id = UUID.fromString(rs.getString("id"));
                String productName = rs.getString("product_name");
                UUID productId = UUID.fromString(rs.getString("product_id"));
                String productText = rs.getString("product_text");
                String ruleJson = rs.getString("rule_json");

                List<RuleCondition> ruleConditions = objectMapper.readValue(ruleJson,
                        objectMapper.getTypeFactory().constructCollectionType(List.class, RuleCondition.class));

                return new RecommendationRule(id, productName, productId, productText, ruleConditions);
            } catch (JsonProcessingException e) {
                throw new SQLException("Error parsing rule JSON", e);
            }
        }
    }
}
