package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import kz.nurbay.telegrambot.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handles the /removeElement command, which allows users to remove a category element and its children
 * from the category tree.
 */
@Component
public class RemoveElementCommand implements BotCommand {

    private static final Logger log = LoggerFactory.getLogger(RemoveElementCommand.class);
    private final CategoryService categoryService;
    private final BotMessageSender botMessageSender;

    public RemoveElementCommand(CategoryService categoryService, BotMessageSender botMessageSender) {
        this.categoryService = categoryService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /removeElement command.
     * Removes the specified category element along with all its children.
     * If the element does not exist, an appropriate message is sent to the user.
     *
     * @param update the incoming update from Telegram containing the command
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        String[] parts = update.getMessage().getText().split(" ", 2);

        String response;
        if (parts.length != 2) {
            response = "Пожалуйста, укажите название одного элемента. Пример: /removeElement элемент";
            botMessageSender.sendMessage(chatId, response, false);
            return;
        }

        String elementName = parts[1];
        boolean elementRemoved = categoryService.removeElementWithChildren(chatId, elementName);

        if (elementRemoved) {
            response = "Элемент \"" + elementName + "\" и его дочерние элементы успешно удалены.";
        } else {
            response = "Элемент \"" + elementName + "\" не найден.";
        }
        botMessageSender.sendMessage(chatId, response, false);
    }

    @Override
    public String getName() {
        return "/removeElement";
    }

    @Override
    public String getDescription() {
        return "Удаляет элемент и все его дочерние элементы. \nПример: /removeElement <название элемента>.";
    }
}

