package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import kz.nurbay.telegrambot.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handles the /start command, which initializes a new user in the system if they do not already exist.
 */
@Component
public class StartCommand implements BotCommand {

    private static final Logger log = LoggerFactory.getLogger(StartCommand.class);
    private final BotMessageSender botMessageSender;
    private final UserService userService;

    public StartCommand(UserService userService, BotMessageSender botMessageSender) {
        this.userService = userService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /start command.
     * Checks if the user already exists in the system; if not, a new user is created.
     * Sends a welcome message to the user to introduce the bot and its basic functionalities.
     *
     * @param update the incoming update from Telegram containing the command
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        userService.createUserIfNotExists(userId);

        String welcomeMessage = "Добро пожаловать! Я ваш бот для создания дерева категории. " +
                "Используйте /help для списка доступных команд.";
        botMessageSender.sendMessage(chatId, welcomeMessage, false);
    }

    @Override
    public String getName() {
        return "/start"; // Имя команды
    }

    @Override
    public String getDescription() {
        return "Инициализирует взаимодействие с ботом и регистрирует вас, если вы еще не были зарегистрированы.";
    }
}
