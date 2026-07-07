package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventCommentUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        date: LocalDate,
        scheduleId: Long,
        event: Event,
        comment: String
    ): Map<Long, List<EventExtraData>> {
        return core(
            date = date,
            scheduleId = scheduleId,
            event = event,

            shouldDelete = { data ->
                comment == "" && data?.tag == 0
            },

            onUpdate = { e, d ->
                core.eventExtraRepos.updateComment(e, d, comment)
            },

            onCreate = { e, d ->
                core.eventExtraRepos.save(
                    EventExtraData(
                        scheduleId = e.scheduleId,
                        eventId = e.id,
                        eventName = e.name,
                        date = d ?: e.startDatetime.toLocalDate(),
                        comment = comment
                    )
                )
            }
        )
    }
}