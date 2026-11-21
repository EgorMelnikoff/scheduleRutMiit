package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.EventExtraDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.ScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.event.EventState
import java.time.LocalDate
import javax.inject.Inject

class LoadEventUseCase @Inject constructor(
    private val eventDataSource: EventDataSource,
    private val eventExtraDataSource: EventExtraDataSource,
    private val scheduleDataSource: ScheduleDataSource
) {
    suspend operator fun invoke(eventId: Long, date: LocalDate?): EventState.Loaded {
        val event = eventDataSource.getById(eventId)
        val eventExtraData = eventExtraDataSource.get(eventId, date)
        val schedule = scheduleDataSource.getById(event.scheduleId)

        return EventState.Loaded(
            event = event,
            schedule = schedule,
            comment = eventExtraData?.comment ?: "",
            tag = eventExtraData?.tag ?: 0
        )
    }
}