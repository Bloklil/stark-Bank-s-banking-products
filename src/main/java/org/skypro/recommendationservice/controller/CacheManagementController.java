package org.skypro.recommendationservice.controller;

import org.skypro.recommendationservice.model.ServiceInfo;
import org.skypro.recommendationservice.service.CachedRecommendationsService;
import org.skypro.recommendationservice.service.RecommendationRuleService;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${spring.application.name:recommendation-service}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String serviceVersion;

    public CacheManagementController(CachedRecommendationsService cachedRecommendationsService) {
        this.cachedRecommendationsService = cachedRecommendationsService;
    }

    @GetMapping("/info")
    public ResponseEntity<ServiceInfo> getServiceInfo() {
        ServiceInfo info = new ServiceInfo(serviceName, serviceVersion);
        return ResponseEntity.ok(info);
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
