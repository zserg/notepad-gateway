package com.zserg.notepad.service

import com.zserg.notepad.model.Note
import com.zserg.notepad.model.NoteRequest
import com.zserg.notepad.repository.NoteRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.*


@Service
class NoteService {

    @Autowired
    private lateinit var noteRepository: NoteRepository

    fun saveNote(note: NoteRequest): String? {
        val saved = noteRepository.save(note.toEntity())
        return saved.id
    }

    fun findById(id: String): Optional<Note> {
        val note = noteRepository.findById(id)
        return note
    }

    fun findAll(): List<Note> {
        return noteRepository.findAll()
    }

    fun find(fromDate: LocalDateTime?, toDateTime: LocalDateTime?): List<Note> {
        return if (fromDate == null && toDateTime == null) {
            noteRepository.findAll()
        } else {
            noteRepository.findByCreatedAtBetween(
                fromDate ?: LocalDate.of(2020, 1, 1).atStartOfDay(),
                toDateTime?: LocalDate.of(2100, 1, 1).atStartOfDay());
        }
    }

}