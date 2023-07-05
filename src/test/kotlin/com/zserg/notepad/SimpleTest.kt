package com.zserg.notepad

import org.junit.jupiter.api.Test

class SimpleTest {
    @Test
    fun test() {
        val a = "=(.*)".toRegex().find("//tags=spring,java")
        a?.groups?.get(1)?.value?.split(",")
//            ?.groupValues?.get(1)?.split(",")?.forEach { println(it) }
        val b = 2
    }

}