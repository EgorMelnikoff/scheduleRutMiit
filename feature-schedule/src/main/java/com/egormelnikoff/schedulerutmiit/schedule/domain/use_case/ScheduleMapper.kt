package com.egormelnikoff.schedulerutmiit.schedule.domain.use_case

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.ScheduleDto
import com.egormelnikoff.schedulerutmiit.core.network.mapper.toDomain
import javax.inject.Inject

class ScheduleMapper @Inject constructor() {
    operator fun invoke(
        scheduleDto: ScheduleDto,
        namedScheduleId: Long,
        index: Int
    ): ScheduleWithEvents {
        val events = mutableListOf<Event>()
        scheduleDto.periodic?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toDomain() }) }
        scheduleDto.nonPeriodic?.events
            ?.filter { it.startDatetime != null && it.endDatetime != null && it.name != null }
            ?.let { events.addAll(it.map { e -> e.toDomain() }) }


        val schedule = Schedule(
            namedScheduleId = namedScheduleId,
            startDate = scheduleDto.timetable.startDate,
            endDate = scheduleDto.timetable.endDate,
            recurrence = scheduleDto.periodic?.recurrence?.toDomain(scheduleDto.timetable.startDate),
            timetableType = scheduleDto.timetable.type,
            downloadUrl = scheduleDto.timetable.downloadUrl,
            timetableId = scheduleDto.timetable.id,
            isDefault = index == 0
        )

        return ScheduleWithEvents(
            schedule = schedule,
            events = events
        )
    }
}