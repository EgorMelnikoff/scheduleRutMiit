package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventTagUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        date: LocalDate?,
        scheduleId: Long,
        event: Event,
        tag: Int
    ): Map<Long, List<EventExtraData>> {
        return core(
            date = date,
            scheduleId = scheduleId,
            event = event,

            shouldDelete = { data ->
                tag == 0 && data?.comment == ""
            },

            onUpdate = { e, d ->
                core.eventExtraRepos.updateTag(e, d, tag)
            },

            onCreate = { e, d ->
                core.eventExtraRepos.save(
                    EventExtraData(
                        scheduleId = e.scheduleId,
                        eventId = e.id,
                        eventName = e.name,
                        date = d ?: e.startDatetime.toLocalDate(),
                        tag = tag
                    )
                )
            }
        )
    }
}