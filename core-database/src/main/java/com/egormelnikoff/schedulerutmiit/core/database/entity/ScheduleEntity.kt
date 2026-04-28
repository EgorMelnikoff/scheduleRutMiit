package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.TimetableType
import java.time.LocalDate

@Entity(tableName = "Schedules")
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

fun ScheduleEntity.toDomain() = Schedule(
    id,
    namedScheduleId,
    timetableId,
    timetableType,
    downloadUrl,
    startDate,
    endDate,
    recurrence?.toDomain(),
    isDefault
)

fun Schedule.toEntity(
    newNamedScheduleId: Long? = null
) = ScheduleEntity(
    id,
    newNamedScheduleId ?: namedScheduleId,
    timetableId,
    timetableType,
    downloadUrl,
    startDate,
    endDate,
    recurrence?.toEntity(),
    isDefault
)