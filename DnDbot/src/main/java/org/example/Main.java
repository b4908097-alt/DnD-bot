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
        // 1. Запуск микро-HTTP сервера для удовлетворения проверок Render
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
            // Укажи здесь юзернейм своего бота без символа @
            return "D&D bot";
        }

        @Override
        public String getBotToken() {
            // Укажи здесь токен бота от BotFather
            return "8804849662:AAFzM9065_seVlUTCJC5__PdWF5Yi5iGxNI";
        }

        @Override
        public void onUpdateReceived(Update update) {
            if (update.hasMessage() && update.getMessage().hasText()) {
                String originalText = update.getMessage().getText().trim();
                String messageText = originalText.toLowerCase();
                long chatId = update.getMessage().getChatId();

                // Команда /start с отправкой меню
                if (messageText.equals("/start")) {
                    sendMenu(chatId, "Приветствую путник ⚔️.\nЗдесь ты сможешь испытать свою удачу! Нажми на кнопку кубика или напиши команду с проверкой сложности, например: /d20 15");
                    return;
                }

                // Обработка броска d20 с опциональной проверкой сложностей (/d20 15 или d20 15)
                if (messageText.startsWith("/d20") || messageText.startsWith("d20")) {
                    String[] parts = originalText.split("\\s+");
                    int roll = random.nextInt(20) + 1;

                    if (parts.length > 1) {
                        try {
                            int target = Integer.parseInt(parts[1]);
                            if (roll >= target) {
                                sendMessage(chatId, "🎲 Выпало: " + roll + " (КС " + target + ")\nПоздравляю путник, ты прошел проверку:)");
                            } else {
                                sendMessage(chatId, "🎲 Выпало: " + roll + " (КС " + target + ")\nК сожалению путник, но ты провалил проверку:(");
                            }
                        } catch (NumberFormatException e) {
                            sendMessage(chatId, "Укажите число сложности корректно, например: /d20 15");
                        }
                    } else {
                        sendMessage(chatId, "🎲 Бросок d20: " + roll);
                    }
                    return;
                }

                // Нормализация прочих команд (/d4 -> d4)
                if (messageText.startsWith("/")) {
                    messageText = messageText.substring(1);
                }

                // Обработка остальных кубиков
                switch (messageText) {
                    case "d4":
                        rollDice(chatId, 4);
                        break;
                    case "d6":
                        rollDice(chatId, 6);
                        break;
                    case "d8":
                        rollDice(chatId, 8);
                        break;
                    case "d10":
                        rollDice(chatId, 10);
                        break;
                    case "d12":
                        rollDice(chatId, 12);
                        break;
                    case "d100":
                        rollDice(chatId, 100);
                        break;
                    default:
                        sendMessage(chatId, "Неизвестная команда. Используй кнопки или напиши, например: /d20 15");
                        break;
                }
            }
        }

        // Метод броска кубика
        private void rollDice(long chatId, int faces) {
            int result = random.nextInt(faces) + 1;
            sendMessage(chatId, "🎲 Бросок d" + faces + ": " + result);
        }

        // Отправка сообщений с интерактивным меню
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
