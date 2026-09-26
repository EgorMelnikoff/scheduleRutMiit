package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.AddEventState
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.EditForm
import javax.inject.Inject

class LoadEditEventUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val loadEventUseCase: LoadEventUseCase,
) {
    suspend operator fun invoke(eventId: Long?, scheduleId: Long) = AddEventState.Loaded(
        schedule = scheduleRepos.getById(scheduleId),
        editForm = EditForm(
            eventId?.let {
                loadEventUseCase(it, null).event
            }
        )
    )
}