package ru.godzilla65536.filestorage.service

import com.mongodb.client.gridfs.model.GridFSFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitLast
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.apache.commons.codec.binary.Base64
import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.ReactiveGridFsResource
import org.springframework.data.mongodb.gridfs.ReactiveGridFsTemplate
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import ru.godzilla65536.filestorage.api.dto.File
import ru.godzilla65536.filestorage.api.dto.FileProps
import ru.godzilla65536.filestorage.telegram.Bot
import java.time.ZoneId

@Service
class StorageService(
    private val gridFsTemplate: ReactiveGridFsTemplate,
    private val telegramBot: Bot
) {

    fun getAllFilesMetadata(): Flow<FileProps> =
        gridFsTemplate.find(Query())
            .flatMap(gridFsTemplate::getResource)
            .flatMap(ReactiveGridFsResource::getGridFSFile)
            .sort(
                // сортировка на уровне БД работает некорректно
                Comparator.comparing(GridFSFile::getUploadDate).reversed()
            )
            .map {
                FileProps(
                    id = it.id.asObjectId().value.toString(),
                    name = it.filename,
                    length = it.length,
                    uploadedAt = it.uploadDate.toInstant().atZone(ZONE_ID_MOSCOW)
                )
            }.asFlow()

    suspend fun addFile(filePart: FilePart) {
        val fileId = gridFsTemplate
            .store(filePart.content(), filePart.filename())
            .map { it.toString() }
            .awaitSingle()
        val fileData = getFileData(findById(fileId))
        mono {
            telegramBot.notifyAboutAddingFile(
                fileData.inputStream(),
                filePart.filename()
            )
        }.subscribe()
    }

    /*
    * TODO: 03.10.2021
    *  Возможно стоит возвращать Resource. А имя файла запрашивать отдельным методом. Мне кажется сейчас браузер
    *  загружает файлы полностью в оперативную память
    */
    suspend fun getFile(fileId: String): Mono<File> {
        val gridFsResource = findById(fileId)
        val byteArray = getFileData(gridFsResource)
        return gridFsResource.gridFSFile.map {
            File(
                name = it.filename,
                base64Content = Base64.encodeBase64String(byteArray)
            )
        }
    }

    suspend fun deleteFile(fileId: String) {
        gridFsTemplate.delete(Query(Criteria.where(ID).`is`(ObjectId(fileId)))).awaitSingleOrNull()
    }

    private suspend fun findById(fileId: String) =
        gridFsTemplate
            .findOne(Query(Criteria.where(ID).`is`(ObjectId(fileId))))
            .flatMap(gridFsTemplate::getResource)
            .awaitSingle()

    private suspend fun getFileData(gridFsResource: ReactiveGridFsResource) =
        gridFsResource.content
            .map { it.asByteBuffer().array() }
            .reduce { b1, b2 -> b1 + b2 }
            .awaitLast()

    companion object {
        const val ID = "_id"
        val ZONE_ID_MOSCOW: ZoneId = ZoneId.of("Europe/Moscow")
    }

}
