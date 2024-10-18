package kz.nurbay.telegrambot.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Configuration class that provides a bean for the {@link TelegramClient},
 * which is used to interact with the Telegram API.
 */
@Configuration
public class TelegramBotConfig {
    /**
     * Creates a {@link TelegramClient} bean that is used to send messages.
     *
     * @param botToken the authentication token for the Telegram bot,
     * @return a configured instance of {@link TelegramClient}
     */
    @Bean
    public TelegramClient telegramClient(@Value("${telegram.bot.token}") String botToken) {
        return new OkHttpTelegramClient(botToken);
    }
}

