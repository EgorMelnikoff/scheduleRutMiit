package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import javax.inject.Inject

class UpdateEventExtraDataUseCase @Inject constructor(
    private val scheduleLocalRepos: ScheduleLocalRepos
) {
    suspend operator fun invoke(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long,
        event: EventEntity,
        tag: Int,
        comment: String
    ): ScheduleUseCaseResult {
        if (comment == "" && tag == 0) {
            scheduleLocalRepos.deleteEventExtraByEventId(event)
            return ScheduleUseCaseResult(
                savedNamedSchedules = null,
                namedScheduleFormatted = scheduleLocalRepos.getNamedScheduleById(primaryKeyNamedSchedule)
            )
        }

        val eventExtraData = scheduleLocalRepos.getEventExtraByEventId(event.id)

        if (eventExtraData != null) {
            scheduleLocalRepos.updateComment(primaryKeySchedule, event, comment)
            scheduleLocalRepos.updateTag(primaryKeySchedule, event, tag)
        } else {
            scheduleLocalRepos.insertEventExtraData(
                 event, tag, comment
            )
        }

        return ScheduleUseCaseResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = scheduleLocalRepos.getNamedScheduleById(primaryKeyNamedSchedule)
        )
    }
}