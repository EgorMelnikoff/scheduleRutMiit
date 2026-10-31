package com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.mapper

import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.EventExtraDataEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.GroupEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.LecturerEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.RecurrenceEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.RoomEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.EventExtraDataImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.EventImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.GroupImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.LecturerImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.NamedScheduleImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.RecurrenceImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.RoomImport
import com.egormelnikoff.schedulerutmiit.schedule_feature.data.local.export.model.ScheduleImport

//NamedSchedule
fun NamedScheduleEntity.toImport() = NamedScheduleImport(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

fun NamedScheduleImport.toEntity() = NamedScheduleEntity(
    id, fullName, shortName, apiId, type, isDefault, lastTimeUpdate
)

//Schedule
fun ScheduleEntity.toImport() = ScheduleImport(
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

fun ScheduleImport.toEntity(
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
fun EventEntity.toImport() = EventImport(
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

fun EventImport.toEntity(
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
fun EventExtraDataEntity.toImport() = EventExtraDataImport(
    id, eventId, scheduleId, eventName, date, comment, tag
)

fun EventExtraDataImport.toEntity(
    newScheduleId: Long? = null
) = EventExtraDataEntity(
    id, eventId, newScheduleId ?: scheduleId, eventName, date, comment, tag
)


//Group
fun GroupEntity.toImport() = GroupImport(id, name)
fun GroupImport.toEntity() = GroupEntity(id, name)

//Lecturer
fun LecturerEntity.toImport() = LecturerImport(id, shortFio, fullFio, hint)
fun LecturerImport.toEntity() = LecturerEntity(id, shortFio, fullFio, hint)

//Room
fun RoomEntity.toImport() = RoomImport(id, name, hint)
fun RoomImport.toEntity() = RoomEntity(id, name, hint)

//Recurrence
fun RecurrenceEntity.toImport() = RecurrenceImport(interval, firstWeekNumber)
fun RecurrenceImport.toEntity() = RecurrenceEntity(interval, firstWeekNumber)
