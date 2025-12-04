package org.skypro.recommendationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleCondition {
    private String query;
    private List<String> arguments;
    private Boolean negate;
}
