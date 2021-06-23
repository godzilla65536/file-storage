package ru.godzilla65536.filestorage.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update

@Service
class TelegramBot(
    private val props: TelegramBotProps
) : TelegramLongPollingBot() {

    override fun getBotToken() = props.token

    override fun getBotUsername() = "noName :)"

    override fun onUpdateReceived(update: Update) {
    }



}