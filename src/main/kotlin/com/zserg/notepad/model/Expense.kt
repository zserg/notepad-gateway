package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.math.BigDecimal
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Expense(
    var type: ExpenseType,
    var subType: String?,
    var value: BigDecimal,
    var date: LocalDateTime?,
    var comment: String?
)
