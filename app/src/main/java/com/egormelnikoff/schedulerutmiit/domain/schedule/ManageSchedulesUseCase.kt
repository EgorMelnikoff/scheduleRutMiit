package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.widget.WidgetDataUpdater
import com.egormelnikoff.schedulerutmiit.data.repos.schedule.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.domain.schedule.result.OpenSavedScheduleResult
import javax.inject.Inject


class ManageSchedulesUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedScheduleFormatted,
        primaryKeySchedule: Long,
        timetableId: String,
        isSaved: Boolean
    ): OpenSavedScheduleResult {
        if (isSaved) {
            scheduleRepos.updatePrioritySchedule(
                primaryKeyNamedSchedule = currentNamedSchedule.namedScheduleEntity.id,
                primaryKeySchedule = primaryKeySchedule
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
        return OpenSavedScheduleResult(
            savedNamedSchedules = null,
            namedScheduleFormatted = currentNamedSchedule.copy(schedules = updatedSchedules)
        )
    }
}