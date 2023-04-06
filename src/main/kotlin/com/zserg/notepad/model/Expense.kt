package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.mongodb.core.mapping.Document
import java.math.BigDecimal

@JsonInclude(JsonInclude.Include.NON_NULL)
data class Expense(
    var type: ExpenseType,
    var subType: String?,
    var value: BigDecimal,
)
