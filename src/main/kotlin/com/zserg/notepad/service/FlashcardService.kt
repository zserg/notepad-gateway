package com.zserg.notepad.service

import com.zserg.notepad.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.multipart.MultipartFile
import java.util.*


@Service
class FlashcardService {
    @Value("\${notepad.url}")
    lateinit var host: String

    @Autowired
    lateinit var restTemplate: RestTemplate

    fun uploadFile(file: MultipartFile): UploadFileResponse {
        val bytes = file.inputStream.readAllBytes()
        val content = String(bytes)
        val questions = parse(content)
        var updatedNotes = 0

        questions.forEach {
            val notes: Array<NoteResponse>? =
                restTemplate.getForObject("$host/notes?title=${it.first}", Array<NoteResponse>::class.java)
            if (notes?.firstOrNull() == null) {
                val save = NoteRequest(
                    id = null,
                    title = it.first,
                    content = it.second,
                    tags = listOf("flashcard")
                )
                val id = restTemplate.postForObject("$host/notes", save, String::class.java)
                println("Created note with id $id")
            }else{
                val note = notes.first().toNote()
                if (note.content != it.second) {
                    note.content = it.second
                    restTemplate.postForObject("$host/notes", note, String::class.java)
                    updatedNotes++
                }
            }
        }

        return UploadFileResponse(questions.size, updatedNotes)
    }

    fun parse(content: String): MutableList<Pair<String, String>> {
        val pairs = mutableListOf<Pair<String, String>>()
        var currentPair: Pair<String, String>? = null

        content.split("\n").forEach { line ->
            if (line.isBlank()) {
                // Start a new pair
                currentPair?.let { pairs.add(it) }
                currentPair = null
            } else if (line.startsWith("###")) {
                // Add a new element to the current pair
                val element = line.removePrefix("###").trim()
                currentPair?.let { pair ->
                    currentPair = pair.copy(second = pair.second + element)
                }
            } else {
                // Start a new pair or add to the existing pair
                val text = line.trim()
                if (currentPair == null) {
                    currentPair = text to ""
                } else {
                    currentPair = currentPair!!.copy(second = currentPair!!.second + text + "\n")
                }
            }
        }

        currentPair?.let { pairs.add(it) }
        return pairs
    }


}