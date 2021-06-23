package ru.godzilla65536.filestorage.service

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.objects.InputFile
import ru.godzilla65536.filestorage.model.File
import ru.godzilla65536.filestorage.telegram.TelegramBot
import ru.godzilla65536.filestorage.telegram.TelegramBotProps
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class StorageService(
    private val gridFsTemplate: GridFsTemplate,
    private val operations: GridFsOperations,
    private val bot: TelegramBot,
    private val telegramBotProps: TelegramBotProps
) {

    fun addFile(file: MultipartFile) {
        gridFsTemplate.store(file.inputStream, file.originalFilename!!)

        val inputFile = InputFile(file.inputStream, file.originalFilename)
        val sendDocument = SendDocument(
            telegramBotProps.chatId,
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

        println("Sending file to telegram chat...")
        bot.execute(sendDocument)
    }

    fun getFile(fileId: String): File {
        val gridFSFile = gridFsTemplate.findOne(Query(Criteria.where(ID).`is`(ObjectId(fileId))))
        return gridFSFile.let {
            File(
                id = it.id.toString(),
                title = it.filename,
                length = it.length,
                uploaded = it.uploadDate.toInstant().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                content = operations.getResource(it).inputStream
            )
        }
    }

    fun deleteFile(fileId: String) {
        gridFsTemplate.delete(Query(Criteria.where(ID).`is`(ObjectId(fileId))))
    }

    fun getFileName(fileId: String) =
        gridFsTemplate.findOne(Query(Criteria.where(ID).`is`(ObjectId(fileId)))).filename

    fun getAll(): List<File> {
        return gridFsTemplate.find(Query()).map {
            File(
                id = it.id.asObjectId().value.toString(),
                it.filename,
                length = it.length,
                uploaded = it.uploadDate.toInstant().atZone(ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("yyyy-MM-dd в hh:mm:ss")),
                content = null
            )
        }.toList()

    }

    companion object {
        const val ID = "_id"
    }

}