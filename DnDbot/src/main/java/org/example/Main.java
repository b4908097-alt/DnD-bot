package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

    public static void main(String[] args) {
        // 1. Запуск микро-HTTP сервера для проверок Render
        try {
            String portStr = System.getenv("PORT");
            int port = (portStr != null) ? Integer.parseInt(portStr) : 10000;

            com.sun.net.httpserver.HttpServer server = com.sun.net.httpserver.HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", exchange -> {
                String response = "Bot is running!";
                exchange.sendResponseHeaders(200, response.length());
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            });
            server.start();
            System.out.println("HTTP-сервер запущен на порту " + port);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 2. Регистрация и запуск Telegram бота
        try {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(new MyBot());
            System.out.println("Бот запущен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Вложенный класс бота
    public static class MyBot extends TelegramLongPollingBot {

        private final Random random = new Random();

        @Override
        public String getBotUsername() {
            // Юзернейм бота без символа @ (например, DnnD20_bot)
            return "DnnD20_bot"; 
        }

        @Override
        public String getBotToken() {
            // Твой токен от BotFather
            return "8804849662:AAFzM9065_seVlUTCJC5__PdWF5Yi5iGxNI";
        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String originalText = update.getMessage().getText().trim();
                long chatId = update.getMessage().getChatId();

                // 1. Очищаем текст от юзернейма бота (для поддержки групп)
                String cleanText = originalText.replaceAll("@" + getBotUsername(), "");
                String messageText = cleanText.toLowerCase();

                // 2. Команда /start с инструкцией
                if (messageText.equals("/start")) {
                    sendMenu(chatId, "Приветствую путник ⚔️.\nЗдесь ты сможешь испытать свою удачу! Нажми на кнопку кубика или напиши команду с проверкой сложности, например: /d20 15");
                    sendMessage(chatId, "Так же сейчас я проведу небольшой инструктаж специально для тебя:\nТы можешь использовать команды для всех кубиков D&D:\nd4, d6, d8, d10, d12, d20, а также d100.\nС уважением, администрация D&D Bot!");
                    return;
                }

                // 3. Обработка всех бросков через единый массив частей
                String[] parts = cleanText.split("\\s+");

                // d4
                if (messageText.startsWith("/d4") || messageText.startsWith("d4")) {
                    handleRoll(chatId, 4, parts);
                    return;
                }

                // d6
                if (messageText.startsWith("/d6") || messageText.startsWith("d6")) {
                    handleRoll(chatId, 6, parts);
                    return;
                }

                // d8
                if (messageText.startsWith("/d8") || messageText.startsWith("d8")) {
                    handleRoll(chatId, 8, parts);
                    return;
                }

                // d100 (ПРОВЕРЯЕМ РАНЬШЕ d10, чтобы не перехватывался префикс!)
                if (messageText.startsWith("/d100") || messageText.startsWith("d100")) {
                    handleRoll(chatId, 100, parts);
                    return;
                }

                // d10
                if (messageText.startsWith("/d10") || messageText.startsWith("d10")) {
                    handleRoll(chatId, 10, parts);
                    return;
                }

                // d12
                if (messageText.startsWith("/d12") || messageText.startsWith("d12")) {
                    handleRoll(chatId, 12, parts);
                    return;
                }

                // d20
                if (messageText.startsWith("/d20") || messageText.startsWith("d20")) {
                    handleRoll(chatId, 20, parts);
                    return;
                }
            }
        }
        
        private void handleRoll(long chatId, int faces, String[] parts) {
    int roll = random.nextInt(faces) + 1;

    if (parts.length > 1) {
        try {
            int target = Integer.parseInt(parts[1]);
            if (roll >= target) {
                sendMessage(chatId, "🎲 Выпало: " + roll + " (Проверка " + target + ")\nПоздравляю путник, ты прошел проверку:)");
            } else {
                sendMessage(chatId, "🎲 Выпало: " + roll + " (Проверка " + target + ")\nК сожалению путник, но ты провалил проверку:(");
            }
        } catch (NumberFormatException e) {
            sendMessage(chatId, "Укажите число сложности корректно, например: /d" + faces + " 15");
        }
    } else {
        // Обычный бросок кубика
        if (roll == faces) {
            sendMessage(chatId, "🎲 Бросок d" + faces + ": " + roll + "\nПоздравляю у тебя критический успех, а ты хорош.");
        } else if (roll == 1) {
            sendMessage(chatId, "🎲 Бросок d" + faces + ": " + roll + "\nК большому сожалению тебе выпал критический провал.\nВ следующий раз повезет больше.");
        } else {
            sendMessage(chatId, "🎲 Бросок d" + faces + ": " + roll);
        }
    }
}

        // Вспомогательный метод броска
        private void rollDice(long chatId, int faces) {
            int result = random.nextInt(faces) + 1;
            sendMessage(chatId, "🎲 Бросок d" + faces + ": " + result);
        }

        // Отправка клавиатуры
        private void sendMenu(long chatId, String text) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);

            ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
            keyboardMarkup.setSelective(true);
            keyboardMarkup.setResizeKeyboard(true);
            keyboardMarkup.setOneTimeKeyboard(false);

            List<KeyboardRow> keyboard = new ArrayList<>();

            KeyboardRow row1 = new KeyboardRow();
            row1.add(new KeyboardButton("d4"));
            row1.add(new KeyboardButton("d6"));
            row1.add(new KeyboardButton("d8"));

            KeyboardRow row2 = new KeyboardRow();
            row2.add(new KeyboardButton("d10"));
            row2.add(new KeyboardButton("d12"));
            row2.add(new KeyboardButton("d20"));

            KeyboardRow row3 = new KeyboardRow();
            row3.add(new KeyboardButton("d100"));

            keyboard.add(row1);
            keyboard.add(row2);
            keyboard.add(row3);

            keyboardMarkup.setKeyboard(keyboard);
            message.setReplyMarkup(keyboardMarkup);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        // Вспомогательный метод отправки обычных сообщений
        private void sendMessage(long chatId, String text) {
            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(chatId));
            message.setText(text);
            try {
                execute(message);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
}
