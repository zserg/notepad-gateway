package com.zserg.notepad.controller

import com.zserg.notepad.model.UploadFileResponse
import com.zserg.notepad.service.FlashcardService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

import java.util.*

@Controller
@RequestMapping("/flashcard")
class FlashcardController {

    @Autowired
    private lateinit var flashcardService: FlashcardService

    @PostMapping("/upload")
    @ResponseBody
    open fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponse {
        val response: UploadFileResponse = flashcardService.uploadFile(file)
        return response
    }

    @GetMapping("/answer")
    @ResponseBody
    open fun getAnswer(@RequestParam("question") question: String): String {
        val response: String = flashcardService.getAnswerFromAiSimple(question)
        return response
    }

}