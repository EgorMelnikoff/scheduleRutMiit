package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.Schedule
import com.egormelnikoff.schedulerutmiit.app.network.model.ScheduleModel
import javax.inject.Inject

class ScheduleMapper @Inject constructor() {
    operator fun invoke(
        schedule: ScheduleModel,
        namedScheduleId: Long,
        index: Int
    ): Schedule {
        val events = mutableListOf<Event>()
        schedule.periodicContent?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }
        schedule.nonPeriodicContent?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }


        val scheduleEntity = ScheduleEntity(
            namedScheduleId = namedScheduleId,
            startDate = schedule.timetable.startDate,
            endDate = schedule.timetable.endDate,
            recurrence = schedule.periodicContent?.recurrence,
            timetableType = schedule.timetable.type,
            downloadUrl = schedule.timetable.downloadUrl,
            timetableId = schedule.timetable.id,
            isDefault = index == 0
        )

        return Schedule(
            scheduleEntity = scheduleEntity,
            events = events
        )
    }
}