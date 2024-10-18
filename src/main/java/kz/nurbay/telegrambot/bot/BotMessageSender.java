package kz.nurbay.telegrambot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.ByteArrayInputStream;

/**
 * A service responsible for sending messages to Telegram users.
 */
@Component
public class BotMessageSender {

    private static final Logger log = LoggerFactory.getLogger(BotMessageSender.class);
    /**
     * The client used to interact with the Telegram API.
     */
    private final TelegramClient telegramClient;

    public BotMessageSender(TelegramClient telegramClient) {
        this.telegramClient = telegramClient;
    }

    /**
     * Sends a text message to the specified chat.
     * Optionally, the message can be sent with Markdown formatting.
     *
     * @param chatId       the ID of the chat to send the message to
     * @param text         the text content of the message
     * @param withMarkdown if true, the message will be sent with Markdown formatting
     */
    public void sendMessage(Long chatId, String text, boolean withMarkdown) {
        SendMessage message = SendMessage.builder()
                .chatId(chatId)
                .text(text)
                .build();

        if (withMarkdown) {
            message.setParseMode("Markdown");
        }

        try {
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message to chat {}: {}", chatId, e.getMessage());
        }
    }

    /**
     * Sends a document to the specified chat.
     *
     * @param chatId         the ID of the chat to send the document to
     * @param fileName       the name of the document to be sent
     * @param documentStream the input stream of the document content
     */
    public void sendDocument(Long chatId, String fileName, ByteArrayInputStream documentStream) {
        InputFile inputFile = new InputFile(documentStream, fileName);

        SendDocument sendDocument = SendDocument.builder()
                .chatId(chatId)
                .document(inputFile)
                .build();

        try {
            telegramClient.execute(sendDocument);
        } catch (TelegramApiException e) {
            log.error("Error sending document to chat {}: {}", chatId, e.getMessage());
        }
    }
}

