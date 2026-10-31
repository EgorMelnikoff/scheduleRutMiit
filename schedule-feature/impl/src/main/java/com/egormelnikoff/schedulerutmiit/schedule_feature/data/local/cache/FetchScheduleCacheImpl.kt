package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.cache

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.cache.FetchScheduleCache
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.FetchScheduleEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

class FetchScheduleCacheImpl @Inject constructor(
) : FetchScheduleCache {
    private val _scheduleRequests = MutableSharedFlow<FetchScheduleEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val scheduleRequests = _scheduleRequests.asSharedFlow()

    override fun addFetchEvent(
        fetchScheduleEvent: FetchScheduleEvent
    ) {
        _scheduleRequests.tryEmit(fetchScheduleEvent)
    }
}