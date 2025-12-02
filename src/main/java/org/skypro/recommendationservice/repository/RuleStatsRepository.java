package org.skypro.recommendationservice.repository;

import org.skypro.recommendationservice.model.RuleStat;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class RuleStatsRepository {
    private final JdbcTemplate jdbcTemplate;

    public RuleStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void incrementRuleCounter(UUID ruleId) {
        String sql = """
            INSERT INTO rule_stats (rule_id, count) 
            VALUES (?, 1)
            ON CONFLICT (rule_id) 
            DO UPDATE SET count = rule_stats.count + 1
            """;
        jdbcTemplate.update(sql, ruleId);
    }

    public List<RuleStat> getAllRuleStats() {
        String sql = """
            SELECT 
                rr.id as rule_id, 
                rr.product_name as rule_name,
                COALESCE(rs.count, 0) as count
            FROM recommendation_rule rr
            LEFT JOIN rule_stats rs ON rr.id = rs.rule_id
            ORDER BY rs.count DESC, rr.product_name
            """;
        return jdbcTemplate.query(sql, new RuleStatRowMapper());
    }

    public Long getRuleCount(UUID ruleId) {
        String sql = "SELECT count FROM rule_stats WHERE rule_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, Long.class, ruleId);
        } catch (Exception e) {
            return 0L;
        }
    }

    public void deleteRuleStats(UUID ruleId) {
        String sql = "DELETE FROM rule_stats WHERE rule_id = ?";
        jdbcTemplate.update(sql, ruleId);
    }

    private static class RuleStatRowMapper implements RowMapper<RuleStat> {
        @Override
        public RuleStat mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new RuleStat(
                    UUID.fromString(rs.getString("rule_id")),
                    rs.getString("rule_name"),
                    rs.getLong("count")
            );
        }
    }

}
