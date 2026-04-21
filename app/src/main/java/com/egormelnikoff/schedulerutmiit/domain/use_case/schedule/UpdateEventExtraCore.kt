package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.app.enums.EventExtraPolicy
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.local.preferences.PreferencesDataStore
import com.egormelnikoff.schedulerutmiit.domain.repos.EventExtraRepos
import com.egormelnikoff.schedulerutmiit.domain.repos.EventRepos
import kotlinx.coroutines.flow.first
import java.time.LocalDateTime
import javax.inject.Inject


class UpdateEventExtraCore @Inject constructor(
    private val preferencesDataStore: PreferencesDataStore,
    private val eventRepos: EventRepos,
    val eventExtraRepos: EventExtraRepos
) {
    suspend operator fun invoke(
        dateTime: LocalDateTime,
        scheduleEntity: ScheduleEntity,
        event: Event,
        shouldDelete: (EventExtraData?) -> Boolean,
        onUpdate: suspend (Event, LocalDateTime?) -> Unit,
        onCreate: suspend (Event, LocalDateTime?) -> Unit
    ): List<EventExtraData> {

        val policy = preferencesDataStore.eventExtraPolicyFlow.first()

        val eventExtraData = eventExtraRepos.get(
            event.id,
            if (policy == EventExtraPolicy.BY_DATES) dateTime else null
        )

        if (shouldDelete(eventExtraData)) {
            eventExtraAction(policy, event, dateTime) { e, dt ->
                eventExtraRepos.delete(e.id, dt)
            }
            return eventExtraRepos.getByScheduleId(scheduleEntity.id)
        }

        if (eventExtraData != null) {
            eventExtraAction(policy, event, dateTime, onUpdate)
        } else {
            eventExtraAction(policy, event, dateTime, onCreate)
        }

        return eventExtraRepos.getByScheduleId(scheduleEntity.id)
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