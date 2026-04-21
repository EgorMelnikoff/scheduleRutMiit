package com.egormelnikoff.schedulerutmiit.domain.use_case.schedule

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.relation.NamedSchedule
import com.egormelnikoff.schedulerutmiit.domain.repos.ScheduleRepos
import com.egormelnikoff.schedulerutmiit.ui.widget.WidgetDataUpdater
import javax.inject.Inject


class SetDefaultScheduleUseCase @Inject constructor(
    private val scheduleRepos: ScheduleRepos,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        currentNamedSchedule: NamedSchedule,
        scheduleId: Long,
        timetableId: String,
        isSaved: Boolean
    ): NamedSchedule {
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

        return currentNamedSchedule.copy(schedules = updatedSchedules)
    }
}