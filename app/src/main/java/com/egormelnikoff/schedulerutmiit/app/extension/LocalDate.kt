package com.egormelnikoff.schedulerutmiit.app.extension

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

fun LocalDate.getFirstDayOfWeek(): LocalDate = this.minusDays(this.dayOfWeek.value - 1L)

fun LocalDate.getCurrentWeek(
    startDate: LocalDate,
    recurrence: RecurrenceDto?
): Int {
    recurrence?.let {
        val weeksFromStart = abs(ChronoUnit.WEEKS.between(this, startDate)).plus(1).toInt()
        return ((weeksFromStart + recurrence.firstWeekNumber) % recurrence.interval).plus(1)
    }
    return -1
}

fun LocalDate.getEventsForDate(
    scheduleEntity: ScheduleEntity,
    periodicEvents: Map<Int, Map<DayOfWeek, List<Event>>>?,
    nonPeriodicEvents: Map<LocalDate, List<Event>>?
): Map<String, List<Event>> {
    if (scheduleEntity.startDate > this || this > scheduleEntity.endDate) return mapOf()

    var displayedEvents = listOf<Event>()
    when {
        (periodicEvents != null && scheduleEntity.recurrence != null) -> {
            val currentWeek = this@getEventsForDate.getCurrentWeek(
                startDate = scheduleEntity.startDate,
                recurrence = scheduleEntity.recurrence
            )
            val events = periodicEvents[currentWeek]?.filter {
                it.key == this@getEventsForDate.dayOfWeek
            }?.values?.flatten()

            events?.let {
                displayedEvents = events
            }
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
