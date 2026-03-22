package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class UpdateEventExtraDataUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        event: EventEntity,
        tag: Int,
        comment: String
    ): ScheduleUseCaseResult {
        if (comment == "" && tag == 0) {
            scheduleRepos.deleteEventExtraByEventId(event.id)
            return ScheduleUseCaseResult(
                savedNamedSchedules = null,
                namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
            )
        }

        val eventExtraData = scheduleRepos.getEventExtraByEventId(event.id)

        if (eventExtraData != null) {
            scheduleRepos.updateCommentEvent(primaryKeySchedule, event, comment)
            scheduleRepos.updateTagEvent(primaryKeySchedule, event, tag)
        } else {
            scheduleRepos.updateEventExtraData(
                 event, tag, comment
            )
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(primaryKeyNamedSchedule)
        )
    }
}