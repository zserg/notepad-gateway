package com.zserg.notepad.model

class OpenAIRequest(content: String) {
    val model = "gpt-3.5-turbo"
    val messages: List<message> = listOf(message("user", content))

    class message(val role: String, val content: String)

}
