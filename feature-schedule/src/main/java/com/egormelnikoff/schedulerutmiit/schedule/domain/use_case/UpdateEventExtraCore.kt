package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.core.common.preferences.PreferencesDataSource
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.schedule.domain.repos.EventRepos
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateEventExtraCore @Inject constructor(
    private val preferencesDataSource: PreferencesDataSource,
    private val eventRepos: EventRepos,
    val eventExtraRepos: EventExtraRepos
) {
    suspend operator fun invoke(
        dateTime: LocalDateTime,
        scheduleId: Long,
        event: Event,
        shouldDelete: (EventExtraData?) -> Boolean,
        onUpdate: suspend (Event, LocalDateTime?) -> Unit,
        onCreate: suspend (Event, LocalDateTime?) -> Unit
    ): Map<Long, EventExtraData> {

        val policy = preferencesDataSource.eventExtraPolicyFlow.first()

        val eventExtraData = eventExtraRepos.get(
            event.id,
            if (policy == EventExtraPolicy.BY_DATES) dateTime else null
        )

        if (shouldDelete(eventExtraData)) {
            eventExtraAction(policy, event, dateTime) { e, dt ->
                eventExtraRepos.delete(e.id, dt)
            }
            return eventExtraRepos.getByScheduleId(scheduleId).associateBy { it.eventId }
        }

        if (eventExtraData != null) {
            eventExtraAction(policy, event, dateTime, onUpdate)
        } else {
            eventExtraAction(policy, event, dateTime, onCreate)
        }

        return eventExtraRepos.getByScheduleId(scheduleId).associateBy { it.eventId }
    }

    private suspend fun eventExtraAction(
        policy: EventExtraPolicy,
        event: Event,
        dateTime: LocalDateTime,
        action: suspend (Event, LocalDateTime?) -> Unit
    ) {
        when (policy) {
            EventExtraPolicy.DEFAULT -> action(event, null)
            EventExtraPolicy.BY_DATES -> action(event, dateTime)
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