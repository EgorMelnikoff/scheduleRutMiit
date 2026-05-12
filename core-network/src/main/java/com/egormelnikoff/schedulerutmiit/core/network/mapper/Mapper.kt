package com.egormelnikoff.schedulerutmiit.core.network.mapper

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.LatestRelease
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.RecurrenceEvent
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.network.dto.latest_release.LatestReleaseFetchDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.EventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.GroupDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.LecturerDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RecurrenceEventDto
import com.egormelnikoff.schedulerutmiit.core.network.dto.schedule.RoomDto

//LatestRelease
fun LatestReleaseFetchDto.toDomain() = LatestRelease(url, name, tag)

//Event
fun EventDto.toDomain() = Event(
    startDatetime = requireNotNull(this.startDatetime),
    endDatetime = requireNotNull(this.endDatetime),
    recurrenceRule = recurrence?.toDomain(),
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
fun RecurrenceDto.toDomain() = Recurrence(interval, currentNumber, firstWeekNumber)

//RecurrenceEvent
fun RecurrenceEventDto.toDomain() = RecurrenceEvent(frequency, interval)