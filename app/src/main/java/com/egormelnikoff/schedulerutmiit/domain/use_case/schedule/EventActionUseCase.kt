package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import kotlinx.coroutines.flow.first
import javax.inject.Inject

sealed class EventAction {
    data object Add : EventAction()
    data object Update : EventAction()
    data object Delete : EventAction()
    data class UpdateHidden(
        val isHidden: Boolean
    ) : EventAction()

    data class UpdateExtra(
        val tag: Int,
        val comment: String
    ) : EventAction()
}

class EventActionUseCase @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos,
    private val eventExtraRepos: EventExtraRepos
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
            is EventAction.UpdateExtra -> updateEventExtra(
                event,
                eventAction.tag,
                eventAction.comment
            )
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
        )
    }


    private suspend fun updateEventExtra(
        event: Event,
        tag: Int,
        comment: String
    ) {
        if (comment == "" && tag == 0) {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.deleteByEventId(it.id)
            }
            return
        }

        val eventExtraData = eventExtraRepos.getByEventId(event.id)

        if (eventExtraData != null) {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.updateComment(it, comment)
            }
            synchronizeEventExtraAction(event) {
                eventExtraRepos.updateTag(it, tag)
            }
        } else {
            synchronizeEventExtraAction(event) {
                eventExtraRepos.save(it, tag, comment)
            }

        }
    }

    private suspend fun synchronizeEventExtraAction(
        event: Event,
        action: suspend (Event) -> Unit
    ) {
        if (preferencesDataStore.syncTagCommentsFlow.first()) {
            eventRepos.getByNameAndType(
                event.name,
                event.typeName,
                event.scheduleId
            ).forEach { event ->
                action(event)
            }
        } else {
            action(event)
        }
    }
}