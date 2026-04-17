package com.egormelnikoff.schedulerutmiit.data.local.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.data.local.serializers.LocalDateTimeSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

@Keep
@Serializable
@Entity(tableName = "EventsExtraData")
data class EventExtraData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "eventId")
    val eventId: Long,
    @ColumnInfo(name = "eventExtraScheduleId")
    val scheduleId: Long = 0,
    val eventName: String?,
    @Serializable(with = LocalDateTimeSerializer::class)
    val eventStartDatetime: LocalDateTime?,
    val comment: String = "",
    val tag: Int = 0
)
