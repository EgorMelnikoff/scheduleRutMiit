package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.TimetableType
import java.time.LocalDate

@Entity(
    tableName = "Schedules",
    foreignKeys = [
//        ForeignKey(
//            entity = NamedScheduleEntity::class,
//            parentColumns = ["NamedScheduleId"],
//            childColumns = ["namedScheduleId"],
//            onDelete = ForeignKey.CASCADE
//        )
    ]
)
data class ScheduleEntity(
    @ColumnInfo(name = "ScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "namedScheduleId")
    val namedScheduleId: Long,
    val timetableId: String,
    val timetableType: TimetableType,
    val downloadUrl: String?,
    val startDate: LocalDate,
    val endDate: LocalDate,
    @Embedded
    val recurrence: RecurrenceEntity?,
    @ColumnInfo(name = "isDefaultSchedule")
    val isDefault: Boolean = false,
)