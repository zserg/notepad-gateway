package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonInclude
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

@Document
@JsonInclude(JsonInclude.Include.NON_NULL)
class Note(
    @Id
    var id: String? = null,
    var title: String,
    var content: String,
    var tags: List<String> = emptyList(),
    var createdAt: LocalDateTime?
    )
