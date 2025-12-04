package org.skypro.recommendationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class Recommendation {
    private final UUID id;
    private final String name;
    private final String text;
}
