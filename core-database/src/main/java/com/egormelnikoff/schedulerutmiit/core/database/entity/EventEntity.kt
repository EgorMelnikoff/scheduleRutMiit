package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import java.time.LocalDateTime

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
    val recurrenceRule: RecurrenceEventEntity?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<LecturerEntity>?,
    val rooms: List<RoomEntity>?,
    val groups: List<GroupEntity>?
)

fun EventEntity.toDomain() = Event(
    id,
    scheduleId,
    isHidden,
    isCustomEvent,
    startDatetime,
    endDatetime,
    recurrenceRule?.toDomain(),
    periodNumber,
    name,
    typeName,
    timeSlotName,
    lecturers?.map { it.toDomain() },
    rooms?.map { it.toDomain() },
    groups?.map { it.toDomain() }
)


fun Event.toEntity(
    newScheduleId: Long? = null
) = EventEntity(
    id,
    newScheduleId ?: scheduleId,
    isHidden,
    isCustomEvent,
    startDatetime,
    endDatetime,
    recurrenceRule?.toEntity(),
    periodNumber,
    name,
    typeName,
    timeSlotName,
    lecturers?.map { it.toEntity() },
    rooms?.map { it.toEntity() },
    groups?.map { it.toEntity() }
)