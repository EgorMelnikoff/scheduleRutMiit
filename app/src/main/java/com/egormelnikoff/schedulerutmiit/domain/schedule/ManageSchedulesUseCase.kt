package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedSchedule
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.ScheduleUseCaseResult
import com.egormelnikoff.schedulerutmiit.repos.schedule.ScheduleRepos
import javax.inject.Inject


class ManageSchedulesUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedSchedule,
        scheduleId: Long,
        timetableId: String,
        isSaved: Boolean
    ): ScheduleUseCaseResult {
        if (isSaved) {
            scheduleRepos.setDefaultSchedule(
                namedScheduleId = currentNamedSchedule.namedScheduleEntity.id,
                scheduleId = scheduleId
            )
            widgetDataUpdater.updateAll()
        }

        val updatedSchedules = currentNamedSchedule.schedules.map { schedule ->
            schedule.copy(
                scheduleEntity = schedule.scheduleEntity.copy(
                    isDefault = schedule.scheduleEntity.timetableId == timetableId
                )
            )
        }

        return ScheduleUseCaseResult(
            savedNamedScheduleEntities = null,
            namedSchedule = currentNamedSchedule.copy(schedules = updatedSchedules)
        )
    }
}