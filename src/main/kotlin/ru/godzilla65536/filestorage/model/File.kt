package ru.godzilla65536.filestorage.model

import java.io.InputStream

data class File(
    val id: String,
    val title: String,
    val length: Long,
    val uploaded: String,
    val content: InputStream?,
)