package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case.observer

import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import javax.inject.Inject

class ObserveHiddenEventsByScheduleIdUseCase @Inject constructor(
    private val eventRepos: EventRepos
) {
    operator fun invoke(scheduleId: Long) =
        eventRepos.observeHiddenEvents(scheduleId)
}