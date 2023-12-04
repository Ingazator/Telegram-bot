package org.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CurrencyBot extends TelegramLongPollingBot {

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();
            String username = update.getMessage().getChat().getUserName();

            if (messageText.equals("/start")) {
                String welcomeMessage = "Hello, @" + username + "!\nWelcome to the Currency Converter Bot. You can use the following commands:\n";
                welcomeMessage += "/help - Show available commands";
                SendMessage welcomeSendMessage = new SendMessage();
                welcomeSendMessage.setChatId(chatId);
                welcomeSendMessage.setText(welcomeMessage);

                try {
                    execute(welcomeSendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (messageText.equals("/help")) {
                String helpMessage = "Available commands:\n";
                helpMessage += "/convert <amount> <base_currency> <target_currency> - Convert currency";
                SendMessage helpSendMessage = new SendMessage();
                helpSendMessage.setChatId(chatId);
                helpSendMessage.setText(helpMessage);

                try {
                    execute(helpSendMessage);
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            } else if (messageText.startsWith("/convert")) {
                String[] commandParts = messageText.split(" ");
                if (commandParts.length == 4) {
                    try {
                        double amount = Double.parseDouble(commandParts[1]);
                        String baseCurrency = commandParts[2].toUpperCase();
                        String targetCurrency = commandParts[3].toUpperCase();

                        double convertedAmount = convertCurrency(amount, baseCurrency, targetCurrency);

                        String responseText = String.format("Converted amount: %.2f %s", convertedAmount, targetCurrency);
                        SendMessage message = new SendMessage();
                        message.setChatId(chatId);
                        message.setText(responseText);

                        execute(message);
                    } catch (NumberFormatException e) {
                        sendErrorMessage(chatId, "Invalid amount format. Please enter a valid number.");
                    } catch (IOException e) {
                        sendErrorMessage(chatId, "Failed to convert currency. Please try again later.");
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    sendErrorMessage(chatId, "Invalid command format. Please use the following format: /convert <amount> <base_currency> <target_currency>");
                }
            }
        }
    }

    private double convertCurrency(double amount, String baseCurrency, String targetCurrency) throws IOException {
        String apiUrl = "https://api.exchangerate-api.com/v4/latest/" + baseCurrency;

        URL url = new URL(apiUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        double exchangeRate = extractExchangeRate(response.toString(), targetCurrency);
        return amount * exchangeRate;
    }

    private double extractExchangeRate(String response, String targetCurrency) {
        int startIndex = response.indexOf("\"" + targetCurrency + "\":");
        int endIndex = response.indexOf(",", startIndex);

        String exchangeRateStr = response.substring(startIndex, endIndex);

        return Double.parseDouble(exchangeRateStr.split(":")[1]);
    }

    private void sendErrorMessage(long chatId, String errorMessage) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(errorMessage);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBotUsername() {
        return "https://t.me/CurrencyConverter02Bot";
    }

    @Override
    public String getBotToken() {
        return "6734190408:AAEoBCOJj8wFE7KIr0MYNInYk46LHGLC5fg";
    }
}