package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import kz.nurbay.telegrambot.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handles the /viewTree command, which allows users to view the structure of their category tree.
 */
@Component
public class ViewTreeCommand implements BotCommand {

    private static final Logger log = LoggerFactory.getLogger(ViewTreeCommand.class);

    private final CategoryService categoryService;
    private final BotMessageSender botMessageSender;

    public ViewTreeCommand(CategoryService categoryService, BotMessageSender botMessageSender) {
        this.categoryService = categoryService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /viewTree command.
     * Checks if the user has a root category. If not, a message is sent suggesting the user create a root element.
     * If a tree exists, it sends the tree structure as a formatted message.
     *
     * @param update the incoming update from Telegram containing the command
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();

        boolean rootExists = categoryService.rootElementExists(userId);

        if (!rootExists) {
            String response = "У вас еще нет дерева. " +
                    "Создайте корневой элемент с помощью команды /addElement <название>";
            botMessageSender.sendMessage(chatId, response, false);

        } else {
            String treeStructure = categoryService.getTreeStructure(userId);
            botMessageSender.sendMessage(chatId, treeStructure, true);
        }
    }

    @Override
    public String getName() {
        return "/viewTree";
    }

    @Override
    public String getDescription() {
        return "Отображает дерево в структурированном виде.";
    }
}

