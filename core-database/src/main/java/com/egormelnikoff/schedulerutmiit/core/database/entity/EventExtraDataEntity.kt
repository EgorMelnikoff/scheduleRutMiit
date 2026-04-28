package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDateTime

@Entity(tableName = "EventsExtraData")
data class EventExtraDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "eventId")
    val eventId: Long,
    @ColumnInfo(name = "eventExtraScheduleId")
    val scheduleId: Long = 0,
    val eventName: String?,
    val dateTime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)

fun EventExtraDataEntity.toDomain() = EventExtraData(
    id, eventId, scheduleId, eventName, dateTime, comment, tag
)

fun EventExtraData.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, dateTime, comment, tag
)