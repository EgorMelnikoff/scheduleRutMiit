package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.core.database.entity.embedded.RecurrenceEntity
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