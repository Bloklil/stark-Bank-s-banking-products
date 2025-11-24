package org.skypro.recommendationservice.configuration;

import org.skypro.recommendationservice.service.TelegramRecommendationBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
public class TelegramBotConfiguration {

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Bean
    public TelegramBotsApi telegramBotsApi(TelegramRecommendationBot bot) {
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot);
            return botsApi;
        } catch (TelegramApiException e) {
            throw new RuntimeException("Failed to register Telegram bot", e);
        }
    }

    @Bean
    public String botToken() {
        return botToken;
    }

    @Bean
    public String botUsername() {
        return botUsername;
    }

}
