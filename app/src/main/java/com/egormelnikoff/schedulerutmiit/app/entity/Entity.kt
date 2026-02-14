package com.egormelnikoff.schedulerutmiit.app.entity

import android.content.Context
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.TimetableType
import com.egormelnikoff.schedulerutmiit.app.extension.toLocaleTimeWithTimeZone
import com.google.gson.JsonParseException
import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter
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
    val timetableType: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    @Embedded
    val recurrence: Recurrence?,
    @ColumnInfo(name = "isDefaultSchedule")
    var isDefault: Boolean = false,
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
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime!!.dayOfWeek}${startDatetime.toLocalTime()}$groups"
            (recurrenceRule != null) -> "$name$typeName${startDatetime!!.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
            else -> "$name$typeName$startDatetime$groups"
        }
        return hashString.hashCode()
    }

    fun customEquals(other: Event): Boolean {
        return this.customHashCode() == other.customHashCode()
    }

    fun customToString(context: Context): String {
        return StringBuilder().apply {
            append("${context.getString(R.string._class)}: ${this@Event.name}")
            this@Event.typeName?.let {
                append("\n${context.getString(R.string.class_type)}: $it")
            }
            append("\n${context.getString(R.string.time)}: ${this@Event.startDatetime!!.toLocaleTimeWithTimeZone()} - ${this@Event.endDatetime!!.toLocaleTimeWithTimeZone()}")

            this@Event.timeSlotName?.let {
                append(" ($it)")
            }

            if (!this@Event.rooms.isNullOrEmpty()) {
                append("\n${context.getString(R.string.place)}: ${this@Event.rooms.joinToString { it.name }}")
            }

            if (!this@Event.lecturers.isNullOrEmpty()) {
                append("\n${context.getString(R.string.lecturers)}: ${this@Event.lecturers.joinToString { it.shortFio }}")
            }

            if (!this@Event.groups.isNullOrEmpty()) {
                append("\n${context.getString(R.string.groups)}: ${this@Event.groups.joinToString { it.name }}")
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
    var scheduleId: Long = 0,

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

class NamedScheduleTypeAdapter : TypeAdapter<NamedScheduleType>() {
    override fun write(out: JsonWriter, value: NamedScheduleType?) {
        if (value == null) {
            out.nullValue()
            return
        }

        out.beginObject()
        out.name("id").value(value.id)
        out.name("name").value(value.typeName)
        out.endObject()
    }

    override fun read(reader: JsonReader): NamedScheduleType {
        return when (reader.peek()) {

            JsonToken.NUMBER -> {
                when (reader.nextInt()) {
                    0 -> NamedScheduleType.GROUP
                    1 -> NamedScheduleType.PERSON
                    2 -> NamedScheduleType.ROOM
                    3 -> NamedScheduleType.MY
                    else -> throw JsonParseException("Unknown NamedScheduleType id")
                }
            }

            JsonToken.BEGIN_OBJECT -> {
                var id: Int? = null

                reader.beginObject()
                while (reader.hasNext()) {
                    when (reader.nextName()) {
                        "id" -> id = reader.nextInt()
                        else -> reader.skipValue()
                    }
                }
                reader.endObject()

                when (id) {
                    0 -> NamedScheduleType.GROUP
                    1 -> NamedScheduleType.PERSON
                    2 -> NamedScheduleType.ROOM
                    3 -> NamedScheduleType.MY
                    else -> throw JsonParseException("Unknown NamedScheduleType id")
                }
            }

            else -> throw JsonParseException("Unexpected token for NamedScheduleType")
        }
    }
}
