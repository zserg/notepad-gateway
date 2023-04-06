package com.zserg.notepad.controller

import com.zserg.notepad.model.Expense
import com.zserg.notepad.service.ExpensesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

import java.util.*

@Controller
@RequestMapping("/")
class Controller {

    @Autowired
    private lateinit var expensesService: ExpensesService

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

}