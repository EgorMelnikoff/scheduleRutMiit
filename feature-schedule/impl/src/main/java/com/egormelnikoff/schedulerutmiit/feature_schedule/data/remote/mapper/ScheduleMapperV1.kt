package com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.EventDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.GroupDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.LecturerDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.RoomDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.remote.model.ScheduleDto
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Group
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.math.abs

class ScheduleMapperV1 @Inject constructor() {
    fun map(
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

//Event
fun EventDto.toDomain() = Event(
    startDatetime = requireNotNull(this.startDatetime),
    endDatetime = requireNotNull(this.endDatetime),
    interval = recurrence?.interval,
    periodNumber = periodNumber,
    name = requireNotNull(this.name),
    typeName = typeName,
    timeSlotName = timeSlotName,
    lecturers = lecturers?.map { it.toDomain() },
    groups = groups?.map { it.toDomain() },
    rooms = rooms?.map { it.toDomain() },
    isHidden = false,
    isCustomEvent = false
)

//Group
fun GroupDto.toDomain() = Group(id, name)

//Lecturer
fun LecturerDto.toDomain() = Lecturer(id, shortFio, fullFio, hint)

//Room
fun RoomDto.toDomain() = Room(id, name, hint)

//Recurrence
fun RecurrenceDto.toDomain(
    startDate: LocalDate
): Recurrence {
    val today = LocalDate.now()
    return if (today > startDate && interval > 1) {
        val currentWeekIndex = abs(
            ChronoUnit.WEEKS.between(
                startDate.getFirstDayOfWeek(),
                today.getFirstDayOfWeek()
            )
        ).plus(1)

        val firstWeekNumber =
            ((currentWeekIndex + currentNumber) % interval)
                .plus(1)

        Recurrence(
            interval,
            firstWeekNumber = firstWeekNumber.toInt()
        )
    } else {
        Recurrence(
            interval,
            firstWeekNumber = currentNumber
        )
    }
}