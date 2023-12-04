package org.example;

import lombok.SneakyThrows;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class App {
    @SneakyThrows
    public static void main(String[] args ) {
        TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
        try {
            api.registerBot(new CurrencyBot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
}
