package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.event.EventRepos
import com.egormelnikoff.schedulerutmiit.repos.named_schedule.NamedScheduleRepos
import java.time.LocalDate
import javax.inject.Inject

class AddCustomEventUseCase @Inject constructor(
    private val namedScheduleRepos: NamedScheduleRepos,
    private val eventRepos: EventRepos,
) {
    companion object {
        const val MAX_EVENTS_COUNT = 10
    }

    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: Event
    ): ScheduleUseCaseResult? {
        if (isAddingAvailable(
                date = event.startDatetime.toLocalDate(),
                scheduleId = scheduleEntity.id
            )
        ) {
            return null
        }
        eventRepos.save(event)

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = namedScheduleRepos.getById(scheduleEntity.namedScheduleId)
        )
    }

    suspend fun isAddingAvailable(date: LocalDate, scheduleId: Long): Boolean {
        val eventsCount = eventRepos.getCountPerDate(date.toString(), scheduleId)
        return eventsCount >= MAX_EVENTS_COUNT
    }
}