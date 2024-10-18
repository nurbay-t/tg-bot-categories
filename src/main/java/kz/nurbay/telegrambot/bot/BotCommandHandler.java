package kz.nurbay.telegrambot.bot;

import kz.nurbay.telegrambot.command.BotCommand;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles valid bot commands by mapping each command to its corresponding handler.
 * This class loads all available {@link BotCommand} beans and stores them in a map.
 * If an unknown command is received, it sends an appropriate response to the user.
 */
@Component
public class BotCommandHandler {
    /**
     * A map of command names to their corresponding {@link BotCommand} implementations.
     */
    private final Map<String, BotCommand> commandMap = new HashMap<>();
    /**
     * Responsible for sending messages back to the user.
     */
    private final BotMessageSender botMessageSender;

    public BotCommandHandler(ApplicationContext applicationContext, BotMessageSender botMessageSender) {
        this.botMessageSender = botMessageSender;

        Map<String, BotCommand> beans = applicationContext.getBeansOfType(BotCommand.class);
        for (BotCommand command : beans.values()) {
            commandMap.put(command.getName(), command);
        }
    }

    /**
     * Handles an incoming command by executing the corresponding {@link BotCommand} handler.
     * If the command is not found in the map, it delegates handling to {@link #handleUnknownCommand(Update)}.
     *
     * @param update the incoming update from Telegram containing the command
     * @throws TelegramApiException if there is an issue sending a response to Telegram
     */
    public void handleCommand(Update update) throws TelegramApiException {
        String messageText = update.getMessage().getText().split(" ")[0];
        BotCommand command = commandMap.get(messageText);

        if (command != null) {
            command.execute(update);
        } else {
            handleUnknownCommand(update);
        }
    }

    /**
     * Handles unknown or unrecognized commands by sending a default response to the user.
     *
     * @param update the incoming update containing the unrecognized command
     * @throws TelegramApiException if there is an issue sending a response to Telegram
     */
    private void handleUnknownCommand(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String response = "Неизвестная команда. Пожалуйста, используйте /help для списка доступных команд.";
        botMessageSender.sendMessage(chatId, response, false);
    }
}

