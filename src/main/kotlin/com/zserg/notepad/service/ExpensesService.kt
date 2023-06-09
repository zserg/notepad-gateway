package com.zserg.notepad.service

import com.zserg.notepad.model.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
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
    @Autowired
    lateinit var restTemplate: RestTemplate

    val food = "(maxima|rimi|norfa|lidl|food)".toRegex(RegexOption.IGNORE_CASE)
    val health = "(dentist|drugs|health)".toRegex(RegexOption.IGNORE_CASE)
    val utilities = "(youtube|netflix|rental|utilities)".toRegex(RegexOption.IGNORE_CASE)
    val transport = "(parking|gas|ticket|transport)".toRegex(RegexOption.IGNORE_CASE)
    val misc = "(senukai|depo|ikea|book|amazon|misc|philips)".toRegex(RegexOption.IGNORE_CASE)

    val amount = "\\d+[.,]*\\d{0,2}".toRegex(RegexOption.IGNORE_CASE)

    fun getExpensesForCurrentMonth(): Mono<List<Expense>> {
        val fromDate = LocalDate.now().withDayOfMonth(1).atStartOfDay().format(DateTimeFormatter.ISO_DATE_TIME)
        val toDate = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()).atTime(23, 59, 59).format(DateTimeFormatter.ISO_DATE_TIME)

        var client: WebClient = WebClient.create(host)
        return client.get()
            .uri("/notes?fromDate=$fromDate&toDate=$toDate")
            .retrieve()
            .bodyToMono(object : ParameterizedTypeReference<List<NoteResponse>>() {})
            .map(::getExpenses)
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

    fun getExpenses(notes: List<NoteResponse>): List<Expense> {
        return notes
            .filter { it.tags?.contains("budget")?:false }
            .map { parseExpense(it) }
            .filterNotNull()
            .toList()
    }

    fun analyze(notes: List<NoteResponse>): List<Expense> {
        val list = notes
            .filter { it.tags?.contains("budget") ?: false }
            .map { parseExpense(it) }
            .filterNotNull()
            .groupingBy { it.type }.aggregate { key, accumulator: Expense?, element, first ->
                if (first) // first element
                    element
                else
                    Expense(key, null, accumulator!!.value.add(element.value), null, null)
            }.values.toMutableList()

        val total = list.sumOf { it.value }
        val fixed = list.filter { it.type == ExpenseType.FOOD || it.type == ExpenseType.UTILITIES }.sumOf { it.value }
        val miscTotal = total - fixed

        list.add(Expense(ExpenseType.FIXED, null, fixed, null, null))
        list.add(Expense(ExpenseType.MISC_TOTAL, null, miscTotal, null, null))
        list.add(Expense(ExpenseType.TOTAL, null, total, null, null))
        return list
    }

    fun parseExpense(note: NoteResponse): Expense? {
        val amount = amount.find(note.content)?.value?.let { BigDecimal(it) } ?: return null

        food.find(note.content)?.value?.let { return@parseExpense Expense(ExpenseType.FOOD, null, amount, note.createdAt, note.content) }
        health.find(note.content)?.value?.let { return@parseExpense Expense(ExpenseType.HEALTH, null, amount, note.createdAt, note.content) }
        utilities.find(note.content)?.value?.let { return@parseExpense Expense(ExpenseType.UTILITIES, null, amount, note.createdAt, note.content) }
        transport.find(note.content)?.value?.let { return@parseExpense Expense(ExpenseType.TRANSPORT, null, amount, note.createdAt, note.content) }
        misc.find(note.content)?.value?.let { return@parseExpense Expense(ExpenseType.MISC, null, amount, note.createdAt, note.content) }

        return Expense(ExpenseType.UNKNOWN, null, amount, note.createdAt, note.content)
    }

    fun postExpense(request: String): String? {
        val expense = NoteRequest(
            id = null,
            title = null,
            content = request,
            tags = listOf("budget")
        )
        val id = restTemplate.postForObject("$host/notes", expense, String::class.java)
        return id
    }


}