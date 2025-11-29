package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.model.ServiceInfo;
import org.skypro.recommendationservice.service.CachedRecommendationsService;
import org.skypro.recommendationservice.service.RecommendationRuleService;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/management")
public class CacheManagementController {
    private final CachedRecommendationsService cachedRecommendationsService;
    private final BuildProperties buildProperties;

    public CacheManagementController(CachedRecommendationsService cachedRecommendationsService,
                                     BuildProperties buildProperties) {
        this.cachedRecommendationsService = cachedRecommendationsService;
        this.buildProperties = buildProperties;
    }

    @GetMapping("/info")
    public ResponseEntity<ServiceInfo> getServiceInfo() {
        try {
            ServiceInfo info = new ServiceInfo(
                    buildProperties.getName(),
                    buildProperties.getVersion()
            );
            return ResponseEntity.ok(info);
        } catch (Exception e) {
            // Fallback если BuildProperties недоступен
            ServiceInfo info = new ServiceInfo("recommendation-service", "1.0.0");
            return ResponseEntity.ok(info);
        }
    }

    @PostMapping("/clear-caches")
    public ResponseEntity<String> clearCaches() {
        try {
            cachedRecommendationsService.clearCaches();
            return ResponseEntity.ok("✅ Все кеши успешно очищены");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ Ошибка при очистке кешей: " + e.getMessage());
        }
    }

}
