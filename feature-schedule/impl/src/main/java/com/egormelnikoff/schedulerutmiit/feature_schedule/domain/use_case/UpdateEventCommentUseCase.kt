package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import java.time.LocalDate
import javax.inject.Inject

class UpdateEventCommentUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        date: LocalDate?,
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
                core.eventExtraDataSource.updateComment(e, d, comment)
            },

            onCreate = { e, d ->
                core.eventExtraDataSource.save(
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