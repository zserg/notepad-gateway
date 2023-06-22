package com.zserg.notepad.service

import com.theokanning.openai.completion.chat.ChatCompletionChunk
import com.theokanning.openai.completion.chat.ChatCompletionRequest
import com.theokanning.openai.completion.chat.ChatMessage
import com.theokanning.openai.completion.chat.ChatMessageRole
import com.theokanning.openai.service.OpenAiService
import com.zserg.notepad.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
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
            } else {
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

    fun getAnswerFromAi(question: String): String {
        val token = System.getenv("OPENAI_TOKEN")
        val service = OpenAiService(token)
        val messages: MutableList<ChatMessage> = ArrayList()
        val systemMessage = ChatMessage(ChatMessageRole.SYSTEM.value(), question)
        messages.add(systemMessage)
        val chatCompletionRequest = ChatCompletionRequest
            .builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .n(1)
            .maxTokens(50)
            .logitBias(HashMap())
            .build()

        val answerList = mutableListOf<String>()

        service.streamChatCompletion(chatCompletionRequest)
            .doOnError { obj: Throwable -> obj.printStackTrace() }
            .blockingForEach { x: ChatCompletionChunk? ->
                x?.choices?.forEach {
                    it.message.content?.let {
                        answerList.add(
                            it
                        )
                    }
                }
            }

        return answerList.joinToString(separator = "")
    }

    //    curl https://api.openai.com/v1/chat/completions \
//    -H "Content-Type: application/json" \
//    -H "Authorization: Bearer $OPENAI_API_KEY" \
//    -d '{
//    "model": "gpt-3.5-turbo",
//    "messages": [{"role": "user", "content": "Hello!"}]
//}'
    fun getAnswerFromAiSimple(question: String): String {
        val token = System.getenv("OPENAI_TOKEN")
        val service = OpenAiService(token)
        val messages: MutableList<ChatMessage> = ArrayList()
        val systemMessage = ChatMessage(ChatMessageRole.SYSTEM.value(), question)
        messages.add(systemMessage)

        val headers = HttpHeaders()
        headers.setContentType(MediaType.APPLICATION_JSON)
        headers.setBearerAuth(token)
        val entity = HttpEntity<OpenAIRequest>(OpenAIRequest(question), headers)
        val response =
            restTemplate.postForObject("https://api.openai.com/v1/chat/completions", entity, OpenAIResponse::class.java)

        return response?.choices?.firstOrNull()?.message?.content ?: ""
    }


    fun getFlashcard(): Flashcard {
        val result = restTemplate.getForObject("$host/notes/flashcard", NoteResponse::class.java)
        return Flashcard(question = result?.title ?: "", answer = result?.content ?: "" )
    }


}