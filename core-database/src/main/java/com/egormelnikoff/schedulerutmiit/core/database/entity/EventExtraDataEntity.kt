package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(
    tableName = "EventsExtraData",
    foreignKeys = [
//        ForeignKey(
//            entity = EventEntity::class,
//            parentColumns = ["EventId"],
//            childColumns = ["eventId"],
//            onDelete = ForeignKey.CASCADE
//        )
    ]
)
data class EventExtraDataEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "eventId")
    val eventId: Long,
    @ColumnInfo(name = "eventExtraScheduleId")
    val scheduleId: Long = 0,
    val eventName: String?,
    val date: LocalDate?,
    val comment: String = "",
    val tag: Int = 0
)

