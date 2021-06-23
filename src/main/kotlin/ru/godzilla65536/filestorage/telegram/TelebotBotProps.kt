package ru.godzilla65536.filestorage.telegram

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

@ConstructorBinding
@ConfigurationProperties(prefix = "telegram.bot")
data class TelegramBotProps(
    val token: String,
    val chatId: String
)
