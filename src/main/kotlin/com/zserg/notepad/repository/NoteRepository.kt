package com.zserg.notepad.repository

import com.zserg.notepad.model.Note
import org.springframework.data.mongodb.repository.MongoRepository
import java.time.LocalDateTime

interface NoteRepository: MongoRepository<Note, String> {
    fun findByCreatedAtBetween(fromDate: LocalDateTime, toDate: LocalDateTime): List<Note>
}