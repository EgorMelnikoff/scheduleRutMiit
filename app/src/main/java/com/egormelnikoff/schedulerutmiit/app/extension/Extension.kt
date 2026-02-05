package com.egormelnikoff.schedulerutmiit.app.extension

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.Recurrence
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun LocalDateTime.toLocaleTimeWithTimeZone(): LocalTime {
    return this.atZone(ZoneOffset.UTC)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalTime()
}

fun LocalDate.getFirstDayOfWeek(): LocalDate {
    return this.minusDays(this.dayOfWeek.value - 1L)
}

fun LocalDate.getCurrentWeek(
    startDate: LocalDate,
    recurrence: Recurrence
): Int {
    val weeksFromStart = abs(ChronoUnit.WEEKS.between(this, startDate)).plus(1).toInt()
    return ((weeksFromStart + recurrence.firstWeekNumber) % recurrence.interval).plus(1)
}

fun List<Event>.getGroupedEvents(): Map<String, List<Event>> {
    if (this.isEmpty()) return mapOf()
    return this
        .sortedBy { event ->
            event.startDatetime!!.toLocalTime()
        }.groupBy { event ->
            Pair(
                event.startDatetime!!.toLocalTime(),
                event.endDatetime!!.toLocalTime()
            ).toString()
        }
}

fun LocalDate.getEventsForDate(
    scheduleEntity: ScheduleEntity,
    periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
    nonPeriodicEvents: Map<LocalDate, List<Event>>?
): Map<String, List<Event>> {
    if (scheduleEntity.startDate > this || this > scheduleEntity.endDate) return mapOf()

    var displayedEvents = listOf<Event>()
    when {
        (periodicEvents != null) -> {
            val currentWeek = this@getEventsForDate.getCurrentWeek(
                startDate = scheduleEntity.startDate,
                recurrence = scheduleEntity.recurrence!!
            )
            val events = periodicEvents[currentWeek]!!.filter {
                it.key == this@getEventsForDate.dayOfWeek
            }.values.flatten()

            displayedEvents = events
        }

        (nonPeriodicEvents != null) -> {
            val events = nonPeriodicEvents.filter {
                it.key == this@getEventsForDate
            }
            displayedEvents = events.values.flatten()
        }
    }

    return displayedEvents.getGroupedEvents()
}

fun Map<Int, Map<DayOfWeek, List<Event>>>.getEventsByDayAndWeek(
    dayOfWeek: DayOfWeek,
    week: Int
): Map<String, List<Event>> {
    val events = this[week]!!.filter {
        it.key == dayOfWeek
    }.values.flatten()

    return events.getGroupedEvents()
}

fun String.getShortName(type: NamedScheduleType): String {
    if (type != NamedScheduleType.Person) return this
    val nameParts = this.split(" ")
    return if (nameParts.size == 3) {
        "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
    } else this
}

fun getTimeSlotName(
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime
): String? {
    if (Duration.between(startDateTime, endDateTime).toMinutes() != 80L) {
        return null
    }

    return when (startDateTime.hour) {
        5 if startDateTime.minute == 30 -> "1 пара"
        7 if startDateTime.minute == 5 -> "2 пара"
        8 if startDateTime.minute == 40 -> "3 пара"
        10 if startDateTime.minute == 45 -> "4 пара"
        12 if startDateTime.minute == 20 -> "5 пара"
        13 if startDateTime.minute == 55 -> "6 пара"
        15 if startDateTime.minute == 30 -> "7 пара"
        17 if startDateTime.minute == 0 -> "8 пара"
        18 if startDateTime.minute == 35 -> "9 пара"
        20 if startDateTime.minute == 10 -> "10 пара"
        else -> null
    }
}