package com.egormelnikoff.schedulerutmiit.schedule_feature.domain.use_case

import com.egormelnikoff.schedulerutmiit.schedule_feature.api.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.EventExtraDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.ScheduleDataSource
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.widget.WidgetDataUpdater
import javax.inject.Inject

class SetDefaultScheduleUseCase @Inject constructor(
    private val scheduleDataSource: ScheduleDataSource,
    private val eventExtraDataSource: EventExtraDataSource,
    private val widgetDataUpdater: WidgetDataUpdater,
) {
    suspend operator fun invoke(
        currentNamedScheduleWithSchedules: NamedScheduleWithSchedules,
        scheduleId: Long,
        timetableId: String,
        isSaved: Boolean
    ): NamedScheduleWithSchedules? {
        if (isSaved) {
            scheduleDataSource.setDefault(
                namedScheduleId = currentNamedScheduleWithSchedules.namedSchedule.id,
                scheduleId = scheduleId
            )
            widgetDataUpdater.updateAll()

            return null
        }

        val updatedSchedules =
            currentNamedScheduleWithSchedules.schedulesWithEvents.map { schedule ->
                schedule.copy(
                    schedule = schedule.schedule.copy(
                        isDefault = schedule.schedule.timetableId == timetableId
                    ),
                    eventsExtraData = eventExtraDataSource.getByScheduleId(schedule.schedule.id)
                )
            }

        return currentNamedScheduleWithSchedules.copy(schedulesWithEvents = updatedSchedules)
    }
}