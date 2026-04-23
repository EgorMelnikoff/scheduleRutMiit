package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.dto.GroupDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.LecturerDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.core.common.dto.RoomDto
import com.egormelnikoff.schedulerutmiit.core.common.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Keep
@Serializable
@Entity(tableName = "Events")
data class Event(
    @ColumnInfo(name = "EventId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventScheduleId")
    val scheduleId: Long = -1,
    val isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,

    @Serializable(with = LocalDateTimeSerializer::class)

    val startDatetime: LocalDateTime,
    @Serializable(with = LocalDateTimeSerializer::class)
    val endDatetime: LocalDateTime,
    @Embedded
    val recurrenceRule: RecurrenceEventDto?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<LecturerDto>?,
    val rooms: List<RoomDto>?,
    val groups: List<GroupDto>?
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

}