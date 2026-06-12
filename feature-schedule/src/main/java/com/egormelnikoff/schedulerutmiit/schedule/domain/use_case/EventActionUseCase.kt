package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import javax.inject.Inject

sealed class EventAction {
    data class Add(
        val event: Event
    ) : EventAction()

    data class Update(
        val updatedEvent: Event,
        val updatableEvent: Event?
    ) : EventAction()

    data class Delete(
        val eventId: Long
    ) : EventAction()

    data class UpdateHidden(
        val eventId: Long,
        val isHidden: Boolean
    ) : EventAction()
}

class EventActionUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos,
    private val widgetDataUpdater: WidgetDataUpdater
) {
    suspend operator fun invoke(
        namedScheduleId: Long,
        eventAction: EventAction
    ): NamedScheduleWithSchedules {
        when (eventAction) {
            is EventAction.Add -> eventRepos.save(eventAction.event)
            is EventAction.Delete -> eventRepos.deleteById(eventAction.eventId)
            is EventAction.Update -> if (eventAction.updatedEvent != eventAction.updatableEvent) eventRepos.update(
                eventAction.updatedEvent
            )

            is EventAction.UpdateHidden -> eventRepos.updateIsHidden(
                eventAction.eventId,
                eventAction.isHidden
            )
        }

        namedScheduleRepos.getById(namedScheduleId).let {
            if (it.namedSchedule.isDefault) {
                widgetDataUpdater.updateAll()
            }
            return it
        }
    }
}