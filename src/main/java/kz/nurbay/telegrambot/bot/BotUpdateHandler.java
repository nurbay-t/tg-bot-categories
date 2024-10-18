package kz.nurbay.telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

/**
 * Handles incoming updates from the Telegram bot.
 */
@Component
public class BotUpdateHandler {

    private static final Logger log = LoggerFactory.getLogger(BotUpdateHandler.class);
    /**
     * Handles valid bot commands that start with a slash ("/").
     */
    private final BotCommandHandler commandHandler;
    /**
     * Handles invalid messages that are not commands.
     */
    private final BotInvalidMessageHandler invalidMessageHandler;


    public BotUpdateHandler(BotCommandHandler commandHandler, BotInvalidMessageHandler botInvalidMessageHandler) {
        this.commandHandler = commandHandler;
        this.invalidMessageHandler = botInvalidMessageHandler;
    }

    /**
     * Processes incoming Telegram updates.
     * If the message starts with a slash ("/"), it is treated as a command and passed to the command handler.
     * Otherwise, it is considered an invalid message and handled by the invalid message handler.
     *
     * @param update the incoming update from Telegram
     */
    public void handleUpdate(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();

            if (messageText.startsWith("/")) {
                try {
                    commandHandler.handleCommand(update);
                } catch (TelegramApiException e) {
                    log.error(e.getMessage());
                }
            } else {
                invalidMessageHandler.handleInvalidMessage(update);
            }
        }
    }
}
