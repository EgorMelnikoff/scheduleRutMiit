package com.egormelnikoff.schedulerutmiit.schedule_feature.api.cache

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.FetchScheduleEvent
import kotlinx.coroutines.flow.Flow

interface FetchScheduleCache {
    val scheduleRequests: Flow<FetchScheduleEvent>

    fun addFetchEvent(
        fetchScheduleEvent: FetchScheduleEvent
    )
}