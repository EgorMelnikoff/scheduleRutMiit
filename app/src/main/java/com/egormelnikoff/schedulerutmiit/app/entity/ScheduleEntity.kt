package com.egormelnikoff.schedulerutmiit.app.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.app.dto.remote.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.app.serializers.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Keep
@Serializable
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
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    @Embedded
    val recurrence: RecurrenceDto?,
    @ColumnInfo(name = "isDefaultSchedule")
    val isDefault: Boolean = false,
) {
    fun getKey(): Int = "$startDate${timetableType.name}".hashCode()
}