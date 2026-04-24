package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.entity.Event
import com.egormelnikoff.schedulerutmiit.core.common.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.entity.ScheduleEntity
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateEventCommentUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        dateTime: LocalDateTime,
        scheduleEntity: ScheduleEntity,
        event: Event,
        comment: String
    ): List<EventExtraData> {
        return core(
            dateTime = dateTime,
            scheduleEntity = scheduleEntity,
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