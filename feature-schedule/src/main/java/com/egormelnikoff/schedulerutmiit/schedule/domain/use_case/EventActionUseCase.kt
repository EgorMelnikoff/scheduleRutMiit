package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.widget.WidgetDataUpdater
import javax.inject.Inject

sealed class EventAction {
    data object Add : EventAction()
    data class Update(
        val updatableEvent: Event?
    ) : EventAction()

    data object Delete : EventAction()
    data class UpdateHidden(
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
        event: Event,
        eventAction: EventAction,
    ): NamedScheduleWithSchedules {
        when (eventAction) {
            is EventAction.Add -> eventRepos.save(event)
            is EventAction.Delete -> eventRepos.deleteById(event.id)
            is EventAction.Update -> if (event != eventAction.updatableEvent) eventRepos.update(
                event
            )

            is EventAction.UpdateHidden -> eventRepos.updateIsHidden(event.id, eventAction.isHidden)
        }

        namedScheduleRepos.getById(namedScheduleId).let {
            if (it.namedSchedule.isDefault) {
                widgetDataUpdater.updateAll()
            }
            return it
        }
    }
}