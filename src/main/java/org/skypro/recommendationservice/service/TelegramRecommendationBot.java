package org.skypro.recommendationservice.service;

import org.skypro.recommendationservice.model.Recommendation;
import org.skypro.recommendationservice.model.User;
import org.skypro.recommendationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TelegramRecommendationBot extends TelegramLongPollingBot {

    private final String botToken;
    private final String botUsername;
    private final RecommendationService recommendationService;
    private final UserRepository userRepository;

    public TelegramRecommendationBot(@Value("${telegram.bot.token}") String botToken,
                                     @Value("${telegram.bot.username}") String botUsername,
                                     RecommendationService recommendationService,
                                     UserRepository userRepository) {
        this.botToken = botToken;
        this.botUsername = botUsername;
        this.recommendationService = recommendationService;
        this.userRepository = userRepository;
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
            handleRecommendCommand(chatId, messageText);
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
                /recommend username - Получить рекомендации для пользователя
                /help - Показать справку
                
                Пример использования:
                /recommend ivanov
                /recommend petrov
                """;

        sendMessage(chatId, welcomeText);
    }


    private void handleRecommendCommand(Long chatId, String messageText) throws TelegramApiException {
        String[] parts = messageText.split(" ", 2);

        if (parts.length != 2) {
            sendMessage(chatId, "❌ Неправильный формат команды. Используйте: /recommend username");
            return;
        }

        String username = parts[1].trim();

        if (username.isEmpty()) {
            sendMessage(chatId, "❌ Укажите username пользователя. Пример: /recommend ivanov");
            return;
        }

        try {
            // Ищем пользователя по username
            Optional<User> userOptional = userRepository.findByUsername(username);

            if (userOptional.isEmpty()) {
                // Если пользователь не найден, проверяем нет ли нескольких похожих
                List<User> similarUsers = userRepository.findUsersByPartialUsername(username);
                if (similarUsers.size() > 1) {
                    sendMultipleUsersFound(chatId, similarUsers);
                } else {
                    sendMessage(chatId, "❌ Пользователь '" + username + "' не найден");
                }
                return;
            }

            User user = userOptional.get();
            List<Recommendation> recommendations = recommendationService.getRecommendations(user.getId());

            sendRecommendations(chatId, user, recommendations);

        } catch (Exception e) {
            sendMessage(chatId, "❌ Произошла ошибка при получении рекомендаций: " + e.getMessage());
        }
    }

    private void sendRecommendations(Long chatId, User user, List<Recommendation> recommendations) throws TelegramApiException {
        StringBuilder message = new StringBuilder();

        message.append("👋 Здравствуйте, ").append(user.getFullName()).append("!\n\n");

        if (recommendations.isEmpty()) {
            message.append("📭 К сожалению, для вас нет доступных рекомендаций в данный момент.\n\n");
            message.append("💡 Рекомендуем обратиться к финансовому консультанту для получения персональных предложений.");
        } else {
            message.append("🎯 Новые продукты для вас:\n\n");

            for (int i = 0; i < recommendations.size(); i++) {
                Recommendation rec = recommendations.get(i);
                message.append("▫️ ").append(rec.getName()).append("\n");
                message.append("   ").append(rec.getText()).append("\n\n");
            }

            message.append("💼 Всего найдено рекомендаций: ").append(recommendations.size());
        }
        sendMessage(chatId, message.toString());
    }

    private void sendMultipleUsersFound(Long chatId, List<User> users) throws TelegramApiException {
        StringBuilder message = new StringBuilder();
        message.append("🔍 Найдено несколько пользователей:\n\n");

        for (User user : users) {
            message.append("• ").append(user.getUsername())
                    .append(" (").append(user.getFullName()).append(")\n");
        }

        message.append("\n💡 Уточните username пользователя.");

        sendMessage(chatId, message.toString());
    }

    private void sendHelpMessage(Long chatId) throws TelegramApiException {
        String helpText = """
                📖 Справка по командам бота:
                
                /start - Начать работу с ботом
                /recommend username - Получить рекомендации для пользователя
                /help - Показать эту справку
                
                Примеры:
                /recommend ivanov
                
                💡 Username - это уникальное имя пользователя в системе.
                Для поиска можно использовать часть имени пользователя.
                """;

        sendMessage(chatId, helpText);
    }

    private void sendUnknownCommandMessage(Long chatId) throws TelegramApiException {
        String unknownCommandText = """
                ❌ Неизвестная команда.
                
                Доступные команды:
                /start - Начать работу
                /recommend username - Получить рекомендации
                /help - Показать справку
                
                Используйте /help для подробной информации.
                """;
        sendMessage(chatId, unknownCommandText);
    }

    private void sendMessage(Long chatId, String text) throws TelegramApiException {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        execute(message);
    }
}
