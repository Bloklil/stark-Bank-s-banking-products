package org.skypro.recommendationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RuleStat {
    private UUID ruleId;
    private String ruleName;
    private Long count;
}
