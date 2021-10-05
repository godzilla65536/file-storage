package ru.godzilla65536.filestorage.api

import io.swagger.v3.oas.annotations.tags.Tag
import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.http.codec.multipart.FilePart
import org.springframework.web.bind.annotation.*
import ru.godzilla65536.filestorage.api.dto.File
import ru.godzilla65536.filestorage.api.dto.FileProps

@Tag(name = "File Storage")
@CrossOrigin
interface FileStorageApi {

    @GetMapping("all", produces = [MediaType.APPLICATION_JSON_VALUE])
    suspend fun getAllFilesMetadata(): Flow<FileProps>

    @PostMapping("upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    suspend fun uploadFile(@RequestPart("file") filePart: FilePart)

    @GetMapping("download/{fileId}")
    suspend fun downloadFile(@PathVariable fileId: String): File

    @DeleteMapping("delete/{fileId}")
    suspend fun deleteFile(@PathVariable fileId: String)

}

// TODO: 30.09.2021 настроить ansible
