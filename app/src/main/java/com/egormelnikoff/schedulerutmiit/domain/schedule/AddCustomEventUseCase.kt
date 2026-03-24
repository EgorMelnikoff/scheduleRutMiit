package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.local.ScheduleLocalRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import java.time.LocalDate
import javax.inject.Inject

class AddCustomEventUseCase @Inject constructor(
    private val scheduleLocalRepos: ScheduleLocalRepos
) {
    companion object {
        const val MAX_EVENTS_COUNT = 10
    }

    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: EventEntity
    ): ScheduleUseCaseResult? {
        if (isAddingAvailable(
                date = event.startDatetime.toLocalDate(),
                scheduleId = scheduleEntity.id
            )
        ) {
            return null
        }
        scheduleLocalRepos.insertEvent(event)

        return ScheduleUseCaseResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = scheduleLocalRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)
        )
    }

    suspend fun isAddingAvailable(date: LocalDate, scheduleId: Long): Boolean {
        val eventsCount = scheduleLocalRepos.getCountEventsPerDate(date.toString(), scheduleId)
        return eventsCount >= MAX_EVENTS_COUNT
    }
}