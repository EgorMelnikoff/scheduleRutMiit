package com.egormelnikoff.schedulerutmiit.schedule.extension

import android.content.Context
import com.egormelnikoff.schedulerutmiit.core.common.R
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.extension.toLocalTimeWithTimeZone
import com.egormelnikoff.schedulerutmiit.core.common.entity.Event
import com.egormelnikoff.schedulerutmiit.core.common.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.EventDto
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

fun Event.customToString(context: Context): String {
    return StringBuilder().apply {
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
    }.toString()
}

fun EventDto.toEntity() = Event(
    startDatetime = requireNotNull(this.startDatetime),
    endDatetime = requireNotNull(this.endDatetime),
    recurrenceRule = recurrence,
    periodNumber = periodNumber,
    name = requireNotNull(this.name),
    typeName = typeName,
    timeSlotName = timeSlotName,
    lecturers = lecturers,
    groups = groups,
    rooms = rooms
)