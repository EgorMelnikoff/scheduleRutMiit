package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventExtraCore @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val eventRepos: EventRepos,
    val eventExtraRepos: EventExtraRepos
) {
    suspend operator fun invoke(
        date: LocalDate?,
        scheduleId: Long,
        event: Event,
        shouldDelete: (EventExtraData?) -> Boolean,
        onUpdate: suspend (Event, LocalDate?) -> Unit,
        onCreate: suspend (Event, LocalDate?) -> Unit
    ): Map<Long, List<EventExtraData>> {

        val policy = preferencesDataSource.eventExtraPolicyFlow.first()

        val eventExtraData = eventExtraRepos.get(
            event.id,
            if (policy == EventExtraPolicy.BY_DATES) date else null
        )

        if (shouldDelete(eventExtraData)) {
            eventExtraAction(policy, event, date) { e, dt ->
                eventExtraRepos.delete(e.id, dt)
            }
            return eventExtraRepos.getByScheduleId(scheduleId).groupBy { it.eventId }
        }

        if (eventExtraData != null) {
            eventExtraAction(policy, event, date, onUpdate)
        } else {
            eventExtraAction(policy, event, date, onCreate)
        }

        return eventExtraRepos.getByScheduleId(scheduleId).groupBy { it.eventId }
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
                eventRepos.getByNameAndType(
                    event.name,
                    event.typeName,
                    event.scheduleId
                ).forEach { action(it, null) }
            }
        }
    }
}