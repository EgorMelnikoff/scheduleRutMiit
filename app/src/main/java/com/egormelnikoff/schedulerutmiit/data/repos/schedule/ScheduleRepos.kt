package com.egormelnikoff.schedulerutmiit.data.repos.schedule

import com.egormelnikoff.schedulerutmiit.app.entity.Event
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums_sealed.NamedScheduleType
import com.egormelnikoff.schedulerutmiit.app.model.Schedule
import com.egormelnikoff.schedulerutmiit.app.model.Timetables
import com.egormelnikoff.schedulerutmiit.data.Result

interface ScheduleRepos {
    suspend fun fetchTimetables(
        apiId: String,
        type: NamedScheduleType
    ): Result<Timetables>

    suspend fun fetchSchedule(
        apiId: String,
        type: NamedScheduleType,
        timetableId: String
    ): Result<Schedule>

    suspend fun fetchCurrentWeek(
        apiId: String,
        startDate: String,
        type: Char
    ): Int

    suspend fun insertNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long

    suspend fun insertSchedule(
        primaryKeySchedule: Long,
        scheduleFormatted: ScheduleFormatted
    )

    suspend fun insertEvent(
        event: Event
    )

    suspend fun insertEventExtraData(
        eventExtraData: EventExtraData
    )


    suspend fun getSavedNamedSchedules(): List<NamedScheduleEntity>

    suspend fun getNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted?

    suspend fun getNamedScheduleByApiId(
        apiId: String
    ): NamedScheduleFormatted?

    suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity?

    suspend fun getEventExtraByEventId(
        primaryKeyEvent: Long
    ): EventExtraData?

    suspend fun getCountEventsPerDate(
        date: String,
        scheduleId: Long
    ): Int


    suspend fun updatePrioritySavedNamedSchedules(
        primaryKeyNamedSchedule: Long
    )

    suspend fun updatePrioritySchedule(
        primaryKeyNamedSchedule: Long,
        primaryKeySchedule: Long
    )

    suspend fun updateNamedScheduleName(
        primaryKeyNamedSchedule: Long,
        type: NamedScheduleType,
        newName: String
    )

    suspend fun updateLastTimeUpdate(
        primaryKeyNamedSchedule: Long,
        lastTimeUpdate: Long
    )

    suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    )

    suspend fun updateCustomEvent(
        event: Event
    )

    suspend fun updateCommentEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        comment: String
    )
    suspend fun updateTagEvent(
        primaryKeySchedule: Long,
        primaryKeyEvent: Long,
        tag: Int
    )

    suspend fun deleteNamedScheduleById(
        primaryKeyNamedSchedule: Long,
        isDefault: Boolean
    )

    suspend fun deleteScheduleById(
        primaryKeySchedule: Long
    )

    suspend fun deleteEventById(
        primaryKeyEvent: Long
    )

    suspend fun deleteEventExtraByEventId(
        primaryKeyEvent: Long
    )
}