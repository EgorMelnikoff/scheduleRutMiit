package com.egormelnikoff.schedulerutmiit.app.entity

import android.content.Context
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import java.time.LocalDate
import java.time.LocalDateTime

@Keep
@Entity(tableName = "NamedSchedules")
data class NamedScheduleEntity(
    @ColumnInfo(name = "NamedScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    @ColumnInfo(name = "isDefaultNamedSchedule")
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)

@Keep
@Entity(tableName = "Schedules")
data class ScheduleEntity(
    @ColumnInfo(name = "ScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "namedScheduleId")
    val namedScheduleId: Long,
    val timetableId: String,
    val timetableType: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    @Embedded
    val recurrence: Recurrence?,
    @ColumnInfo(name = "isDefaultSchedule")
    val isDefault: Boolean = false,
) {
    fun getKey(): Int = "$startDate${timetableType.name}".hashCode()
}

@Keep
@Entity(tableName = "Events")
data class EventEntity(
    @ColumnInfo(name = "EventId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventScheduleId")
    val scheduleId: Long = -1,
    val isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,

    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    @Embedded
    val recurrenceRule: RecurrenceRule?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<Lecturer>?,
    val rooms: List<Room>?,
    val groups: List<Group>?
) {
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}$groups"
            (recurrenceRule != null) -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun customEquals(other: EventEntity): Boolean {
        return this.customHashCode() == other.customHashCode()
    }

    fun customToString(context: Context): String {
        return StringBuilder().apply {
            append("${context.getString(R.string._class)}: ${this@EventEntity.name}")
            this@EventEntity.typeName?.let {
                append("\n${context.getString(R.string.class_type)}: $it")
            }
            append("\n${context.getString(R.string.time)}: ${this@EventEntity.startDatetime.toLocalTimeWithTimeZone()} - ${this@EventEntity.endDatetime.toLocalTimeWithTimeZone()}")

            this@EventEntity.timeSlotName?.let {
                append(" ($it)")
            }

            if (!this@EventEntity.rooms.isNullOrEmpty()) {
                append("\n${context.getString(R.string.place)}: ${this@EventEntity.rooms.joinToString { it.name }}")
            }

            if (!this@EventEntity.lecturers.isNullOrEmpty()) {
                append("\n${context.getString(R.string.lecturers)}: ${this@EventEntity.lecturers.joinToString { it.shortFio }}")
            }

            if (!this@EventEntity.groups.isNullOrEmpty()) {
                append("\n${context.getString(R.string.groups)}: ${this@EventEntity.groups.joinToString { it.name }}")
            }
        }.toString()
    }
}

@Keep
@Entity(tableName = "EventsExtraData")
data class EventExtraData(
    @ColumnInfo(name = "EventExtraId")
    @PrimaryKey
    val id: Long = 0,

    @ColumnInfo(name = "eventExtraScheduleId")
    val scheduleId: Long = 0,

    val eventName: String?,
    val eventStartDatetime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)

@Keep
@Entity(tableName = "SearchHistory")
data class SearchQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)

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
        entity = EventEntity::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventScheduleId"
    )
    val events: List<EventEntity>,
    @Relation(
        entity = EventExtraData::class,
        parentColumn = "ScheduleId",
        entityColumn = "eventExtraScheduleId"
    )
    val eventsExtraData: List<EventExtraData> = listOf()
)

@Keep
data class Recurrence(
    val interval: Int,
    val currentNumber: Int,
    val firstWeekNumber: Int
)

@Keep
data class RecurrenceRule(
    val frequency: String,
    val interval: Int
)

@Keep
data class Group(
    val id: Int,
    val name: String
)

@Keep
data class Lecturer(
    val id: Int,
    val shortFio: String,
    val fullFio: String,
    val hint: String
)

@Keep
data class Room(
    val id: Int,
    val name: String,
    val hint: String
)