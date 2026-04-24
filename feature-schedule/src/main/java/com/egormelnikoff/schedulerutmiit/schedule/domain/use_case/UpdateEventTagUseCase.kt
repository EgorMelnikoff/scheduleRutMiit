package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.entity.Event
import com.egormelnikoff.schedulerutmiit.core.common.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.entity.ScheduleEntity
import java.time.LocalDateTime
import javax.inject.Inject

class UpdateEventTagUseCase @Inject constructor(
    private val core: UpdateEventExtraCore
) {
    suspend operator fun invoke(
        dateTime: LocalDateTime,
        scheduleEntity: ScheduleEntity,
        event: Event,
        tag: Int
    ): List<EventExtraData> {
        return core(
            dateTime = dateTime,
            scheduleEntity = scheduleEntity,
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