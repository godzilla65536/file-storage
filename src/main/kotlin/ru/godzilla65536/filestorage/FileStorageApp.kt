package ru.godzilla65536.filestorage

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class FileStorageApp

fun main(args: Array<String>) {
    runApplication<FileStorageApp>(*args)
}
