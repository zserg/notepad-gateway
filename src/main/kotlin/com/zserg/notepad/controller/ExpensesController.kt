package com.zserg.notepad.controller

import com.zserg.notepad.model.Expense
import com.zserg.notepad.model.ExpenseRequest
import com.zserg.notepad.model.ExpenseResponse
import com.zserg.notepad.service.ExpensesService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

import java.util.*

@RestController
@RequestMapping("/expenses")
class ExpensesController {

    @Autowired
    private lateinit var expensesService: ExpensesService

    @GetMapping
    fun getExpenses(): Mono<List<Expense>> {
        return expensesService.getExpensesForCurrentMonth()
    }

    @PostMapping
    fun createExpenses(@RequestBody expenseRequest: ExpenseRequest): ExpenseResponse {
        val id = expensesService.postExpense(expenseRequest.expense?:"")
        return ExpenseResponse(id)
    }

    @GetMapping("summary")
    fun getExpensesSummary(): Mono<List<Expense>> {
        return expensesService.getExpensesSummaryForCurrentMonth()
    }
}