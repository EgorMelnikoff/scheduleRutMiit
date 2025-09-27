package com.egormelnikoff.schedulerutmiit.data.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.time.LocalDate
import java.time.LocalDateTime


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

@Entity(tableName = "NamedSchedules")
data class NamedScheduleEntity (
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

@Entity(tableName = "Schedules")
data class ScheduleEntity (
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


data class Recurrence(
    val frequency: String?,
    val interval: Int?,
    val currentNumber: Int?,
    val firstWeekNumber: Int
)

@Entity(tableName = "Events")
data class Event(
    @ColumnInfo(name = "EventId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventScheduleId")
    var scheduleId: Long,
    val startDatetime: LocalDateTime?,
    val endDatetime: LocalDateTime?,
    val isHidden: Boolean = false,
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
    override fun hashCode(): Int {
        val hashString = if (recurrenceRule != null) {
            "$name$typeName${startDatetime!!.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
        } else {
            "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return this.hashCode() == other.hashCode()
    }
}


@Entity(tableName = "EventsExtraData")
data class EventExtraData (
    @ColumnInfo(name = "EventExtraId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "eventExtraScheduleId")
    var scheduleId: Long = 0,

    val eventName: String?,
    val eventStartDatetime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)


data class RecurrenceRule(
    val frequency: String,
    val interval: Int
)

data class Lecturer (
    val id: Int?,
    val shortFio: String?,
    val fullFio: String?,
    val description: String?,
    val url: String?,
    val hint: String?
)

data class Room (
    val id: Int?,
    val name: String?,
    val url: String?,
    val hint: String?
)

data class Group (
    val id: Int?,
    val name: String?,
    val url: String?,
)