package com.egormelnikoff.schedulerutmiit.search.domain.model

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.NamedScheduleType

internal data class SearchQuery(
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)