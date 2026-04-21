package com.egormelnikoff.schedulerutmiit.app.extension

import com.egormelnikoff.schedulerutmiit.app.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

fun List<Event>.getGroupedEvents(): Map<String, List<Event>> {
    if (this.isEmpty()) return mapOf()
    return this
        .sortedBy { event ->
            event.startDatetime.toLocalTime()
        }.groupBy { event ->
            Pair(
                event.startDatetime.toLocalTime(),
                event.endDatetime.toLocalTime()
            ).toString()
        }
}

fun List<Event>.getPeriodicEvents(
    interval: Int
): Map<Int, Map<DayOfWeek, List<Event>>> {
    return buildMap {
        for (week in 1..interval) {
            val eventsForWeek = this@getPeriodicEvents.filter { event ->
                val rule = event.recurrenceRule ?: return@filter false
                (rule.interval == 1 || event.periodNumber == week)
            }
            if (eventsForWeek.isNotEmpty()) {
                this[week] = eventsForWeek.groupBy { it.startDatetime.dayOfWeek }
            } else {
                this[week] = emptyMap()
            }
        }
    }
}

fun List<EventExtraData>.findEventExtra(
    eventExtraPolicy: EventExtraPolicy,
    event: Event,
    dateTime: LocalDateTime
): EventExtraData? {
    return if (eventExtraPolicy == EventExtraPolicy.BY_DATES) {
        this.find { it.eventId == event.id && it.dateTime == dateTime }
    } else {
        this.find { it.eventId == event.id }
    }
}

fun List<Pair<String, List<Event>>>?.getEnrichedEvents(
    date: LocalDate,
    eventExtraPolicy: EventExtraPolicy,
    eventsExtraData: List<EventExtraData>
): List<Pair<String, List<Pair<Event, EventExtraData?>>>> {
    return this?.map { (title, events) ->
        val enriched = events.map { event ->
            val extra = eventsExtraData.findEventExtra(
                eventExtraPolicy,
                event,
                LocalDateTime.of(date, event.startDatetime.toLocalTime())
            )
            event to extra
        }
        title to enriched
    } ?: emptyList()
}