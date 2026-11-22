package com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.mapper

import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.RecurrenceEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.RoomEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.EventExtraDataImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.EventImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.GroupImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.LecturerImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.NamedScheduleImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.RecurrenceImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.RoomImport
import com.egormelnikoff.schedulerutmiit.feature_schedule.data.local.export.model.ScheduleImport

//NamedSchedule
internal fun NamedScheduleEntity.toImport() = NamedScheduleImport(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

internal fun NamedScheduleImport.toEntity() = NamedScheduleEntity(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

//Schedule
internal fun ScheduleEntity.toImport() = ScheduleImport(
    id,
    namedScheduleId,
    timetableId,
    timetableType,
    downloadUrl,
    startDate,
    endDate,
    recurrence?.toImport(),
    isDefault
)

internal fun ScheduleImport.toEntity(
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
internal fun EventEntity.toImport() = EventImport(
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
    lecturers?.map { it.toImport() },
    rooms?.map { it.toImport() },
    groups?.map { it.toImport() }
)

internal fun EventImport.toEntity(
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
internal fun EventExtraDataEntity.toImport() = EventExtraDataImport(
    id, eventId, scheduleId, eventName, date, comment, tag
)

internal fun EventExtraDataImport.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, date, comment, tag
)


//Group
internal fun GroupEntity.toImport() = GroupImport(id, name)
internal fun GroupImport.toEntity() = GroupEntity(id, name)

//Lecturer
internal fun LecturerEntity.toImport() = LecturerImport(id, shortFio, fullFio, hint)
internal fun LecturerImport.toEntity() = LecturerEntity(id, shortFio, fullFio, hint)

//Room
internal fun RoomEntity.toImport() = RoomImport(id, name, hint)
internal fun RoomImport.toEntity() = RoomEntity(id, name, hint)

//Recurrence
internal fun RecurrenceEntity.toImport() = RecurrenceImport(interval, firstWeekNumber)
internal fun RecurrenceImport.toEntity() = RecurrenceEntity(interval, firstWeekNumber)
