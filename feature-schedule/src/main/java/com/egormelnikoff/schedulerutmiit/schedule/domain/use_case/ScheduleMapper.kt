package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.entity.Event
import com.egormelnikoff.schedulerutmiit.core.common.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.common.entity.relation.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.schedule.extension.toEntity
import javax.inject.Inject

class ScheduleMapper @Inject constructor() {
    operator fun invoke(
        schedule: ScheduleDto,
        namedScheduleId: Long,
        index: Int
    ): Schedule {
        val events = mutableListOf<Event>()
        schedule.periodic?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }
        schedule.nonPeriodic?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toEntity() }) }


        val scheduleEntity = ScheduleEntity(
            namedScheduleId = namedScheduleId,
            startDate = schedule.timetable.startDate,
            endDate = schedule.timetable.endDate,
            recurrence = schedule.periodic?.recurrence,
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