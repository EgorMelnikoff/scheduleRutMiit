package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType

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

fun NamedScheduleEntity.toDomain() = NamedSchedule(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

fun NamedSchedule.toEntity() = NamedScheduleEntity(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)