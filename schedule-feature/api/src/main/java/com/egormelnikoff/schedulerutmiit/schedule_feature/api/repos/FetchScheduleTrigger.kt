package com.egormelnikoff.schedulerutmiit.schedule_feature.api.repos

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleType

interface FetchScheduleTrigger {
    suspend fun trigger(
        name: String = "",
        apiId: Int = 0,
        type: NamedScheduleType = NamedScheduleType.GROUP,
    )
}