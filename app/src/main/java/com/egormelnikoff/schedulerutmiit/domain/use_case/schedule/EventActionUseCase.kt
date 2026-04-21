package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.ui.widget.WidgetDataUpdater
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
    private val eventRepos: EventRepos,
    private val widgetDataUpdater: WidgetDataUpdater
) {
    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: Event,
        eventAction: EventAction,
    ): NamedSchedule {
        when (eventAction) {
            is EventAction.Add -> eventRepos.save(event)
            is EventAction.Delete -> eventRepos.deleteById(event.id)
            is EventAction.Update -> eventRepos.update(event)
            is EventAction.UpdateHidden -> eventRepos.updateIsHidden(event.id, eventAction.isHidden)
        }

        namedScheduleRepos.getById(scheduleEntity.namedScheduleId).let {
            if (it.namedScheduleEntity.isDefault) {
                widgetDataUpdater.updateAll()
            }
            return it
        }
    }
}