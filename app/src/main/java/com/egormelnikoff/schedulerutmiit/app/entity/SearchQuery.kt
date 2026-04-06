package com.egormelnikoff.schedulerutmiit.app.entity

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType

@Keep
@Entity(tableName = "SearchHistory")
data class SearchQuery(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)