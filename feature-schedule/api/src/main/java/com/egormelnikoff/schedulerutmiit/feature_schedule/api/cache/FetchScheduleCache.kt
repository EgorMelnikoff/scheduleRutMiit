package com.egormelnikoff.schedulerutmiit.feature_schedule.api.cache

import com.egormelnikoff.schedulerutmiit.feature_schedule.api.model.FetchScheduleEvent
import kotlinx.coroutines.flow.Flow

interface FetchScheduleCache {
    val scheduleRequests: Flow<FetchScheduleEvent>

    fun addFetchEvent(
        fetchScheduleEvent: FetchScheduleEvent
    )
}