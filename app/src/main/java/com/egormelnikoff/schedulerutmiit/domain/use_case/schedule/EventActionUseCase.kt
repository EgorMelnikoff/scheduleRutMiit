package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

sealed class EventAction {
    data object Add : EventAction()
    data object Update : EventAction()
    data object Delete : EventAction()
    data class UpdateHidden(
        val isHidden: Boolean
    ) : EventAction()
}

class EventActionUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos
) {
    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: Event,
        eventAction: EventAction,
    ): ScheduleUseCaseResult {
        when (eventAction) {
            is EventAction.Add -> eventRepos.save(event)
            is EventAction.Delete -> eventRepos.deleteById(event.id)
            is EventAction.Update -> eventRepos.update(event)
            is EventAction.UpdateHidden -> eventRepos.updateIsHidden(event.id, eventAction.isHidden)
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
        )
    }
}