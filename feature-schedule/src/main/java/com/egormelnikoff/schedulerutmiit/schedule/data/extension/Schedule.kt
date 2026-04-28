package com.egormelnikoff.schedulerutmiit.schedule.data.extension

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun List<ScheduleWithEvents>.findDefaultSchedule(): ScheduleWithEvents? {
    return this.find { it.schedule.isDefault } ?: this.firstOrNull()
}

fun Schedule.getEventsForDate(
    date: LocalDate,
    periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
    nonPeriodicEvents: Map<LocalDate, List<Event>>?
): Map<String, List<Event>> {
    if (this.startDate > date || date > this.endDate) return mapOf()

    var displayedEvents = listOf<Event>()
    when {
        (periodicEvents != null && this.recurrence != null) -> {
            val currentWeek = date.getCurrentWeek(
                startDate = this.startDate,
                recurrence = this.recurrence
            )
            val events = periodicEvents[currentWeek]?.filter {
                it.key == date.dayOfWeek
            }?.values?.flatten()

            events?.let {
                displayedEvents = events
            }
        }

        (nonPeriodicEvents != null) -> {
            val events = nonPeriodicEvents.filter {
                it.key == date
            }
            displayedEvents = events.values.flatten()
        }
    }

    return displayedEvents.getGroupedEvents()
}

fun String.getShortName(type: NamedScheduleType): String {
    if (type != NamedScheduleType.PERSON) return this
    val nameParts = this.split(" ")
    return if (nameParts.size == 3) {
        "${nameParts.first()} ${nameParts[1][0]}. ${nameParts[2][0]}."
    } else this
}

fun getTimeSlotName(
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?
): String? {
    if (Duration.between(startDateTime, endDateTime).toMinutes() != 80L) {
        return null
    }

    return when (startDateTime?.hour) {
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

fun LocalDate.getCurrentWeek(
    startDate: LocalDate,
    recurrence: Recurrence?
): Int {
    recurrence?.let {
        val weeksFromStart = abs(ChronoUnit.WEEKS.between(this, startDate)).plus(1).toInt()
        return ((weeksFromStart + recurrence.firstWeekNumber) % recurrence.interval).plus(1)
    }
    return -1
}
