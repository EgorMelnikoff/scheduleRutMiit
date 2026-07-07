package com.egormelnikoff.schedulerutmiit.core.common.domain

import com.egormelnikoff.schedulerutmiit.core.common.enums.NamedScheduleType

data class SearchQuery(
    val id: Long = 0,
    val name: String,
    val apiId: Int,
    val namedScheduleType: NamedScheduleType
)