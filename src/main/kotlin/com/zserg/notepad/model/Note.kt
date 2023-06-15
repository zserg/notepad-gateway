package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonInclude
import java.time.LocalDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
class Note(
    var id: String? = null,
    var title: String,
    var content: String,
    var tags: List<String> = emptyList(),
    var createdAt: LocalDateTime?
    )
