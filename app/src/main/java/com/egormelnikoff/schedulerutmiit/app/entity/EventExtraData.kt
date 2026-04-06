package com.egormelnikoff.schedulerutmiit.app.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

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
