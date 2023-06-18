package com.zserg.notepad.service

import com.zserg.notepad.model.ExpenseType
import com.zserg.notepad.model.NoteResponse
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal


class ExpensesServiceTest {

    @Test
    fun parseExpense() {
        val service = ExpensesService("")
        val expense = service.parseExpense(get("12.1 Maxima"))
        assertThat(expense?.type).isEqualTo(ExpenseType.FOOD)
        assertThat(expense?.value).isEqualTo(BigDecimal("12.1"))
    }
    @Test
    fun parseExpenseInteger() {
        val service = ExpensesService("")
        val expense = service.parseExpense(get("1 Maxima"))
        assertThat(expense?.type).isEqualTo(ExpenseType.FOOD)
        assertThat(expense?.value).isEqualTo(BigDecimal("1"))
    }

    @Test
    fun parseExpenseIntegerLong() {
        val service = ExpensesService("")
        val expense = service.parseExpense(get("100 Maxima"))
        assertThat(expense?.type).isEqualTo(ExpenseType.FOOD)
        assertThat(expense?.value).isEqualTo(BigDecimal("100"))
    }

    fun get(text: String): NoteResponse {
        return NoteResponse(content = text)
    }
}