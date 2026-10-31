package com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.mapper

import com.egormelnikoff.schedulerutmiit.core.common.utils.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Group
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Room
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.EventDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.GroupDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.LecturerDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.remote.model.RoomDto
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

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