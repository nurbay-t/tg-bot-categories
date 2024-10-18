package kz.nurbay.telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * TelegramBot is the main bot class responsible for handling incoming Telegram updates.
 */
@Component
public class TelegramBot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(TelegramBot.class);
    /**
     * The bot's authentication token retrieved from application properties.
     */
    private final String botToken;
    /**
     * The handler responsible for processing incoming Telegram updates.
     */
    private final BotUpdateHandler updateHandler;

    public TelegramBot(@Value("${telegram.bot.token}") String botToken, BotUpdateHandler updateHandler) {
        this.botToken = botToken;
        this.updateHandler = updateHandler;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        updateHandler.handleUpdate(update);
    }
}

