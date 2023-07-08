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
import org.springframework.web.client.postForObject
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
        val result = parse(content)
        var updatedNotes = 0

        result.questions.forEach {
            val notes: Array<NoteResponse>? =
                restTemplate.postForObject("$host/notes/find", FindRequest().apply { title = it.first }, Array<NoteResponse>::class.java)
            if (notes?.firstOrNull() == null) {
                val save = NoteRequest(
                    id = null,
                    title = it.first,
                    content = it.second,
                    tags = listOf("flashcard") + result.tags
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

        return UploadFileResponse(result.questions.size, updatedNotes)
    }

    fun parse(content: String): ParseResult {
        val pairs = mutableListOf<Pair<String, String>>()
        var pair: Pair<String, String>? = null

        var tags = listOf<String>()
        var question = false
        var answer = false

        for (line in content.split("\n")) {
            if (line.startsWith("//tags=")) {
                val result = "=(.*)".toRegex().find(line)
                tags = result?.groups?.get(1)?.value?.split(",") ?: listOf()
            } else if ("Q[0-9]+:".toRegex().containsMatchIn(line)) {
                pair?.let { pairs.add(it) }
                pair = Pair(line, "")
                question = true
                answer = false
            } else if (line.startsWith("Answer:")) {
                question = false
                answer = true
                pair = pair!!.copy(second = pair.second + line)
            } else {
                if (question) {
                    pair = pair!!.copy(first = pair.first + line)
                } else if(answer) {
                    pair = pair!!.copy(second = pair.second + line)
                }
            }
        }
        pair?.let { pairs.add(it) }
        return ParseResult(tags, pairs)
    }

    class ParseResult(val tags: List<String>, val questions: List<Pair<String, String>>)

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
        return Flashcard(question = result?.title ?: "", answer = result?.content ?: "")
    }


}