package org.skypro.recommendationservice.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.skypro.recommendationservice.model.RecommendationRule;
import org.skypro.recommendationservice.model.RuleCondition;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureTestDatabase
public class RecommendationRuleRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    private ObjectMapper objectMapper;

    private RecommendationRuleRepository repository;

    @Mock
    RuleStatsRepository ruleStatsRepository;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        objectMapper = new ObjectMapper();
        repository = new RecommendationRuleRepository(jdbcTemplate, objectMapper, ruleStatsRepository);
    }

    private RecommendationRule createRule() {
        return new RecommendationRule(
                null,
                "что-то",
                UUID.randomUUID(),
                "то-то",
                List.of(new RuleCondition("amount", List.of(">500"), false))
        );
    }

    @Test
    @DisplayName("юнит-тест на метод save")
    void save_ShouldCallInsertQueryWithCorrectArguments() throws Exception {
        RecommendationRule rule = createRule();

        repository.save(rule);

        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);

        verify(jdbcTemplate).update(
                anyString(),
                captor.capture(),
                captor.capture(),
                captor.capture(),
                captor.capture(),
                captor.capture()
        );

        List<Object> args = captor.getAllValues();

        assertEquals(5, args.size());

        assertTrue(args.get(0) instanceof UUID);

        assertEquals("что-то", args.get(1));

        String json = (String) args.get(4);
        List<?> parsed = objectMapper.readValue(json, List.class);
        assertEquals(1, parsed.size());
    }

    @Test
    @DisplayName("проверка метода deleteById, успех")
    void deleteById_ShouldReturnTrue_WhenRowDeleted() {
        when(jdbcTemplate.update(anyString(), (Object[]) any())).thenReturn(1);

        boolean result = repository.deleteById(UUID.randomUUID());

        assertTrue(result);
    }

    @Test
    @DisplayName("проверка метода deleteById, неудача")
    void deleteById_ShouldReturnFalse_WhenZeroRowsAffected() {
        when(jdbcTemplate.update(anyString(), (Object[]) any())).thenReturn(0);

        boolean result = repository.deleteById(UUID.randomUUID());

        assertFalse(result);
    }

    @Test
    @DisplayName("корректно преобразует данные из базы")
    void findById_ShouldMapRowCorrectly() throws Exception {
        UUID id = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        List<RuleCondition> conditions = List.of(new RuleCondition("amount", List.of(">500"), false));
        String ruleJson = new ObjectMapper().writeValueAsString(conditions);

        RecommendationRule expectedRule = new RecommendationRule(id, "карта", productId, "получи все блага", conditions);
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(id)))
                .thenReturn(List.of(expectedRule));

        Optional<RecommendationRule> result = repository.findById(id);

        assertTrue(result.isPresent());
        assertEquals(expectedRule.getId(), result.get().getId());
        assertEquals(expectedRule.getProductName(), result.get().getProductName());
        assertEquals(expectedRule.getProductText(), result.get().getProductText());
        assertEquals(expectedRule.getRule().size(), result.get().getRule().size());
        assertEquals(expectedRule.getRule().get(0).getQuery(), result.get().getRule().get(0).getQuery());
    }

}