package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

/**
 * Handles the /help command, which shows a list of all available bot commands and their descriptions.
 * This command collects all {@link BotCommand} implementations and displays their names and descriptions.
 */
@Component
public class HelpCommand implements BotCommand {

    private final Set<BotCommand> botCommands;
    private final BotMessageSender botMessageSender;

    public HelpCommand(Set<BotCommand> botCommands, BotMessageSender botMessageSender) {
        this.botCommands = botCommands;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /help command.
     * Builds a help message containing all available commands and their descriptions, and sends it to the user.
     *
     * @param update the incoming update from Telegram containing the command
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();

        StringBuilder helpMessage = new StringBuilder();
        helpMessage.append("Доступные команды:\n\n");

        for (BotCommand command : botCommands) {
            helpMessage.append(command.getName()).append(" - ").append(command.getDescription()).append("\n\n");
        }

        botMessageSender.sendMessage(chatId, helpMessage.toString(), false);
    }

    @Override
    public String getName() {
        return "/help";
    }

    @Override
    public String getDescription() {
        return "Показывает список всех доступных команд и их описание.";
    }
}