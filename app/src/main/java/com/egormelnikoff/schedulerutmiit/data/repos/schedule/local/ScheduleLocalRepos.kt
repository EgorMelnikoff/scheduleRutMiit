package com.egormelnikoff.schedulerutmiit.data.repos.schedule.local

import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.enums.NamedScheduleType

interface ScheduleLocalRepos {
    suspend fun saveNamedSchedule(
        namedSchedule: NamedScheduleFormatted
    ): Long

    suspend fun saveSchedule(
        primaryKeyNamedSchedule: Long,
        scheduleFormatted: ScheduleFormatted
    )

    suspend fun insertEvent(
        event: EventEntity
    )

    suspend fun insertEventExtraData(
        event: EventEntity,
        tag: Int,
        comment: String
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
        event: EventEntity
    )



    suspend fun getSavedNamedSchedules(): List<NamedScheduleEntity>

    suspend fun getNamedScheduleById(
        primaryKeyNamedSchedule: Long
    ): NamedScheduleFormatted

    suspend fun getNamedScheduleByApiId(
        apiId: Int
    ): NamedScheduleFormatted?

    suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity?

    suspend fun getEventExtraByEventId(
        primaryKeyEvent: Long
    ): EventExtraData?

    suspend fun getCountEventsPerDate(
        date: String,
        scheduleId: Long
    ): Int



    suspend fun replaceScheduleEvents(
        oldScheduleId: Long,
        newScheduleFormatted: ScheduleFormatted
    )

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
        primaryKeyNamedSchedule: Long
    )

    suspend fun updateEvent(
        event: EventEntity
    )

    suspend fun updateEventIsHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    )

    suspend fun updateComment(
        primaryKeySchedule: Long,
        event: EventEntity,
        comment: String
    )
    suspend fun updateTag(
        primaryKeySchedule: Long,
        event: EventEntity,
        tag: Int
    )
}