package com.egormelnikoff.schedulerutmiit.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType

@Entity(tableName = "SearchHistory")
data class SearchQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)