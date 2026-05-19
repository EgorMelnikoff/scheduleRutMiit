package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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

