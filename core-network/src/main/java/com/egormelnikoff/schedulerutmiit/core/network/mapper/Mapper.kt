package com.egormelnikoff.schedulerutmiit.core.network.mapper

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.extension.getFirstDayOfWeek
import com.egormelnikoff.schedulerutmiit.core.network.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.EventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.LecturerDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RoomDto
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.abs

//LatestRelease
fun LatestReleaseFetchDto.toDomain() = LatestRelease(url, name, tag)

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
    rooms = rooms?.map { it.toDomain() }
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