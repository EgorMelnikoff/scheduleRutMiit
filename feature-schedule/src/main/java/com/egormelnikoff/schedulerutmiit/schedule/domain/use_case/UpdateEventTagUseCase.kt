package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateEventTagUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        dateTime: LocalDateTime,
        schedule: Schedule,
        event: Event,
        tag: Int
    ): List<EventExtraData> {
        return core(
            dateTime = dateTime,
            schedule = schedule,
            event = event,

            shouldDelete = { data ->
                tag == 0 && data?.comment == ""
            },

            onUpdate = { e, dt ->
                core.eventExtraRepos.updateTag(e, dt, tag)
            },

            onCreate = { e, dt ->
                core.eventExtraRepos.save(
                    EventExtraData(
                        scheduleId = e.scheduleId,
                        eventId = e.id,
                        eventName = e.name,
                        dateTime = dt ?: e.startDatetime,
                        tag = tag
                    )
                )
            }
        )
    }
}