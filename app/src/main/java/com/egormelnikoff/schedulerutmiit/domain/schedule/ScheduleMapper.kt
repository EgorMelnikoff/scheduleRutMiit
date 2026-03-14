package com.egormelnikoff.schedulerutmiit.domain.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import javax.inject.Inject

class ScheduleMapper @Inject constructor() {
    operator fun invoke(
        schedule: Schedule,
        primaryKeyNamedSchedule: Long,
        index: Int
    ): ScheduleFormatted {
        val events = mutableListOf<EventEntity>()
        schedule.periodicContent?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }
        schedule.nonPeriodicContent?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }


        val scheduleEntity = ScheduleEntity(
            namedScheduleId = primaryKeyNamedSchedule,
            startDate = schedule.timetable.startDate,
            endDate = schedule.timetable.endDate,
            recurrence = schedule.periodicContent?.recurrence,
            timetableType = schedule.timetable.type,
            downloadUrl = schedule.timetable.downloadUrl,
            timetableId = schedule.timetable.id,
            isDefault = index == 0
        )

        return ScheduleFormatted(
            scheduleEntity = scheduleEntity,
            events = events
        )
    }
}