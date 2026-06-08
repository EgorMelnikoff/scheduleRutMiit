package com.egormelnikoff.schedulerutmiit.schedule.data.parser

import com.egormelnikoff.schedulerutmiit.core.common.DateTimeFormatters.parserFormatter
import com.egormelnikoff.schedulerutmiit.core.common.Locale.ruLocale
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.common.extension.toUtcTime
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.EventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.LecturerDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.NonPeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.PeriodicContentDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RoomDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.timetable.TimetableDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Year
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import kotlin.math.abs

object ScheduleParser {
    suspend operator fun invoke(
        document: Document,
        timetable: TimetableDto,
        currentGroup: Group?
    ): ScheduleDto = withContext(Dispatchers.Default) {
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
                ScheduleDto(
                    timetable = timetable,
                    periodic = PeriodicContentDto(
                        events = nonPeriodicContent.events,
                        recurrence = RecurrenceDto(
                            interval = 1,
                            currentNumber = 1,
                            firstWeekNumber = 1
                        )
                    ),
                    nonPeriodic = null
                )
            } else {
                ScheduleDto(
                    timetable = timetable,
                    periodic = periodicContent,
                    nonPeriodic = null
                )
            }
        } else {
            val nonPeriodicContent = document.parseNonPeriodicSchedule(
                currentGroup = currentGroup,
                formatter = parserFormatter
            )

            ScheduleDto(
                timetable = timetable,
                periodic = null,
                nonPeriodic = nonPeriodicContent
            )
        }
    }

    private suspend fun Document.parsePeriodicSchedule(
        startDate: LocalDate,
        currentGroup: Group?,
        formatter: DateTimeFormatter,
    ): PeriodicContentDto {
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
                            recurrenceRule = RecurrenceEventDto(
                                frequency = "WEEKLY",
                                interval = 2
                            ),
                            currentGroup = currentGroup
                        )
                    }.orEmpty()
                }.orEmpty()
        }

        return PeriodicContentDto(
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
    ): NonPeriodicContentDto {
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
        return if (isPeriodic) {
            NonPeriodicContentDto(
                events = events.map {
                    it.copy(
                        periodNumber = 1,
                        recurrence = RecurrenceEventDto(
                            frequency = "WEEKLY",
                            interval = 1
                        )
                    )
                }
            )
        } else NonPeriodicContentDto(
            events = events
        )
    }

    private fun Element.parseDate(
        isPeriodic: Boolean,
        formatter: DateTimeFormatter
    ): LocalDate? {
        val header = this@parseDate.selectFirst(".info-block__header-text")
            ?: return null

        val year = Year.now().value

        val dateText = if (isPeriodic) {
            header
                .select(".text-secondary.small")
                .firstOrNull()
                ?.text()
                ?.trim()
                .orEmpty()
        } else {
            header.ownText().trim()
        }

        if (dateText.isNotBlank()) {
            return LocalDate.parse("$dateText $year", formatter)
        }

        if (isPeriodic) {
            val dayOfWeek = DayOfWeek.entries.firstOrNull {
                it.getDisplayName(TextStyle.FULL, ruLocale)
                    .equals(header.ownText().trim(), ignoreCase = true)
            } ?: return null

            return LocalDate.now().with(
                TemporalAdjusters.nextOrSame(dayOfWeek)
            )
        } else {
            return null
        }

    }

    private suspend fun Element.parseEvents(
        date: LocalDate,
        periodNumber: Int? = null,
        recurrenceRule: RecurrenceEventDto? = null,
        currentGroup: Group?
    ): List<EventDto> {
        return this@parseEvents
            .select(".timetable__list-timeslot")
            .flatMap { timeslot ->
                val headerText = timeslot
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

                val subjectContainer = timeslot.selectFirst(".pl-4")
                val eventBlocks =
                    subjectContainer?.select("div.timetable__grid-about") ?: emptyList()

                if (eventBlocks.isEmpty()) {
                    val typeName = timeslot
                        .selectFirst(".timetable__grid-text_gray")
                        ?.text()
                        ?.trim()
                    val name = subjectContainer?.ownText()?.trim()

                    listOf(
                        EventDto(
                            startDatetime = LocalDateTime.of(date, startTime),
                            endDatetime = LocalDateTime.of(date, endTime),
                            name = name,
                            typeName = typeName,
                            timeSlotName = timeSlotName,
                            lecturers = timeslot.parseLecturers(),
                            rooms = timeslot.parseRooms(),
                            groups = timeslot.parseGroups(currentGroup),
                            periodNumber = periodNumber,
                            recurrence = recurrenceRule
                        )
                    )
                } else {
                    eventBlocks.map { aboutDiv ->
                        val previousTextNode = aboutDiv.previousSibling()
                        val name = (previousTextNode as? TextNode)?.text()?.trim()
                            ?: subjectContainer?.ownText()?.trim() ?: ""

                        val typeSpan = aboutDiv.previousElementSibling()
                        val typeName = typeSpan?.text()?.trim()
                            ?: timeslot.selectFirst(".timetable__grid-text_gray")?.text()?.trim()

                        EventDto(
                            startDatetime = LocalDateTime.of(date, startTime),
                            endDatetime = LocalDateTime.of(date, endTime),
                            name = name,
                            typeName = typeName,
                            timeSlotName = timeSlotName,
                            lecturers = aboutDiv.parseLecturers(),
                            rooms = aboutDiv.parseRooms(),
                            groups = aboutDiv.parseGroups(currentGroup),
                            periodNumber = periodNumber,
                            recurrence = recurrenceRule
                        )
                    }
                }
            }
    }

    private fun Element.parseLecturers(): MutableList<LecturerDto> {
        return this@parseLecturers
            .select(".icon-academic-cap")
            .mapNotNull { a ->

                val href = a.attr("href")
                val id = href
                    .substringAfter("/people/")
                    .substringBefore("/")
                    .toIntOrNull() ?: return@mapNotNull null

                val title = a.attr("title")

                LecturerDto(
                    id = id,
                    shortFio = a.text().trim(),
                    fullFio = title.substringBefore(",").trim(),
                    hint = title.substringAfter(",").trim()
                )
            }.toMutableList()
    }

    private fun Element.parseRooms(): MutableList<RoomDto> {
        return this@parseRooms
            .select(".icon-location")
            .mapNotNull { a ->

                val href = a.attr("href")

                val id = href
                    .substringAfter("room=")
                    .substringBefore("&")
                    .toIntOrNull() ?: return@mapNotNull null

                val title = a.attr("title")

                RoomDto(
                    id = id,
                    name = a.text().trim(),
                    hint = title.trim()
                )
            }.toMutableList()
    }

    private fun Element.parseGroups(
        currentGroup: Group?
    ): MutableList<GroupDto> {
        return this@parseGroups
            .select(".icon-community")
            .mapNotNull {
                val id = it.attr("href")
                    .substringAfter("/timetable/")
                    .toIntOrNull() ?: currentGroup?.id ?: return@mapNotNull null

                GroupDto(
                    id = id,
                    name = it.text().trim()
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

    private fun List<EventDto>.normalizePeriodicEvents(): List<EventDto> {
        return this.groupBy { it.customHashCode(true) }
            .map { (_, events) ->
                events.first().let { event ->
                    if (events.size > 1)
                        event.copy(
                            recurrence = event.recurrence?.copy(interval = 1)
                        )
                    else event
                }
            }
    }

    private fun getRecurrence(
        interval: Int,
        currentNumber: Int,
        startDate: LocalDate
    ): RecurrenceDto {
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

            RecurrenceDto(
                interval, currentNumber,
                firstWeekNumber = firstWeekNumber.toInt()
            )
        } else {
            RecurrenceDto(
                interval, currentNumber,
                firstWeekNumber = currentNumber
            )
        }
    }
}