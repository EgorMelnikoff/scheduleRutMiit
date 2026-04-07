package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import javax.inject.Inject

class UpdateEventIsHiddenUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        eventId: Long,
        isHidden: Boolean,
    ): ScheduleUseCaseResult {
        eventRepos.updateIsHidden(eventId, isHidden)

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(namedScheduleId)
        )
    }
}