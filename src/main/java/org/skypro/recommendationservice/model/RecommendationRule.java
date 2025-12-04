package org.skypro.recommendationservice.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecommendationRule {
    private UUID id;
    private String productName;
    private UUID productId;
    private String productText;
    private List<RuleCondition> rule;

    @JsonProperty("product_name")
    public String getProductName() {
        return productName;
    }

    @JsonProperty("product_id")
    public UUID getProductId() {
        return productId;
    }

    @JsonProperty("product_text")
    public String getProductText() {
        return productText;
    }
}
