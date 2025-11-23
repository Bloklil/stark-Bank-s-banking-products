package org.skypro.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

public class RecommendationRule {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    private List<RuleCondition> rule;

    public RecommendationRule() {}

    public RecommendationRule(UUID id, String productName, UUID productId, String productText, List<RuleCondition> rule) {
        this.id = id;
        this.productName = productName;
        this.productId = productId;
        this.productText = productText;
        this.rule = rule;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    @JsonProperty("product_name")
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    @JsonProperty("product_id")
    public UUID getProductId() { return productId; }
    public void setProductId(UUID productId) { this.productId = productId; }

    @JsonProperty("product_text")
    public String getProductText() { return productText; }
    public void setProductText(String productText) { this.productText = productText; }

    public List<RuleCondition> getRule() { return rule; }
    public void setRule(List<RuleCondition> rule) { this.rule = rule; }
}
