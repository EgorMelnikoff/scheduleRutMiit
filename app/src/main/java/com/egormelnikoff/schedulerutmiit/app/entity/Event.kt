package com.egormelnikoff.schedulerutmiit.app.entity

import android.content.Context
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.R
import com.egormelnikoff.schedulerutmiit.app.extension.toLocalTimeWithTimeZone
import java.time.LocalDateTime

@Keep
@Entity(tableName = "Events")
data class Event(
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
    val recurrenceRule: com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.RecurrenceEventDto?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.LecturerDto>?,
    val rooms: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.RoomDto>?,
    val groups: List<com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.event.GroupDto>?
) {
    fun customHashCode(forceNonPeriodic: Boolean = false): Int {
        val hashString = when {
            forceNonPeriodic -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}$groups"
            (recurrenceRule != null) -> "$name$typeName${startDatetime.dayOfWeek}${startDatetime.toLocalTime()}${recurrenceRule.interval}$periodNumber$groups"
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
            append("\n${context.getString(R.string.time)}: ${this@Event.startDatetime.toLocalTimeWithTimeZone()} - ${this@Event.endDatetime.toLocalTimeWithTimeZone()}")

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
