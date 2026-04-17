package com.egormelnikoff.schedulerutmiit.data.local.db.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.app.enums.TimetableType
import com.egormelnikoff.schedulerutmiit.data.local.serializers.LocalDateSerializer
import com.egormelnikoff.schedulerutmiit.data.remote.dto.schedule.RecurrenceDto
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