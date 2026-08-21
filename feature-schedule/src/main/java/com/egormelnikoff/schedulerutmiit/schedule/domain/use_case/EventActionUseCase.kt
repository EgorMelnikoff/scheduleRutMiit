package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import javax.inject.Inject

sealed interface EventAction {
    data class Add(
        val event: Event
    ) : EventAction

    data class Update(
        val event: Event
    ) : EventAction

    data class Delete(
        val eventId: Long
    ) : EventAction

    data class UpdateHidden(
        val eventId: Long,
        val isHidden: Boolean
    ) : EventAction
}

class EventActionUseCase @Inject constructor(
    private val eventRepos: EventRepos
) {
    suspend operator fun invoke(
        eventAction: EventAction
    ) {
        when (eventAction) {
            is EventAction.Add -> eventRepos.save(eventAction.event)
            is EventAction.Delete -> eventRepos.deleteById(eventAction.eventId)
            is EventAction.Update -> eventRepos.update(eventAction.event)

            is EventAction.UpdateHidden -> eventRepos.updateIsHidden(
                eventAction.eventId,
                eventAction.isHidden
            )
        }
    }
}