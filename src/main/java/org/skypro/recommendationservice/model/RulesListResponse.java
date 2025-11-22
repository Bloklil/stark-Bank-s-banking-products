package org.skypro.recommendationservice.model;

import java.util.List;

public class RulesListResponse {

    private List<RecommendationRule> data;

    public RulesListResponse(List<RecommendationRule> data) {
        this.data = data;
    }

    public List<RecommendationRule> getData() { return data; }
    public void setData(List<RecommendationRule> data) { this.data = data; }
}
