package com.zserg.notepad.service

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.io.File

class NoteServiceTest {

    @Test
    fun parse() {
        val service = FlashcardService()

        val content = """
//tags=spring,kotlin          
           Q1: Can you describe a complex project you've worked on in the past? What were the challenges you faced and how did you overcome them?
###
Answer: adssdd
sadsd

Q2: How do you approach designing and architecting software solutions? Can you walk us through your process?
Answer: sss
sss
sssasds

ass


Q3: How do you ensure the quality and maintainability of your code? Describe any practices or tools you typically use for code reviews, testing, and documentation.
###
It's some answer

Q4:. Have you ever had to optimize code for performance? How did you identify and address bottlenecks in the system?
        """.trimIndent()

        val parse: FlashcardService.ParseResult = service.parse(content)
        assertEquals(4, parse.questions.size)
        assertEquals(2, parse.tags.size)
    }

    @Test
    fun parse1() {
        val service = FlashcardService()
        val content = File("kotlin.txt").readText()
        val parse: FlashcardService.ParseResult = service.parse(content)
        assertEquals(69, parse.questions.size)
        assertEquals(1, parse.tags.size)
    }

}