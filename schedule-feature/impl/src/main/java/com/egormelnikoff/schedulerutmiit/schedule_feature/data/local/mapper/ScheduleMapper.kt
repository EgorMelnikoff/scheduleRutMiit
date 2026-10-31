package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper

import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Event
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Group
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Room
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.schedule_feature.domain.model.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.NamedScheduleWithSchedulesRelation
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.RecurrenceEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.RoomEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.ScheduleWithEventsRelation

fun NamedScheduleWithSchedulesRelation.toDomain() = NamedScheduleWithSchedules(
    namedScheduleEntity.toDomain(), scheduleWithEvents.map { it.toDomain() }
)

fun ScheduleWithEventsRelation.toDomain() = ScheduleWithEvents(
    scheduleEntity.toDomain(),
    events.map { it.toDomain() },
    eventsExtraData.map { it.toDomain() }
)

//NamedSchedule
fun NamedScheduleEntity.toDomain() = NamedSchedule(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

fun NamedSchedule.toEntity() = NamedScheduleEntity(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

//Schedule
fun ScheduleEntity.toDomain() = Schedule(
    id,
    namedScheduleId,
    timetableId,
    timetableType,
    downloadUrl,
    startDate,
    endDate,
    recurrence?.toDomain(),
    isDefault
)

fun Schedule.toEntity(
    newNamedScheduleId: Long? = null
) = ScheduleEntity(
    id,
    newNamedScheduleId ?: namedScheduleId,
    timetableId,
    timetableType,
    downloadUrl,
    startDate,
    endDate,
    recurrence?.toEntity(),
    isDefault
)

//Event
fun EventEntity.toDomain() = Event(
    id,
    scheduleId,
    isHidden,
    isCustomEvent,
    startDatetime,
    endDatetime,
    interval,
    periodNumber,
    name,
    typeName,
    timeSlotName,
    lecturers?.map { it.toDomain() },
    rooms?.map { it.toDomain() },
    groups?.map { it.toDomain() }
)

fun Event.toEntity(
    newScheduleId: Long? = null
) = EventEntity(
    id,
    newScheduleId ?: scheduleId,
    isHidden,
    isCustomEvent,
    startDatetime,
    endDatetime,
    interval,
    periodNumber,
    name,
    typeName,
    timeSlotName,
    lecturers?.map { it.toEntity() },
    rooms?.map { it.toEntity() },
    groups?.map { it.toEntity() }
)

//EventExtra
fun EventExtraDataEntity.toDomain() = EventExtraData(
    id, eventId, scheduleId, eventName, date, comment, tag
)

fun EventExtraData.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, date, comment, tag
)


//Group
fun GroupEntity.toDomain() = Group(id, name)
fun Group.toEntity() = GroupEntity(id, name)

//Lecturer
fun LecturerEntity.toDomain() = Lecturer(id, shortFio, fullFio, hint)
fun Lecturer.toEntity() = LecturerEntity(id, shortFio, fullFio, hint)

//Room
fun RoomEntity.toDomain() = Room(id, name, hint)
fun Room.toEntity() = RoomEntity(id, name, hint)

//Recurrence
fun RecurrenceEntity.toDomain() = Recurrence(interval, firstWeekNumber)
fun Recurrence.toEntity() = RecurrenceEntity(interval, firstWeekNumber)