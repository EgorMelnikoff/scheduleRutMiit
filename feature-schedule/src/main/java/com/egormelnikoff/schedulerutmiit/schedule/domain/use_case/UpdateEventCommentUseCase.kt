package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventCommentUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        dateTime: LocalDate,
        scheduleId: Long,
        event: Event,
        comment: String
    ): Map<Long, EventExtraData> {
        return core(
            dateTime = dateTime.atTime(event.startDatetime.toLocalTime()),
            scheduleId = scheduleId,
            event = event,

            shouldDelete = { data ->
                comment == "" && data?.tag == 0
            },

            onUpdate = { e, dt ->
                core.eventExtraRepos.updateComment(e, dt, comment)
            },

            onCreate = { e, dt ->
                core.eventExtraRepos.save(
                    EventExtraData(
                        scheduleId = e.scheduleId,
                        eventId = e.id,
                        eventName = e.name,
                        dateTime = dt ?: e.startDatetime,
                        comment = comment
                    )
                )
            }
        )
    }
}