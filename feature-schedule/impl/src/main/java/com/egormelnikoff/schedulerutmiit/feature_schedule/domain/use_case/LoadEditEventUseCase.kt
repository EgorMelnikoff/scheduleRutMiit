package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.ScheduleDataSource
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.feature_schedule.ui.add_event.EditForm
import javax.inject.Inject

class LoadEditEventUseCase @Inject constructor(
    private val scheduleDataSource: ScheduleDataSource,
    private val eventDataSource: EventDataSource,
) {
    suspend operator fun invoke(eventId: Long?, scheduleId: Long) = AddEventState.Loaded(
        schedule = scheduleDataSource.getById(scheduleId),
        editForm = EditForm(
            eventId?.let {
                eventDataSource.getById(it)
            }
        )
    )
}