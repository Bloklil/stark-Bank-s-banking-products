package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.UUID;

@Service
public class TelegramRecommendationBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final RecommendationService recommendationService;

    public TelegramRecommendationBot(@Value("${telegram.bot.token}") String botToken,
                                     @Value("${telegram.bot.username}") String botUsername,
                                     RecommendationService recommendationService) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.recommendationService = recommendationService;
    }

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();

            try {
                handleMessage(chatId, messageText);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleMessage(Long chatId, String messageText) throws TelegramApiException {
        if (messageText.startsWith("/start")) {
            sendWelcomeMessage(chatId);
        } else if (messageText.startsWith("/recommend")) {
            handleRecommendationCommand(chatId, messageText);
        } else if (messageText.startsWith("/help")) {
            sendHelpMessage(chatId);
        } else {
            sendUnknownCommandMessage(chatId);
        }
    }

    private void sendWelcomeMessage(Long chatId) throws TelegramApiException {
        String welcomeText = """
                🏦 Добро пожаловать в Recommendation Bot! 🏦
                
                Я помогу вам получить персональные финансовые рекомендации.
                
                Доступные команды:
                /recommend <user_id> - Получить рекомендации для пользователя
                /help - Показать справку
                
                Пример использования:
                /recommend ebe958b5-44be-4adb-9ec2-680a3565c23a
                """;

        sendMessage(chatId, welcomeText);
    }

    private void handleRecommendationCommand(Long chatId, String messageText) throws TelegramApiException {
        String[] parts = messageText.split(" ");

        if (parts.length != 2) {
            sendMessage(chatId, "❌ Неправильный формат команды. Используйте: /recommend <user_id>");
            return;
        }

        try {
            UUID userId = UUID.fromString(parts[1]);
            List<Recommendation> recommendations = recommendationService.getRecommendations(userId);

            if (recommendations.isEmpty()) {
                sendMessage(chatId, "📭 Для пользователя " + userId + " нет доступных рекомендаций.");
            } else {
                sendRecommendations(chatId, userId, recommendations);
            }

        } catch (IllegalArgumentException e) {
            sendMessage(chatId, "❌ Неверный формат UUID. Проверьте правильность идентификатора пользователя.");
        } catch (Exception e) {
            sendMessage(chatId, "❌ Произошла ошибка при получении рекомендаций: " + e.getMessage());
        }
    }

    private void sendRecommendations(Long chatId, UUID userId, List<Recommendation> recommendations) throws TelegramApiException {
        StringBuilder message = new StringBuilder();
        message.append("🎯 Персональные рекомендации для пользователя ").append(userId).append("\n\n");

        for (int i = 0; i < recommendations.size(); i++) {
            Recommendation rec = recommendations.get(i);
            message.append(i + 1).append(". ").append(rec.getName()).append("\n");
            message.append("📝 ").append(rec.getText()).append("\n\n");
        }

        message.append("💡 Всего найдено рекомендаций: ").append(recommendations.size());


        if (message.length() > 4096) {
            sendLongMessage(chatId, message.toString());
        } else {
            sendMessage(chatId, message.toString());
        }
    }

    private void sendHelpMessage(Long chatId) throws TelegramApiException {
        String helpText = """
                📖 Справка по командам бота:
                
                /start - Начать работу с ботом
                /recommend <user_id> - Получить рекомендации для пользователя
                /help - Показать эту справку
                
                Примеры:
                /recommend ebe958b5-44be-4adb-9ec2-680a3565c23a
                /recommend 550e8400-e29b-41d4-a716-446655440000
                
                💡 User ID должен быть в формате UUID.
                """;

        sendMessage(chatId, helpText);
    }

    private void sendUnknownCommandMessage(Long chatId) throws TelegramApiException {
        sendMessage(chatId, "❌ Неизвестная команда. Используйте /help для просмотра доступных команд.");
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        execute(message);
    }

    private void sendLongMessage(Long chatId, String longText) throws TelegramApiException {

        int chunkSize = 4096;
        for (int i = 0; i < longText.length(); i += chunkSize) {
            String chunk = longText.substring(i, Math.min(longText.length(), i + chunkSize));
            sendMessage(chatId, chunk);
        }
    }


    public void sendAdminNotification(String message) throws TelegramApiException {

        String adminChatId = "YOUR_ADMIN_CHAT_ID";
        sendMessage(Long.parseLong(adminChatId), "🔔 " + message);
    }


    public void sendRecommendationsToUser(String telegramChatId, UUID userId) throws TelegramApiException {
        List<Recommendation> recommendations = recommendationService.getRecommendations(userId);
        sendRecommendations(Long.parseLong(telegramChatId), userId, recommendations);
    }

}
