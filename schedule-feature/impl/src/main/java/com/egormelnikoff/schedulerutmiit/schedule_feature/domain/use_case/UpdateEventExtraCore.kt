package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.model.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.domain.repos.PreferencesRepos
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.EventDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.EventExtraDataSource
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventExtraCore @Inject constructor(
    private val preferencesRepos: PreferencesRepos,
    private val eventDataSource: EventDataSource,
    val eventExtraDataSource: EventExtraDataSource
) {
    suspend operator fun invoke(
        date: LocalDate?,
        scheduleId: Long,
        event: Event,
        shouldDelete: (EventExtraData?) -> Boolean,
        onUpdate: suspend (Event, LocalDate?) -> Unit,
        onCreate: suspend (Event, LocalDate?) -> Unit
    ): Map<Long, List<EventExtraData>> {

        val policy = preferencesRepos.eventExtraPolicyFlow.first()

        val eventExtraData = eventExtraDataSource.get(
            event.id,
            if (policy == EventExtraPolicy.BY_DATES) date else null
        )

        if (shouldDelete(eventExtraData)) {
            eventExtraAction(policy, event, date) { e, dt ->
                eventExtraDataSource.delete(e.id, dt)
            }
            return eventExtraDataSource.getByScheduleId(scheduleId).groupBy { it.eventId }
        }

        if (eventExtraData != null) {
            eventExtraAction(policy, event, date, onUpdate)
        } else {
            eventExtraAction(policy, event, date, onCreate)
        }

        return eventExtraDataSource.getByScheduleId(scheduleId).groupBy { it.eventId }
    }

    private suspend fun eventExtraAction(
        policy: EventExtraPolicy,
        event: Event,
        date: LocalDate?,
        action: suspend (Event, LocalDate?) -> Unit
    ) {
        when (policy) {
            EventExtraPolicy.DEFAULT -> action(event, null)
            EventExtraPolicy.BY_DATES -> action(event, date)
            EventExtraPolicy.SYNCHRONIZED -> {
                eventDataSource.getByNameAndType(
                    event.name,
                    event.typeName,
                    event.scheduleId
                ).forEach { action(it, null) }
            }
        }
    }
}