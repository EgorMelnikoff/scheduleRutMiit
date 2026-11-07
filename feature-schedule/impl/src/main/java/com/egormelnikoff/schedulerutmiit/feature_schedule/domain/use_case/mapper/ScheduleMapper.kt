package com.egormelnikoff.schedulerutmiit.feature_schedule.domain.use_case.mapper

import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleDto
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