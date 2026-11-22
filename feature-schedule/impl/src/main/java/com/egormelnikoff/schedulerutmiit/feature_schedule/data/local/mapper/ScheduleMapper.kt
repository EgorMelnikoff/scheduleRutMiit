package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.mapper

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.NamedScheduleWithSchedulesRelation
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.RecurrenceEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.RoomEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.ScheduleWithEventsRelation
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Event
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Group
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Lecturer
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedSchedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Recurrence
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Room
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.Schedule
import com.egormelnikoff.schedulerutmiit.feature_schedule.domain.model.ScheduleWithEvents

internal fun NamedScheduleWithSchedulesRelation.toDomain() = NamedScheduleWithSchedules(
    namedScheduleEntity.toDomain(), scheduleWithEvents.map { it.toDomain() }
)

internal fun ScheduleWithEventsRelation.toDomain() = ScheduleWithEvents(
    scheduleEntity.toDomain(),
    events.map { it.toDomain() },
    eventsExtraData.map { it.toDomain() }
)

//NamedSchedule
internal fun NamedScheduleEntity.toDomain() = NamedSchedule(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

internal fun NamedSchedule.toEntity() = NamedScheduleEntity(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

//Schedule
internal fun ScheduleEntity.toDomain() = Schedule(
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

internal fun Schedule.toEntity(
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
internal fun EventEntity.toDomain() = Event(
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

internal fun Event.toEntity(
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
internal fun EventExtraDataEntity.toDomain() = EventExtraData(
    id, eventId, scheduleId, eventName, date, comment, tag
)

internal fun EventExtraData.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, date, comment, tag
)


//Group
internal fun GroupEntity.toDomain() = Group(id, name)
internal fun Group.toEntity() = GroupEntity(id, name)

//Lecturer
internal fun LecturerEntity.toDomain() = Lecturer(id, shortFio, fullFio, hint)
internal fun Lecturer.toEntity() = LecturerEntity(id, shortFio, fullFio, hint)

//Room
internal fun RoomEntity.toDomain() = Room(id, name, hint)
internal fun Room.toEntity() = RoomEntity(id, name, hint)

//Recurrence
internal fun RecurrenceEntity.toDomain() = Recurrence(interval, firstWeekNumber)
internal fun Recurrence.toEntity() = RecurrenceEntity(interval, firstWeekNumber)