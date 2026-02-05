package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import java.time.LocalDate
import javax.inject.Inject

class AddCustomEventUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos
) {
    companion object {
        const val MAX_EVENTS_COUNT = 10
    }

    suspend operator fun invoke(
        scheduleEntity: ScheduleEntity,
        event: Event
    ): OpenSavedScheduleResult? {
        if (isAddingAvailable(
                date = event.startDatetime!!.toLocalDate(),
                scheduleId = scheduleEntity.id
            )
        ) {
            return null
        }
        scheduleRepos.insertEvent(event)

        return OpenSavedScheduleResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = scheduleRepos.getNamedScheduleById(scheduleEntity.namedScheduleId)
        )
    }

    suspend fun isAddingAvailable(date: LocalDate, scheduleId: Long): Boolean {
        val eventsCount = scheduleRepos.getCountEventsPerDate(date.toString(), scheduleId)
        return eventsCount >= MAX_EVENTS_COUNT
    }
}