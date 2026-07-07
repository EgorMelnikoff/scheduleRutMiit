package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.database.entity.serializable.GroupEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.serializable.LecturerEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.serializable.RoomEntity
import java.time.LocalDateTime

@Entity(
    tableName = "Events",
    foreignKeys = [
//        ForeignKey(
//            entity = ScheduleEntity::class,
//            parentColumns = ["ScheduleId"],
//            childColumns = ["scheduleId"],
//            onDelete = ForeignKey.CASCADE
//        )
    ]
)
data class EventEntity(
    @ColumnInfo(name = "EventId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "eventScheduleId")
    val scheduleId: Long = 0,
    val isHidden: Boolean = false,
    val isCustomEvent: Boolean = false,
    val startDatetime: LocalDateTime,
    val endDatetime: LocalDateTime,
    val interval: Int?,
    val periodNumber: Int?,
    val name: String,
    val typeName: String?,
    val timeSlotName: String?,
    val lecturers: List<LecturerEntity>?,
    val rooms: List<RoomEntity>?,
    val groups: List<GroupEntity>?
)