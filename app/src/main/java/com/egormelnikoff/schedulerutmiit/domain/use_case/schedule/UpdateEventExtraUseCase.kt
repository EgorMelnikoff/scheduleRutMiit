package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.NamedScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.use_case.schedule.result.ScheduleUseCaseResult
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateEventExtraUseCase @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos,
    private val eventExtraRepos: EventExtraRepos
) {
    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: Event,
        dateTime: LocalDateTime,
        tag: Int,
        comment: String
    ): ScheduleUseCaseResult {
        val policy = preferencesDataStore.eventExtraPolicyFlow.first()

        if (comment == "" && tag == 0) {
            eventExtraAction(policy, event, dateTime) { event, dateTime ->
                eventExtraRepos.delete(event.id, dateTime)
            }
            return ScheduleUseCaseResult(
                savedNamedScheduleEntities = null,
                namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
            )
        }


        val eventExtraData = eventExtraRepos.get(
            event.id,
            if (policy == EventExtraPolicy.BY_DATES) dateTime else null
        )

        if (eventExtraData != null) {
            eventExtraAction(policy, event, dateTime) { event, dateTime ->
                eventExtraRepos.updateComment(event, dateTime, comment)
            }
            eventExtraAction(policy, event, dateTime) { event, dateTime ->
                eventExtraRepos.updateTag(event, dateTime, tag)
            }
        } else {
            eventExtraAction(policy, event, dateTime) { event, dateTime ->
                eventExtraRepos.save(event, dateTime, tag, comment)
            }
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
        )
    }

    private suspend fun eventExtraAction(
        policy: EventExtraPolicy,
        event: Event,
        dateTime: LocalDateTime,
        action: suspend (Event, LocalDateTime?) -> Unit
    ) {
        when (policy) {
            EventExtraPolicy.DEFAULT -> action(event, null)
            EventExtraPolicy.SYNCHRONIZED -> eventRepos.getByNameAndType(
                event.name,
                event.typeName,
                event.scheduleId
            ).forEach { event ->
                action(event, null)
            }

            EventExtraPolicy.BY_DATES -> action(event, dateTime)
        }
    }
}