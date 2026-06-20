package com.egormelnikoff.schedulerutmiit.schedule.data.extension

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.extension.replaceDate
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime

fun List<Event>.getGroupedEvents(): Map<String, List<Event>> {
    if (this.isEmpty()) return mapOf()
    return this
        .sortedBy { event ->
            event.startDatetime.toLocalTime()
        }.groupBy { event ->
            (event.startDatetime.toLocalTime() to event.endDatetime.toLocalTime()).toString()
        }
}

fun List<Event>.getPeriodicEvents(
    interval: Int
): Map<Int, Map<DayOfWeek, List<Event>>> {
    return buildMap {
        for (week in 1..interval) {
            val eventsForWeek = this@getPeriodicEvents.filter { event ->
                event.interval ?: return@filter false
                (event.interval == 1 || event.periodNumber == week)
            }
            if (eventsForWeek.isNotEmpty()) {
                this[week] = eventsForWeek.groupBy { it.startDatetime.dayOfWeek }
            } else {
                this[week] = emptyMap()
            }
        }
    }
}

fun Map<Long, EventExtraData>.findEventExtra(
    eventExtraPolicy: EventExtraPolicy,
    eventId: Long,
    dateTime: LocalDateTime
): EventExtraData? {
    return if (eventExtraPolicy == EventExtraPolicy.BY_DATES) {
        this.values.find { it.eventId == eventId && it.dateTime == dateTime }
    } else {
        this[eventId]
    }
}

fun Map.Entry<String, List<Event>>.getEnrichedEvents(
    eventsExtraData: Map<Long, EventExtraData>,
    eventExtraPolicy: EventExtraPolicy,
    date: LocalDate
): List<Pair<Event, EventExtraData?>> {
    return this.value.map { event ->
        event to eventsExtraData.findEventExtra(
            eventExtraPolicy = eventExtraPolicy,
            eventId = event.id,
            dateTime = event.startDatetime.replaceDate(date)
        )
    }
}

fun Map<String, List<Event>>?.getEnrichedEvents(
    date: LocalDate,
    eventExtraPolicy: EventExtraPolicy,
    eventsExtraData: Map<Long, EventExtraData>
): List<Pair<String, List<Pair<Event, EventExtraData?>>>> {
    if (this == null) return emptyList()

    return this.map { entry ->
        val enriched = entry.getEnrichedEvents(eventsExtraData, eventExtraPolicy, date)
        entry.key to enriched
    }
}


fun Event.customToString(context: Context): String {
    return buildString {
        append("${context.getString(R.string._class)}: ${this@customToString.name}")
        this@customToString.typeName?.let {
            append("\n${context.getString(R.string.class_type)}: $it")
        }
        append("\n${context.getString(R.string.time)}: ${this@customToString.startDatetime.toLocalTimeWithTimeZone()} - ${this@customToString.endDatetime.toLocalTimeWithTimeZone()}")

        this@customToString.timeSlotName?.let {
            append(" ($it)")
        }

        if (!this@customToString.rooms.isNullOrEmpty()) {
            append("\n${context.getString(R.string.place)}: ${this@customToString.rooms?.joinToString { it.name }}")
        }

        if (!this@customToString.lecturers.isNullOrEmpty()) {
            append("\n${context.getString(R.string.lecturers)}: ${this@customToString.lecturers?.joinToString { it.shortFio }}")
        }

        if (!this@customToString.groups.isNullOrEmpty()) {
            append("\n${context.getString(R.string.groups)}: ${this@customToString.groups?.joinToString { it.name }}")
        }
    }
}