package ru.godzilla65536.filestorage.web

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import ru.godzilla65536.filestorage.service.StorageService

@Controller
class FileUploadController @Autowired constructor(
    private val service: StorageService
) {
    @GetMapping("/")
    fun listUploadedFiles(model: Model): String {
        model.addAttribute("filesInfo", service.getAll())
        return "uploadForm"
    }

    @GetMapping("/file/{fileId}")
    @ResponseBody
    fun getFile(@PathVariable fileId: String): ResponseEntity<Resource> {
        val file = service.getFile(fileId)
        return ResponseEntity.ok()
            .header(
                HttpHeaders.CONTENT_DISPOSITION,
                "attachment; filename=\"${file.title}\""
            ).body(InputStreamResource(file.content!!))
    }

    @PostMapping("/")
    fun handleFileUpload(
        @RequestParam("file") file: MultipartFile,
        redirectAttributes: RedirectAttributes
    ): String {
        service.addFile(file)
        redirectAttributes.apply {
            addFlashAttribute("message", "Вы добавили файл")
            addFlashAttribute("fileName", file.originalFilename)
        }
        return "redirect:/"
    }

    @GetMapping("/file/delete/{fileId}")
    fun handleFileDelete(
        @PathVariable fileId: String,
        redirectAttributes: RedirectAttributes
    ): String {
        val fileName = service.getFileName(fileId)
        service.deleteFile(fileId)
        redirectAttributes.apply {
            addFlashAttribute("message", "Вы удалили файл")
            addFlashAttribute("fileName", fileName)
        }
        return "redirect:/"
    }

}