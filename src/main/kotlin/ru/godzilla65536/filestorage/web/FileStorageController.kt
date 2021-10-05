package ru.godzilla65536.filestorage.web

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.RestController
import ru.godzilla65536.filestorage.api.FileStorageApi
import ru.godzilla65536.filestorage.api.dto.File
import ru.godzilla65536.filestorage.api.dto.FileProps
import ru.godzilla65536.filestorage.service.StorageService

@RestController
class FileStorageController(
    private val service: StorageService
) : FileStorageApi {

    override suspend fun getAllFilesMetadata(): Flow<FileProps> = service.getAllFilesMetadata()

    override suspend fun uploadFile(filePart: FilePart) {
        service.addFile(filePart)
    }

    override suspend fun downloadFile(fileId: String): File {
        return service.getFile(fileId).awaitSingle()
    }

    override suspend fun deleteFile(fileId: String) {
        service.deleteFile(fileId)
    }

}

// TODO: 05.10.2021 Отправлять в Telegram ссылку, если файл больше 50 МБ
