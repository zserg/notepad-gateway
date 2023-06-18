package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonProperty

class ExpenseRequest (
    @JsonProperty("expense") var expense: String?
)