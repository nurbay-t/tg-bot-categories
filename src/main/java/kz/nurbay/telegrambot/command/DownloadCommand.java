package kz.nurbay.telegrambot.command;

import kz.nurbay.telegrambot.bot.BotMessageSender;
import kz.nurbay.telegrambot.model.Category;
import kz.nurbay.telegrambot.service.CategoryService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Handles the /download command, which allows users to download their category tree as an Excel document.
 */
@Component
public class DownloadCommand implements BotCommand {

    private final CategoryService categoryService;
    private final BotMessageSender botMessageSender;

    public DownloadCommand(CategoryService categoryService, BotMessageSender botMessageSender) {
        this.categoryService = categoryService;
        this.botMessageSender = botMessageSender;
    }

    /**
     * Executes the /download command.
     * Retrieves all categories for the user and generates an Excel file with the category tree.
     * If categories are found, the file is sent to the user. Otherwise, a message is sent informing the user
     * that there is no category tree.
     *
     * @param update the incoming update from Telegram containing the command
     */
    @Override
    public void execute(Update update) {
        Long userId = update.getMessage().getFrom().getId();
        Long chatId = update.getMessage().getChatId();

        List<Category> categories = categoryService.getAllCategoriesByUserId(userId);

        if (!categories.isEmpty()) {
            ByteArrayInputStream excelFile = categoryService.createExcelFileWithCategories(categories);
            if (excelFile != null) {
                botMessageSender.sendDocument(chatId, "categories.xlsx", excelFile);
            } else {
                botMessageSender.sendMessage(chatId, "Не удалось создать файл.", false);
            }
        } else {
            botMessageSender.sendMessage(chatId, "У вас нет дерева", false);
        }
    }

    @Override
    public String getName() {
        return "/download";
    }

    @Override
    public String getDescription() {
        return "Скачивает Excel документ с деревом категорий.";
    }
}
