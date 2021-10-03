package ru.godzilla65536.filestorage.web

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactor.awaitSingle
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.RestController
import ru.godzilla65536.filestorage.api.FileStorageApi
import ru.godzilla65536.filestorage.api.dto.FileContent
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

    // проблемы с кириллицей и content-type
    override suspend fun downloadFile(fileId: String): FileContent {
        return service.getFile(fileId).awaitSingle()
    }
//        val httpHeaders = HttpHeaders().apply {
//            contentDisposition = ContentDisposition
//                .builder("attachment")
//                .filename(fileContent.title)
//                .build()
//            set("fileName", "йцуфыв")
//        }
//        return ResponseEntity.ok()
//            .headers(httpHeaders)
//            .body(InputStreamResource(fileContent.base64Content))
//    }

    override suspend fun deleteFile(fileId: String) {
        service.deleteFile(fileId)
    }

}
