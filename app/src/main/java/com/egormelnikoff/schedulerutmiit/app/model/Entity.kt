package com.egormelnikoff.schedulerutmiit.app.model

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.math.abs

@Keep
data class NamedScheduleFormatted(
    @Embedded
    val namedScheduleEntity: NamedScheduleEntity,
    @Relation(
        entity = ScheduleEntity::class,
        parentColumn = "NamedScheduleId",
        entityColumn = "namedScheduleId"
    )
    val schedules: List<ScheduleFormatted>
)

@Keep
data class ScheduleFormatted(
    @Embedded
    val scheduleEntity: ScheduleEntity,
    @Relation(
        entity = Event::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventScheduleId"
    )
    val events: List<Event>,
    @Relation(
        entity = EventExtraData::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventExtraScheduleId"
    )
    val eventsExtraData: List<EventExtraData>
)

@Keep
@Entity(tableName = "NamedSchedules")
data class NamedScheduleEntity(
    @ColumnInfo(name = "NamedScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: Int,
    @ColumnInfo(name = "isDefaultNamedSchedule")
    var isDefault: Boolean,
    var lastTimeUpdate: Long
)

@Keep
@Entity(tableName = "Schedules")
data class ScheduleEntity(
    @ColumnInfo(name = "ScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "namedScheduleId")
    var namedScheduleId: Long,
    val timetableId: String,
    val typeName: String,
    val startName: String,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    @Embedded
    val recurrence: Recurrence?,
    @ColumnInfo(name = "isDefaultSchedule")
    var isDefault: Boolean = false,
)

@Keep
data class Recurrence(
    val frequency: String?,
    val interval: Int?,
    val currentNumber: Int?,
    val firstWeekNumber: Int
)

@Keep
@Entity(tableName = "Events")
data class Event(
    @ColumnInfo(name = "EventId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventScheduleId")
    var scheduleId: Long,
    val startDatetime: LocalDateTime?,
    val endDatetime: LocalDateTime?,
    var isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,
    @Embedded
    val recurrenceRule: RecurrenceRule?,
    val periodNumber: Int?,
    val name: String?,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?
) {
    fun customHashCode(): Int {
        val hashString = if (recurrenceRule != null) {
            "$name$typeName${startDatetime!!.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
        } else {
            "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun customEquals(other: Event): Boolean {
        return this.customHashCode() == other.customHashCode()
    }
}

@Keep
@Entity(tableName = "EventsExtraData")
data class EventExtraData(
    @ColumnInfo(name = "EventExtraId")
    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "eventExtraScheduleId")
    var scheduleId: Long = 0,

    val eventName: String?,
    val eventStartDatetime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)

fun LocalDateTime.toLocaleTimeWithTimeZone(): LocalTime {
    return this.atZone(ZoneOffset.UTC)
        .withZoneSameInstant(ZoneId.systemDefault())
        .toLocalTime()
}

fun LocalDate.getFirstDayOfWeek(): LocalDate {
    return this.minusDays(this.dayOfWeek.value - 1L)
}

fun getCurrentWeek(
    date: LocalDate,
    startDate: LocalDate,
    recurrence: Recurrence
): Int {
    val weeksFromStart = abs(ChronoUnit.WEEKS.between(date, startDate)).plus(1).toInt()
    return ((weeksFromStart + recurrence.firstWeekNumber) % recurrence.interval!!).plus(1)
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
    var displayedEvents = listOf<Event>()

    when {
        (periodicEvents != null) -> {
            val currentWeek = getCurrentWeek(
                date = this,
                startDate = scheduleEntity.startDate,
                recurrence = scheduleEntity.recurrence!!
            )
            val events = periodicEvents[currentWeek]!!.filter {
                it.key == this.dayOfWeek
            }.values.flatten()

            displayedEvents = events
        }

        (nonPeriodicEvents != null) -> {
            val events = nonPeriodicEvents.filter {
                it.key == this
            }
            displayedEvents = events.values.flatten()
        }
    }

    return displayedEvents.getGroupedEvents()
}

fun getTimeSlotName(
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime
): String? {
    if (Duration.between(startDateTime, endDateTime).toMinutes() != 80L) {
        return null
    }

    return when {
        startDateTime.hour == 5 && startDateTime.minute == 30 -> "1 пара"
        startDateTime.hour == 7 && startDateTime.minute == 5 -> "2 пара"
        startDateTime.hour == 8 && startDateTime.minute == 40 -> "3 пара"
        startDateTime.hour == 10 && startDateTime.minute == 45 -> "4 пара"
        startDateTime.hour == 12 && startDateTime.minute == 20 -> "5 пара"
        startDateTime.hour == 13 && startDateTime.minute == 55 -> "6 пара"
        startDateTime.hour == 15 && startDateTime.minute == 30 -> "7 пара"
        startDateTime.hour == 17 && startDateTime.minute == 0 -> "8 пара"
        startDateTime.hour == 18 && startDateTime.minute == 35 -> "9 пара"
        startDateTime.hour == 20 && startDateTime.minute == 10 -> "10 пара"
        else -> null
    }
}