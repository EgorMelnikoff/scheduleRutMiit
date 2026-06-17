package com.egormelnikoff.schedulerutmiit.core.database.mapper

import com.egormelnikoff.schedulerutmiit.core.common.domain.Event
import com.egormelnikoff.schedulerutmiit.core.common.domain.EventExtraData
import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Lecturer
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedSchedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.NamedScheduleWithSchedules
import com.egormelnikoff.schedulerutmiit.core.common.domain.Recurrence
import com.egormelnikoff.schedulerutmiit.core.common.domain.RecurrenceEvent
import com.egormelnikoff.schedulerutmiit.core.common.domain.Room
import com.egormelnikoff.schedulerutmiit.core.common.domain.Schedule
import com.egormelnikoff.schedulerutmiit.core.common.domain.ScheduleWithEvents
import com.egormelnikoff.schedulerutmiit.core.common.domain.Task
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.RecurrenceEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.RecurrenceEventEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.RoomEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskCompletionEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.TaskEntity
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.NamedScheduleWithSchedulesRelation
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.ScheduleWithEventsRelation
import com.egormelnikoff.schedulerutmiit.core.database.entity.relation.TaskWithCompletionsRelation

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
    recurrenceRule?.toDomain(),
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
    recurrenceRule?.toEntity(),
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
    id, eventId, scheduleId, eventName, dateTime, comment, tag
)

fun EventExtraData.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, dateTime, comment, tag
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
fun RecurrenceEntity.toDomain() = Recurrence(interval, currentNumber, firstWeekNumber)
fun Recurrence.toEntity() = RecurrenceEntity(interval, currentNumber, firstWeekNumber)

//RecurrenceEvent
fun RecurrenceEventEntity.toDomain() = RecurrenceEvent(frequency, interval)
fun RecurrenceEvent.toEntity() = RecurrenceEventEntity(frequency, interval)

//Task
