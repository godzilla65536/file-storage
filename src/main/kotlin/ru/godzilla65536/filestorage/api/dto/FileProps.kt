package ru.godzilla65536.filestorage.api.dto

import java.time.ZonedDateTime

data class FileProps(
    val id: String,
    val name: String,
    val length: Long,
    val uploadedAt: ZonedDateTime
)
