package com.zserg.notepad.service

import com.zserg.notepad.model.ExpenseType
import org.junit.jupiter.api.Test

import org.assertj.core.api.Assertions.assertThat
import java.math.BigDecimal


class ExpensesServiceTest {

    @Test
    fun parseExpense() {
        val service = ExpensesService("")
        val expense = service.parseExpense("12.1 Maxima")
        assertThat(expense.type).isEqualTo(ExpenseType.FOOD)
        assertThat(expense.value).isEqualTo(BigDecimal("12.1"))
    }
}