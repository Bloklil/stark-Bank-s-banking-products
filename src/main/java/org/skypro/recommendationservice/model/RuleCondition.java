package org.skypro.recommendationservice.model;

import java.util.List;

public class RuleCondition {
    private String query;
    private List<String> arguments;
    private Boolean negate;

    public RuleCondition() {}

    public RuleCondition(String query, List<String> arguments, Boolean negate) {
        this.query = query;
        this.arguments = arguments;
        this.negate = negate;
    }

    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public List<String> getArguments() { return arguments; }
    public void setArguments(List<String> arguments) { this.arguments = arguments; }

    public Boolean getNegate() { return negate; }
    public void setNegate(Boolean negate) { this.negate = negate; }
}
