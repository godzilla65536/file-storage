package ru.godzilla65536.filestorage.telegram

import org.springframework.stereotype.Service
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.InputStream

@Service
class Bot(
    private val props: BotProps
) : TelegramLongPollingBot() {

    // TODO: 03.10.2021 Сообщать в консоли о состоянии бота (включён/выключен)

    override fun getBotToken() = props.token

    override fun getBotUsername() = "noName :)"

    override fun onUpdateReceived(update: Update) {
    }

    fun notifyAboutAddingFile(file: InputStream, filename: String) {
        if (!props.enabled) {
            return
        }

        val inputFile = InputFile(file, filename)
        val sendDocument = SendDocument(
            props.chatId,
            inputFile,
            "Добавлен новый файл!",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
        )

        println("Sending file '$filename' to telegram chat...")
        try {
            this.execute(sendDocument)
            println("File '$filename' successfully sent!")
        } catch (e: Exception) {
            println("Something happens with file '$filename' :(")
            e.printStackTrace()
        }
    }

}

// TODO: 05.10.2021 Добавить кнопку "Отправить в Telegram"
