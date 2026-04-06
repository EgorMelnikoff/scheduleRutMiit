package com.egormelnikoff.schedulerutmiit.app.entity

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType

@Keep
@Entity(tableName = "NamedSchedules")
data class NamedScheduleEntity(
    @ColumnInfo(name = "NamedScheduleId")
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val shortName: String,
    val apiId: String?,
    val type: NamedScheduleType,
    @ColumnInfo(name = "isDefaultNamedSchedule")
    val isDefault: Boolean,
    val lastTimeUpdate: Long
)