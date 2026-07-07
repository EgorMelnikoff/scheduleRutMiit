package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.ui.dialog.add_event.view_model.state.EditEventState
import javax.inject.Inject

class LoadEditEventUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(eventId: Long?, scheduleId: Long) = EditEventState.Loaded(
        schedule = scheduleRepos.getById(scheduleId)
    )

}