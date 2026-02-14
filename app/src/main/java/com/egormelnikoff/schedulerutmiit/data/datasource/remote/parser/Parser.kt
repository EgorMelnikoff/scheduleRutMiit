package com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.Lecturer
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.RecurrenceRule
import com.egormelnikoff.schedulerutmiit.app.entity.Room
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.toUtcTime
import com.egormelnikoff.schedulerutmiit.app.model.News
import com.egormelnikoff.schedulerutmiit.app.model.NonPeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.PeriodicContent
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetable
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.Endpoints.BASE_MIIT_URL
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.util.Locale

object Parser {
    /* Schedule */
    fun parseSchedule(
        document: Document,
        timetable: Timetable,
        currentGroup: Group?
    ): Result<Schedule> {
        return if (timetable.type == TimetableType.PERIODIC) {
            parsePeriodicSchedule(
                document,
                timetable,
                currentGroup
            )
        } else {
            parseNonPeriodicSchedule(
                document,
                timetable,
                currentGroup
            )
        }
    }

    private fun parsePeriodicSchedule(
        document: Document,
        timetable: Timetable,
        currentGroup: Group?
    ): Result<Schedule> {
        val weekNumbers = document
            .select(".nav-link[aria-controls]")
            .map {
                it.attr("aria-controls")
                    .removePrefix("week-")
                    .toInt()
            }

        val events = weekNumbers.flatMap { periodNumber ->
            document.getElementById("week-$periodNumber")
                ?.select("div.info-block.info-block_collapse.show")
                ?.flatMap { element ->
                    element.parseDate(true)?.let { date ->
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

        return Result.Success(
            Schedule(
                timetable = timetable,
                periodicContent = PeriodicContent(
                    events = events.normalizeEvents(),
                    recurrence = Recurrence(
                        interval = weekNumbers.size,
                        currentNumber = parseCurrentWeek(document),
                        firstWeekNumber = 1
                    )
                ),
                nonPeriodicContent = null
            )
        )

    }

    private fun parseNonPeriodicSchedule(
        document: Document,
        timetable: Timetable,
        currentGroup: Group?
    ): Result<Schedule> {
        val eventsByDates = document.select("div.info-block.info-block_collapse.show")

        val events = eventsByDates.flatMap { element ->
            element.parseDate(false)?.let { date ->
                element.parseEvents(
                    date = date,
                    currentGroup = currentGroup
                )
            }.orEmpty()
        }

        return Result.Success(
            Schedule(
                timetable = timetable,
                periodicContent = null,
                nonPeriodicContent = NonPeriodicContent(
                    events = events
                )
            )
        )
    }

    private fun Element.parseDate(isPeriodic: Boolean): LocalDate? {
        val header = this.selectFirst(".info-block__header-text")

        val dateText = if (isPeriodic) {
            header
                ?.select(".text-secondary.small")
                ?.first()
                ?.text()
                ?.trim() ?: return null
        } else {
            header
                ?.ownText()
                ?.trim() ?: return null
        }

        val formatter = DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.of("ru", "RU"))
        val year = Year.now().value

        return LocalDate.parse("$dateText $year", formatter)
    }

    private fun Element.parseEvents(
        date: LocalDate,
        periodNumber: Int? = null,
        recurrenceRule: RecurrenceRule? = null,
        currentGroup: Group?
    ): List<Event> {
        return this
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
                val groups = element.parseGroups()

                if (groups.isNotEmpty() && currentGroup != null) {
                    groups.addFirst(currentGroup)
                }

                Event(
                    startDatetime = LocalDateTime.of(date, startTime),
                    endDatetime = LocalDateTime.of(date, endTime),
                    scheduleId = -1,
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

    private fun Element.parseLecturers(): MutableList<Lecturer> {
        return this
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

    private fun Element.parseRooms(): MutableList<Room> {
        return this
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

    private fun Element.parseGroups(): MutableList<Group> {
        return this
            .select(".icon-community")
            .mapNotNull { a ->

                val href = a.attr("href")

                val id = href
                    .substringAfter("/timetable/")
                    .toIntOrNull() ?: return@mapNotNull null

                Group(
                    id = id,
                    name = a.text().trim()
                )
            }.toMutableList()
    }

    fun parseCurrentWeek(element: Element): Int {
        val activeLink = element.select(".nav-link[aria-controls].active").first()

        val weekNumber = activeLink?.attr("aria-controls")
            ?.removePrefix("week-")
            ?.toIntOrNull()

        return weekNumber ?: 1
    }

    /* Search */
    fun parsePeople(element: Element): List<Person> {
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
        return people
    }

    /* News */
    fun parseNews(news: News): News {
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

        news.elements = parsedElements
        news.images = parsedImages
        return news
    }

    /* Subjects list */
    fun parsePagesCount(element: Element): Int {
        return element.select("ul.pagination li[data-page]")
            .mapNotNull {
                it.attr("data-page").toIntOrNull()
            }
            .maxOrNull() ?: 1
    }

    fun parseListSubjectsByPage(element: Element): MutableMap<String, MutableSet<String>> {
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
        return subjectTeachers
    }


    /* NORMALIZERS */
    private fun List<Event>.normalizeEvents(): List<Event> {
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
}
