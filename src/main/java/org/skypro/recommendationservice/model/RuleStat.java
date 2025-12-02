package org.skypro.recommendationservice.model;

import java.util.UUID;

public class RuleStat {
    private UUID ruleId;
    private String ruleName;
    private Long count;

    public RuleStat(UUID ruleId, String ruleName, Long count) {
        this.ruleId = ruleId;
        this.ruleName = ruleName;
        this.count = count;
    }

    public UUID getRuleId() { return ruleId; }
    public void setRuleId(UUID ruleId) { this.ruleId = ruleId; }

    public String getRuleName() { return ruleName; }
    public void setRuleName(String ruleName) { this.ruleName = ruleName; }

    public Long getCount() { return count; }
    public void setCount(Long count) { this.count = count; }
}
