package com.zserg.notepad.controller

import com.zserg.notepad.model.Expense
import com.zserg.notepad.model.UploadFileResponse
import com.zserg.notepad.service.ExpensesService
import com.zserg.notepad.service.FlashcardService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import reactor.core.publisher.Mono

import java.util.*

@Controller
@RequestMapping("/")
class Controller {

    @Autowired
    private lateinit var expensesService: ExpensesService
    @Autowired
    private lateinit var flashcardService: FlashcardService

    @GetMapping("expenses")
    @ResponseBody
    fun getExpenses(): Mono<List<Expense>> {
        return expensesService.getExpensesForCurrentMonth()
    }

    @GetMapping("expenses-summary")
    @ResponseBody
    fun getExpensesSummary(): Mono<List<Expense>> {
        return expensesService.getExpensesSummaryForCurrentMonth()
    }

    @PostMapping("/flashcard/upload")
    @ResponseBody
    open fun uploadFile(@RequestParam("file") file: MultipartFile): UploadFileResponse {
        val response: UploadFileResponse = flashcardService.uploadFile(file)
        return response
    }

}