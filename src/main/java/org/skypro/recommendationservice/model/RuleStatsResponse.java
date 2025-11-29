package org.skypro.recommendationservice.model;

import java.util.List;

public class RuleStatsResponse {
    private List<RuleStat> stats;

    public RuleStatsResponse(List<RuleStat> stats) {
        this.stats = stats;
    }

    public List<RuleStat> getStats() { return stats; }
    public void setStats(List<RuleStat> stats) { this.stats = stats; }
}
