package com.zserg.notepad.model

class OpenAIResponse {
    val id: String? = null
    val `object`: String? = null
    val created: Long? = null
    val model: String? = null
    val usage: Usage? = null
    val choices: List<Choice>? = null
    class Usage {
        val prompt_tokens: Int? = null
        val completion_tokens: Int? = null
        val total_tokens: Int? = null
    }

    class Choice {
        val message: Message? = null
        val finish_reason: String? = null
        val index: Int? = null
    }
    class Message {
        val role: String? = null
        val content: String? = null
    }
}
