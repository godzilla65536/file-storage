package ru.godzilla65536.filestorage.service

import org.bson.types.ObjectId
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.gridfs.GridFsOperations
import org.springframework.data.mongodb.gridfs.GridFsTemplate
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import ru.godzilla65536.filestorage.model.File
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
class StorageService(
    private val gridFsTemplate: GridFsTemplate,
    private val operations: GridFsOperations
) {

    fun addFile(file: MultipartFile) {
        gridFsTemplate.store(file.inputStream, file.originalFilename!!)
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