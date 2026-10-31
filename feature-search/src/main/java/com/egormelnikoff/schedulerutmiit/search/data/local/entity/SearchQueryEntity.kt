package com.egormelnikoff.schedulerutmiit.search.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType

@Entity(
    tableName = "SearchHistory"
)
internal data class SearchQueryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)