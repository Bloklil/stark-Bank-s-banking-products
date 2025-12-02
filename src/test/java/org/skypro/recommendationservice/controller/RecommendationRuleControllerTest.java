package org.skypro.recommendationservice.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.model.RulesListResponse;
import org.skypro.recommendationservice.repository.RecommendationRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;


public class RecommendationRuleControllerTest {
    private RecommendationRuleRepository repository;
    private RecommendationRuleController controller;

    @BeforeEach
    void setUp() {
        repository = mock(RecommendationRuleRepository.class);
        controller = new RecommendationRuleController(repository);
    }

    @Test
    @DisplayName("контроллер правильно принимает, вызывает, возвращает")
    void createRule_ShouldReturnSavedRule() {
        RecommendationRule rule = new RecommendationRule(UUID.randomUUID(),
                "бред", UUID.randomUUID(), "бредовый процент", List.of());
        when(repository.save(rule)).thenReturn(rule);

        ResponseEntity<RecommendationRule> response = controller.createRule(rule);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(rule, response.getBody());
        verify(repository).save(rule);
    }

    @Test
    @DisplayName("контроллер возвращает правила из репозитория, формирует правильный ответ")
    void getAllRules_ShouldReturnRulesListResponse() {
        RecommendationRule rule1 = new RecommendationRule(UUID.randomUUID(),
                "продукт", UUID.randomUUID(), "карта", List.of());
        RecommendationRule rule2 = new RecommendationRule(UUID.randomUUID(),
                "продукты", UUID.randomUUID(), "кредит", List.of());

        when(repository.findAll()).thenReturn(List.of(rule1, rule2));

        ResponseEntity<RulesListResponse> response = controller.getAllRules();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getData().size());
        verify(repository).findAll();
    }

    @Test
    @DisplayName("при успешном удалении правила контроллер возвращает правильный статус и вызывает репозиторий корректно")
    void deleteRuleByProductId_ShouldReturnNoContent_WhenDeleted() {
        UUID productId = UUID.randomUUID();
        when(repository.deleteByProductId(productId)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteRuleByProductId(productId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(repository).deleteByProductId(productId);
    }

    @Test
    @DisplayName("неудачное удаление правила")
    void deleteRuleByProductId_ShouldReturnNotFound_WhenNotDeleted() {
        UUID productId = UUID.randomUUID();
        when(repository.deleteByProductId(productId)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteRuleByProductId(productId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(repository).deleteByProductId(productId);
    }

    @Test
    @DisplayName("успешное удаление правила")
    void deleteRuleById_ShouldReturnNoContent_WhenDeleted() {
        UUID ruleId = UUID.randomUUID();
        when(repository.deleteById(ruleId)).thenReturn(true);

        ResponseEntity<Void> response = controller.deleteRuleById(ruleId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(repository).deleteById(ruleId);
    }

    @Test
    @DisplayName("контроллер корректно возвращает 404, если удаление невозможно.")
    void deleteRuleById_ShouldReturnNotFound_WhenNotDeleted() {
        UUID ruleId = UUID.randomUUID();
        when(repository.deleteById(ruleId)).thenReturn(false);

        ResponseEntity<Void> response = controller.deleteRuleById(ruleId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(repository).deleteById(ruleId);
    }

}