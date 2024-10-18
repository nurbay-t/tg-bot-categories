package kz.nurbay.telegrambot.command;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Represents a command that can be executed by the Telegram bot.
 * Each command should have a unique name, a description, and logic to execute the command.
 */
public interface BotCommand {

    /**
     * Executes the command based on the provided Telegram update.
     *
     * @param update the incoming update from Telegram that contains the command and its context
     */
    void execute(Update update);

    /**
     * Returns the unique name of the command.
     * This name is used to map the command in the bot (e.g., "/start").
     *
     * @return the name of the command
     */
    String getName();

    /**
     * Returns a brief description of the command.
     * This is used to inform the user about the purpose of the command (in the /help menu).
     *
     * @return the description of the command
     */
    String getDescription();
}

