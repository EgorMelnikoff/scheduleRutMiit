package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import javax.inject.Inject

class UpdateEventUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
       event: Event
    ): ScheduleUseCaseResult {
        eventRepos.update(event)

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}