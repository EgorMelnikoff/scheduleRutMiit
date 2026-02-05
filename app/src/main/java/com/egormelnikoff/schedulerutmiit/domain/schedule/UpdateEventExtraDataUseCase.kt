package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import javax.inject.Inject

class UpdateEventExtraDataUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        event: Event,
        tag: Int,
        comment: String
    ): OpenSavedScheduleResult {
        if (comment == "" && tag == 0) {
            scheduleRepos.deleteEventExtraByEventId(event.id)
            return OpenSavedScheduleResult(
                savedNamedSchedules = null,
                namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
            )
        }

        val eventExtraData = scheduleRepos.getEventExtraByEventId(event.id)

        eventExtraData?.let {
            scheduleRepos.updateCommentEvent(primaryKeySchedule, event.id, comment)
            scheduleRepos.updateTagEvent(primaryKeySchedule, event.id, tag)
        } ?: scheduleRepos.insertEventExtraData(
            EventExtraData(
                id = event.id,
                scheduleId = primaryKeySchedule,
                eventName = event.name,
                eventStartDatetime = event.startDatetime,
                comment = comment,
                tag = tag
            )
        )

        return OpenSavedScheduleResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
        )
    }
}