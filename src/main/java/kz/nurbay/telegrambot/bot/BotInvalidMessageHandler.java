package kz.nurbay.telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handles invalid messages that do not start with a command (i.e., messages without a "/").
 */
@Component
public class BotInvalidMessageHandler {

    private static final Logger log = LoggerFactory.getLogger(BotInvalidMessageHandler.class);
    private final BotMessageSender botMessageSender;

    public BotInvalidMessageHandler(BotMessageSender botMessageSender) {
        this.botMessageSender = botMessageSender;
    }

    /**
     * Handles invalid messages by sending a predefined response to the user.
     *
     * @param update the incoming update from Telegram containing the invalid message
     */
    public void handleInvalidMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        String response = "Я принимаю только команды, ознакомиться со списком команд можно через команду /help";
        botMessageSender.sendMessage(chatId, response, false);
    }
}
