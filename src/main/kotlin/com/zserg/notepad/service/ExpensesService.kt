package com.zserg.notepad.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.zserg.notepad.model.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*


@Service
class ExpensesService(
    @Value("\${notepad.url}")
    val host: String
) {
    val food = "(maxima|rimi|norfa|lidl|food)".toRegex(RegexOption.IGNORE_CASE)
    val health = "(dentist|drugs|health)".toRegex(RegexOption.IGNORE_CASE)
    val utilities = "(youtube|netflix|rental|utilities)".toRegex(RegexOption.IGNORE_CASE)
    val transport = "(parking|gas|ticket)".toRegex(RegexOption.IGNORE_CASE)
    val misc = "(senukai|depo|ikea|book|amazon)".toRegex(RegexOption.IGNORE_CASE)
    val amount = "\\d+[.,]*\\d{1,2}".toRegex(RegexOption.IGNORE_CASE)

    fun getExpensesForCurrentMonth(): Mono<List<Expense>> {
        val fromDate = LocalDate.now().withDayOfMonth(1).atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)
        val toDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59).format(DateTimeFormatter.ISO_DATE_TIME)

        var client: WebClient = WebClient.create(host)
        return client.get()
            .uri("/notes?fromDate=$fromDate&toDate=$toDate")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<NoteResponse>>() {})
            .map(::analyze)
    }

    fun getExpensesSummaryForCurrentMonth(): Mono<List<Expense>> {
        val fromDate = LocalDate.now().withDayOfMonth(1).atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)
        val toDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59).format(DateTimeFormatter.ISO_DATE_TIME)

        var client: WebClient = WebClient.create(host)
        return client.get()
            .uri("/notes?fromDate=$fromDate&toDate=$toDate")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<NoteResponse>>() {})
            .map(::analyze)
    }

    fun analyze(notes: List<NoteResponse>): List<Expense> {
         return notes
            .filter { it.tags?.contains("budget")?:false }
            .map { parseExpense(it.content) }
            .filterNotNull()
            .groupingBy { it.type }.aggregate { key, accumulator: Expense?, element, first ->
            if (first) // first element
                element
            else
                Expense(key, null, accumulator!!.value.add(element.value))
        }.values.toList()
    }

    fun parseExpense(note: String): Expense? {
        val amount = amount.find(note)?.value?.let { BigDecimal(it) } ?: return null

        food.find(note)?.value?.let { return@parseExpense Expense(ExpenseType.FOOD, null, amount) }
        health.find(note)?.value?.let { return@parseExpense Expense(ExpenseType.HEALTH, null, amount) }
        utilities.find(note)?.value?.let { return@parseExpense Expense(ExpenseType.UTILITIES, null, amount) }
        transport.find(note)?.value?.let { return@parseExpense Expense(ExpenseType.TRANSPORT, null, amount) }
        misc.find(note)?.value?.let { return@parseExpense Expense(ExpenseType.MISC, null, amount) }

        return Expense(ExpenseType.UNKNOWN, null, amount)
    }

}