package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import kz.nurbay.telegrambot.service.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Handles the addition of new categories in the bot's category tree.
 * This command allows users to add either a root element or a child element to an existing parent.
 * Usage in telegram bot:
 * - /addElement <element> - Adds a root element if one does not already exist.
 * - /addElement <parent> <element> - Adds a child element to the specified parent element.
 */
@Component
public class AddElementCommand implements BotCommand {

    private static final Logger log = LoggerFactory.getLogger(AddElementCommand.class);
    private final CategoryService categoryService;
    private final BotMessageSender botMessageSender;

    public AddElementCommand(CategoryService categoryService, BotMessageSender botMessageSender) {
        this.categoryService = categoryService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /addElement command to add either a root or child element.
     * Validates the input and determines whether to add a root element or a child element under a parent.
     *
     * @param update the incoming update from Telegram containing the command and its arguments
     */
    @Override
    public void execute(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String[] parts = update.getMessage().getText().split(" ");

        if (parts.length < 2) {
            botMessageSender.sendMessage(
                    chatId,
                    "Пожалуйста, укажите название элемента. Пример: /addElement элемент",
                    false);
            return;
        }

        if (parts.length > 3) {
            botMessageSender.sendMessage(
                    chatId,
                    "Неверное количество аргументов. " +
                            "Используйте /addElement <элемент> или /addElement <родитель> <элемент>",
                    false);
            return;
        }

        String parentElementName = parts.length == 3 ? parts[1] : null;
        String elementName = parts.length == 3 ? parts[2] : parts[1];
        String response;

        if (parentElementName == null) {
            boolean rootExists = categoryService.rootElementExists(userId);
            if (rootExists) {
                response = "Корневой элемент уже существует.";
            } else {
                categoryService.addRootElement(userId, elementName);
                response = "Элемент \"" + elementName + "\" добавлен как корневой элемент.";
            }
        } else {
            boolean parentExists = categoryService.addChildElement(userId, parentElementName, elementName);
            if (parentExists) {
                response = "Элемент \"" + elementName + "\" " +
                        "добавлен к родительскому элементу \"" + parentElementName + "\".";
            } else {
                response = "Родительский элемент \"" + parentElementName + "\" не найден.";
            }
        }

        botMessageSender.sendMessage(chatId, response, false);
    }

    @Override
    public String getName() {
        return "/addElement";
    }

    @Override
    public String getDescription() {
        return "Добавляет элемент в дерево. " +
                "\nПример: /addElement <родитель> <дочерний элемент> или /addElement <корневой элемент>.";
    }
}
