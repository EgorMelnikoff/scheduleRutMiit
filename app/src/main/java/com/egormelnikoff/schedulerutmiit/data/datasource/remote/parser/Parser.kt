package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import android.os.Build
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.RecurrenceRule
import com.egormelnikoff.schedulerutmiit.app.entity.Room
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.app.extension.toUtcTime
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NewsContent
import com.egormelnikoff.schedulerutmiit.app.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints.BASE_MIIT_URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.abs

object Parser {
    /* Schedule */
    suspend fun parseSchedule(
        document: Document,
        timetable: Timetable,
        currentGroup: Group?
    ): Schedule = withContext(Dispatchers.Default) {
        val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA) {
            Locale.of("ru", "RU")
        } else {
            @Suppress("DEPRECATION")
            Locale("ru", "RU")
        }

        val parserFormatter = DateTimeFormatter.ofPattern("d MMMM yyyy", locale)

        return@withContext if (timetable.type == TimetableType.PERIODIC) {
            val periodicContent = document.parsePeriodicSchedule(
                timetable.startDate,
                currentGroup,
                parserFormatter
            )
            if (periodicContent.events.isNullOrEmpty()) {
                val nonPeriodicContent = document.parseNonPeriodicSchedule(
                    isPeriodic = true,
                    currentGroup = currentGroup,
                    formatter = parserFormatter
                )
                Schedule(
                    timetable = timetable,
                    periodicContent = PeriodicContent(
                        events = nonPeriodicContent.events,
                        recurrence = Recurrence(
                            interval = 1,
                            currentNumber = 1,
                            firstWeekNumber = 1
                        )
                    ),
                    nonPeriodicContent = null
                )
            } else {
                Schedule(
                    timetable = timetable,
                    periodicContent = periodicContent,
                    nonPeriodicContent = null
                )
            }
        } else {
            val nonPeriodicContent = document.parseNonPeriodicSchedule(
                currentGroup = currentGroup,
                formatter = parserFormatter
            )

            Schedule(
                timetable = timetable,
                periodicContent = null,
                nonPeriodicContent = nonPeriodicContent
            )
        }
    }

    private suspend fun Document.parsePeriodicSchedule(
        startDate: LocalDate,
        currentGroup: Group?,
        formatter: DateTimeFormatter,
    ): PeriodicContent = withContext(Dispatchers.Default) {
        val weekNumbers = this@parsePeriodicSchedule
            .select(".nav-link[aria-controls]")
            .map {
                it.attr("aria-controls")
                    .removePrefix("week-")
                    .toInt()
            }

        val events = weekNumbers.flatMap { periodNumber ->
            this@parsePeriodicSchedule.getElementById("week-$periodNumber")
                ?.select("div.info-block.info-block_collapse.show")
                ?.flatMap { element ->
                    element.parseDate(true, formatter)?.let { date ->
                        element.parseEvents(
                            date = date,
                            periodNumber = periodNumber,
                            recurrenceRule = RecurrenceRule(
                                frequency = "WEEKLY",
                                interval = 2
                            ),
                            currentGroup = currentGroup
                        )
                    }.orEmpty()
                }.orEmpty()
        }

        return@withContext PeriodicContent(
            events = events.normalizePeriodicEvents(),
            recurrence = getRecurrence(
                startDate = startDate,
                interval = weekNumbers.size,
                currentNumber = parseCurrentWeek(this@parsePeriodicSchedule),
            )
        )
    }

    private suspend fun Document.parseNonPeriodicSchedule(
        isPeriodic: Boolean = false,
        currentGroup: Group?,
        formatter: DateTimeFormatter,
    ): NonPeriodicContent = withContext(Dispatchers.Default) {
        val eventsByDates = this@parseNonPeriodicSchedule
            .select("div.info-block.info-block_collapse.show")

        val events = eventsByDates.flatMap { element ->
            element.parseDate(isPeriodic, formatter)?.let { date ->
                element.parseEvents(
                    date = date,
                    currentGroup = currentGroup
                )
            }.orEmpty()
        }
        return@withContext if (isPeriodic) {
            NonPeriodicContent(
                events = events.map {
                    it.copy(
                        periodNumber = 1,
                        recurrenceRule = RecurrenceRule(
                            frequency = "WEEKLY",
                            interval = 1
                        )
                    )
                }
            )
        } else NonPeriodicContent(
            events = events
        )
    }

    private suspend fun Element.parseDate(
        isPeriodic: Boolean,
        formatter: DateTimeFormatter
    ): LocalDate? = withContext(Dispatchers.Default) {
        val header = this@parseDate.selectFirst(".info-block__header-text")

        val dateText = if (isPeriodic) {
            header
                ?.select(".text-secondary.small")
                ?.first()
                ?.text()
                ?.trim() ?: return@withContext null
        } else {
            header
                ?.ownText()
                ?.trim() ?: return@withContext null
        }

        if (dateText.isEmpty()) return@withContext null

        val year = Year.now().value

        return@withContext LocalDate.parse("$dateText $year", formatter)
    }

    private suspend fun Element.parseEvents(
        date: LocalDate,
        periodNumber: Int? = null,
        recurrenceRule: RecurrenceRule? = null,
        currentGroup: Group?
    ): List<Event> = withContext(Dispatchers.Default) {
        return@withContext this@parseEvents
            .select(".timetable__list-timeslot")
            .map { element ->
                val headerText = element
                    .selectFirst(".mb-1")
                    ?.text()
                    ?.trim()

                val timeSlotName = headerText
                    ?.takeIf { it.contains(",") }
                    ?.substringBefore(",")
                    ?.trim()

                val timeRange = headerText
                    ?.substringAfter(",")
                    ?.trim()

                val startTime = timeRange
                    ?.substringBefore("—")
                    ?.trim()
                    ?.let { LocalTime.parse(it) }
                    ?.toUtcTime(date)

                val endTime = timeRange
                    ?.substringAfter("—")
                    ?.trim()
                    ?.let { LocalTime.parse(it) }
                    ?.toUtcTime(date)

                val typeName = element
                    .selectFirst(".timetable__grid-text_gray")
                    ?.text()
                    ?.trim()

                val subjectContainer = element.selectFirst(".pl-4")

                val name = subjectContainer
                    ?.ownText()
                    ?.trim()

                val lecturers = element.parseLecturers()
                val rooms = element.parseRooms()
                val groups = element.parseGroups(currentGroup)

                Event(
                    startDatetime = LocalDateTime.of(date, startTime),
                    endDatetime = LocalDateTime.of(date, endTime),
                    name = name,
                    typeName = typeName,
                    timeSlotName = timeSlotName,
                    lecturers = lecturers,
                    rooms = rooms,
                    groups = groups,
                    periodNumber = periodNumber,
                    recurrenceRule = recurrenceRule
                )
            }
    }

    private suspend fun Element.parseLecturers(): MutableList<Lecturer> =
        withContext(Dispatchers.Default) {
            return@withContext this@parseLecturers
                .select(".icon-academic-cap")
                .mapNotNull { a ->

                    val href = a.attr("href")
                    val id = href
                        .substringAfter("/people/")
                        .substringBefore("/")
                        .toIntOrNull() ?: return@mapNotNull null

                    val title = a.attr("title")

                    Lecturer(
                        id = id,
                        shortFio = a.text().trim(),
                        fullFio = title.substringBefore(",").trim(),
                        hint = title.substringAfter(",").trim()
                    )
                }.toMutableList()
        }

    private suspend fun Element.parseRooms(): MutableList<Room> = withContext(Dispatchers.Default) {
        return@withContext this@parseRooms
            .select(".icon-location")
            .mapNotNull { a ->

                val href = a.attr("href")

                val id = href
                    .substringAfter("room=")
                    .substringBefore("&")
                    .toIntOrNull() ?: return@mapNotNull null

                val title = a.attr("title")

                Room(
                    id = id,
                    name = a.text().trim(),
                    hint = title.trim()
                )
            }.toMutableList()
    }

    private suspend fun Element.parseGroups(
        currentGroup: Group?
    ): MutableList<Group> = withContext(Dispatchers.Default) {
        return@withContext this@parseGroups
            .select(".icon-community")
            .mapNotNull {
                val id = it.attr("href")
                    .substringAfter("/timetable/")
                    .toIntOrNull() ?: currentGroup?.id ?: return@mapNotNull null

                Group(
                    id = id,
                    name = it.text().trim()
                )
            }.toMutableList()
    }

    suspend fun parseCurrentWeek(element: Element): Int = withContext(Dispatchers.Default) {
        val activeLink = element.select(".nav-link[aria-controls].active").first()

        val weekNumber = activeLink?.attr("aria-controls")
            ?.removePrefix("week-")
            ?.toIntOrNull()

        return@withContext weekNumber ?: 1
    }

    /* Search */
    suspend fun parsePeople(element: Element): List<Person> = withContext(Dispatchers.Default) {
        val people = mutableListOf<Person>()
        element.select("div.search__people").forEach { item ->
            val aElement = item.selectFirst("a.mb-2")
            val spanElement = item.selectFirst("span[itemprop=Post]")
            if (aElement != null && spanElement != null) {
                val name = aElement.text()
                val id = aElement.attr("href")
                    .substringAfter("/people/")
                    .toIntOrNull() ?: -1
                val position = spanElement.text().trim()
                people.add(Person(name, id, position))
            }
        }
        return@withContext people
    }

    /* News */
    suspend fun parseNews(news: News): NewsContent = withContext(Dispatchers.Default) {
        val document = Jsoup.parse(news.content)
        val elements = document.select("p, li, tr, img")
        val parsedElements = mutableListOf<Pair<String, Any>>()
        val parsedImages = mutableListOf<String>()
        for (element in elements) {
            when (element.tagName()) {
                "p" -> {
                    val annotatedString = htmlToAnnotatedString(element.html())
                    if (annotatedString.isNotEmpty()) {
                        parsedElements.add(Pair("p", annotatedString))
                    }
                }

                "li" -> {
                    val annotatedString = htmlToAnnotatedString("• ${element.html()}")
                    if (annotatedString.isNotEmpty()) {
                        parsedElements.add(Pair("li", annotatedString))
                    }
                }

                "tr" -> {
                    val tableRow = element.select("td")
                    val tableRowItems = mutableListOf<String>()
                    tableRow.forEach { td ->
                        val text = td.text().trim()
                        if (text.isNotEmpty()) {
                            tableRowItems.add(text)
                        }
                    }
                    parsedElements.add(Pair("tr", tableRowItems))
                }

                "img" -> {
                    val imageUrl = element.attr("src")
                    if (imageUrl.isNotEmpty()) {
                        parsedImages.add("$BASE_MIIT_URL$imageUrl")
                    }
                }
            }
        }

        return@withContext NewsContent(
            news, parsedElements, parsedImages
        )
    }

    /* Subjects list */
    suspend fun parsePagesCount(element: Element): Int = withContext(Dispatchers.Default) {
        return@withContext element.select("ul.pagination li[data-page]")
            .mapNotNull {
                it.attr("data-page").toIntOrNull()
            }
            .maxOrNull() ?: 1
    }

    suspend fun parseListSubjectsByPage(
        element: Element
    ): MutableMap<String, MutableSet<String>> = withContext(
        Dispatchers.Default
    ) {
        val subjectTeachers = mutableMapOf<String, MutableSet<String>>()

        element.select("div[itemprop=teachingStaff]").forEach { item ->
            val teacher = item.selectFirst("a[itemprop=fio]")?.text()?.trim()
            val subjects = item.select("span[itemprop=teachingDiscipline]").mapNotNull {
                it.text().trim().takeIf { t -> t.isNotBlank() }
            }
            if (teacher.isNullOrBlank() || subjects.isEmpty()) {
                return@forEach
            }
            subjects.forEach { subject ->
                subjectTeachers.getOrPut(subject) {
                    mutableSetOf()
                }.add(teacher)
            }
        }
        return@withContext subjectTeachers
    }


    /* NORMALIZERS */
    private fun List<Event>.normalizePeriodicEvents(): List<Event> {
        return this.groupBy { it.customHashCode(true) }
            .map { (_, events) ->
                events.first().let { event ->
                    if (events.size > 1)
                        event.copy(
                            recurrenceRule = event.recurrenceRule?.copy(interval = 1)
                        )
                    else event
                }
            }
    }

    /* CONVERTERS */
    private fun htmlToAnnotatedString(html: String): AnnotatedString {
        val body = Jsoup.parse(html).body()

        return buildAnnotatedString {
            body.childNodes().forEach { node ->
                when (node) {
                    is TextNode -> {
                        append(node.text())
                    }

                    is Element -> {
                        if (node.tagName() == "a") {
                            val url = node.attr("href")
                            val linkText = node.text()

                            pushLink(
                                LinkAnnotation.Url(
                                    url = url,
                                    styles = TextLinkStyles(
                                        style = SpanStyle(
                                            textDecoration = TextDecoration.Underline,
                                            fontSize = 16.sp
                                        )
                                    )
                                )
                            )
                            append(linkText)
                            pop()
                        } else {
                            append(node.text())
                        }
                    }
                }
            }
        }
    }

    private fun getRecurrence(
        interval: Int,
        currentNumber: Int,
        startDate: LocalDate
    ): Recurrence {
        val today = LocalDate.now()
        return if (today > startDate && interval > 1) {
            val currentWeekIndex = abs(
                ChronoUnit.WEEKS.between(
                    startDate.getFirstDayOfWeek(),
                    today.getFirstDayOfWeek()
                )
            ).plus(1)

            val firstWeekNumber =
                ((currentWeekIndex + currentNumber) % interval)
                    .plus(1)

            Recurrence(
                interval, currentNumber,
                firstWeekNumber = firstWeekNumber.toInt()
            )
        } else {
            Recurrence(
                interval, currentNumber,
                firstWeekNumber = currentNumber
            )
        }
    }
}
