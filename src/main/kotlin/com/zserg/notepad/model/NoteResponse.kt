package com.zserg.notepad.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.time.LocalDateTime

@JsonIgnoreProperties(ignoreUnknown = true)
data class NoteResponse(
    val id: String? = null,
    val title: String? = null,
    val content: String = "",
    val tags: List<String>? = null,
    val createdAt: LocalDateTime? = null
) {
    constructor(note: Note) : this(
        id = note.id,
        title = note.title,
        content = note.content,
        tags = note.tags,
        createdAt = note.createdAt
    )

    fun fromEntity(note: Note): NoteResponse {
        return NoteResponse(
            id = note.id,
            title = note.title,
            content = note.content,
            tags = note.tags,
            createdAt = note.createdAt
        )
    }
}
