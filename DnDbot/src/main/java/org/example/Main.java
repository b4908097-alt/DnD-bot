package org.example;
import java.net.ServerSocket;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Random;

public class Main {

    public static class MyBot extends TelegramLongPollingBot {

        private final Random random = new Random();

        @Override
        public String getBotUsername() {
            return "DnD bot";
        }

        @Override
        public String getBotToken() {
            // Вставьте сюда ваш актуальный токен от @BotFather без лишних пробелов
            return "8804849662:AAFzM9065_seVlUTCJC5__PdWF5Yi5iGxNI";
        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String chatId = update.getMessage().getChatId().toString();
                String text = update.getMessage().getText();

                String answer = null;

                // Если текст начинается с "/d20"
if (messageText.startsWith("/d20")) {
    // 1. Разбиваем строку по пробелам
    String[] parts = messageText.split(" ");
    
    // Бросаем кубик d20
    int roll = random.nextInt(20) + 1;
    
    // 2. Проверяем, ввёл ли пользователь второе значение (например, 15)
    if (parts.length > 1) {
        try {
            // Преобразуем второй аргумент в число
            int target = Integer.parseInt(parts[1]);
            
            // 3. Сравниваем результат броска с числом проверки
            if (roll >= target) {
                sendMessage(chatId, "🎲 Выпало: " + roll + " (КС " + target + ")\nПоздравляю путник, ты прошел проверку:)");
            } else {
                sendMessage(chatId, "🎲 Выпало: " + roll + " (КС " + target + ")\nК сожалению путник, но ты провалил проверку:(");
            }
        } catch (NumberFormatException e) {
            // Если вместо числа ввели текст, например "/d20 abc"
            sendMessage(chatId, "Укажите число сложности корректно, например: /d20 15");
        }
    } else {
        // Если пользователь написал просто "/d20" без сложности
        sendMessage(chatId, "🎲 Бросок d20: " + roll);
    }
}

                if (text.equalsIgnoreCase("/start")) {
                    answer = "Приветствую путник ⚔️. \nЗдесь ты сможешь испытать свою удачу, а именнонажми: /d20 или напиши 'кубик', чтобы бросить d20! /nУдачи 🍀";
                } else if (text.equalsIgnoreCase("/d20") || text.equalsIgnoreCase("кубик") || text.equalsIgnoreCase("d20")) {
                    int diceResult = random.nextInt(20) + 1;

                    if (diceResult == 20) {
                        answer = "🎲 Выпало: 20! 🎯 КРИТИЧЕСКИЙ УСПЕХ!  (а ты удачливый)";
                    } else if (diceResult == 1) {
                        answer = "🎲 Выпало: 1! 💀 КРИТИЧЕСКИЙ ПРОВАЛ!  (не повезло не фортануло)";
                    } else {
                        answer = "🎲 Выпало: " + diceResult;
                    }
                }

                // Отправка сообщения происходит ТОЛЬКО при командах /start, /d20 или "кубик"
                if (answer != null) {
                    SendMessage message = new SendMessage();
                    message.setChatId(chatId);
                    message.setText(answer);

                    try {
                        execute(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(new MyBot());
        System.out.println("Бот запущен!");

        // Настоящий мини-HTTP сервер для Render
try {
    String portStr = System.getenv("PORT");
    int port = (portStr != null) ? Integer.parseInt(portStr) : 10000;

    com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new java.net.InetSocketAddress(port), 0);
    server.createContext("/", exchange -> {
        String response = "Bot is running!";
        exchange.sendResponseHeaders(200, response.length());
        java.io.OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    });
    server.start();
    System.out.println("HTTP-сервер запущен на порту " + port);
} catch (Exception e) {
    e.printStackTrace();
}
    }

}
