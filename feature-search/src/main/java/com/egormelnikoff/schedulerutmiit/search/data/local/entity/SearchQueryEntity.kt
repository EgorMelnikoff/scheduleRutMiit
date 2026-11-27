package com.egormelnikoff.schedulerutmiit.search.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.ScheduleType

@Entity(
    tableName = "SearchHistory"
)
internal data class SearchQueryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: ScheduleType
)