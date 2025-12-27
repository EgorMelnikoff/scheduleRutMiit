package com.egormelnikoff.schedulerutmiit.data.datasource.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.app.model.Event
import com.egormelnikoff.schedulerutmiit.app.model.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.model.ScheduleFormatted

@Dao
interface NamedScheduleDao {
    suspend fun insertNamedSchedule(namedScheduleFormatted: NamedScheduleFormatted): Long {
        val namedScheduleId = insertNamedScheduleEntity(namedScheduleFormatted.namedScheduleEntity)
        for (schedule in namedScheduleFormatted.schedules) {
            insertSchedule(
                namedScheduleId = namedScheduleId,
                scheduleFormatted = schedule
            )
        }
        return namedScheduleId
    }

    suspend fun insertSchedule(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted,
    ) {
        scheduleFormatted.scheduleEntity.namedScheduleId = namedScheduleId
        val scheduleId = insertScheduleEntity(scheduleFormatted.scheduleEntity)

        scheduleFormatted.eventsExtraData.forEach { eventExtraData ->
            eventExtraData.scheduleId = scheduleId
            insertEventExtraData(eventExtraData)
        }

        scheduleFormatted.events.forEach { event ->
            event.scheduleId = scheduleId
            insertEvent(event)
        }
    }
    @Insert(onConflict = REPLACE)
    suspend fun insertNamedScheduleEntity(namedScheduleEntity: NamedScheduleEntity): Long
    @Insert(onConflict = REPLACE)
    suspend fun insertScheduleEntity(scheduleEntity: ScheduleEntity): Long
    @Insert(onConflict = REPLACE)
    suspend fun insertEvent(event: Event)
    @Insert(onConflict = REPLACE)
    suspend fun insertEventExtraData(eventExtraData: EventExtraData)


    @Query("SELECT * FROM NamedSchedules")
    suspend fun getAll(): List<NamedScheduleEntity>
    @Query("SELECT * FROM NamedSchedules WHERE apiId = :apiId")
    suspend fun getNamedScheduleByApiId(apiId: Int): NamedScheduleFormatted?
    @Query("SELECT * FROM NamedSchedules WHERE NamedScheduleId = :id")
    suspend fun getNamedScheduleById(id: Long): NamedScheduleFormatted?
    @Query("SELECT * FROM NamedSchedules WHERE isDefaultNamedSchedule = 1")
    suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity?
    @Query("SELECT * FROM EventsExtraData WHERE EventExtraId = :primaryKey")
    suspend fun getEventExtraByEventId(primaryKey: Long): EventExtraData?
    @Query("SELECT ScheduleId FROM Schedules WHERE NamedScheduleId = :primaryKey")
    suspend fun getScheduleId(primaryKey: Long): List<Long>
    @Query("SELECT COUNT(*) FROM NamedSchedules")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM Events WHERE eventScheduleId = :scheduleId AND SUBSTRING(startDatetime, 1, 10) = :date")
    suspend fun getCountEventsPerDate(date: String, scheduleId: Long): Int

    suspend fun delete(primaryKey: Long) {
        deleteNamedScheduleById(primaryKey)
        val schedulesId = getScheduleId(primaryKey)
        deleteSchedulesByNamedScheduleId(primaryKey)
        for (scheduleId in schedulesId) {
            deleteEventsByScheduleId(scheduleId)
            deleteEventsExtraByScheduleId(scheduleId)
        }
    }
    @Query("DELETE FROM NamedSchedules WHERE NamedScheduleId = :primaryKey")
    suspend fun deleteNamedScheduleById(primaryKey: Long)
    @Query("DELETE FROM Schedules WHERE namedScheduleId = :id")
    suspend fun deleteSchedulesByNamedScheduleId(id: Long)
    @Query("DELETE FROM Schedules WHERE ScheduleId = :id")
    suspend fun deleteScheduleById(id: Long)
    @Query("DELETE FROM Events WHERE eventScheduleId = :id")
    suspend fun deleteEventsByScheduleId(id: Long)
    @Query("DELETE FROM Events WHERE EventId = :id")
    suspend fun deleteEventById(id: Long)
    @Query("DELETE FROM EventsExtraData WHERE eventExtraScheduleId = :id")
    suspend fun deleteEventsExtraByScheduleId(id: Long)
    @Query("DELETE FROM EventsExtraData WHERE EventExtraId = :id")
    suspend fun deleteEventExtraByEventId(id: Long)


    @Query("UPDATE eventsextradata SET tag = :priority WHERE eventExtraScheduleId = :schedulePrimaryKey AND EventExtraId = :eventPrimaryKey")
    suspend fun updateTagEvent(
        schedulePrimaryKey: Long,
        eventPrimaryKey: Long,
        priority: Int
    )
    @Query("UPDATE eventsextradata SET comment = :comment WHERE eventExtraScheduleId = :schedulePrimaryKey AND EventExtraId = :eventPrimaryKey")
    suspend fun updateCommentEvent(
        schedulePrimaryKey: Long,
        eventPrimaryKey: Long,
        comment: String
    )

    @Query("UPDATE Events SET isHidden = :isHidden WHERE EventId = :eventPrimaryKey")
    suspend fun updateEventHidden(
        eventPrimaryKey: Long,
        isHidden: Boolean
    )

    @Query("UPDATE namedschedules SET lastTimeUpdate = :lastTimeUpdate WHERE NamedScheduleId = :primaryKeyNamedSchedule")
    suspend fun updateLastTimeUpdate(
        primaryKeyNamedSchedule: Long,
        lastTimeUpdate: Long
    )

    @Query("UPDATE namedschedules SET fullName = :fullName, shortName = :shortName WHERE NamedScheduleId = :primaryKeyNamedSchedule")
    suspend fun updateName(
        primaryKeyNamedSchedule: Long,
        fullName: String,
        shortName: String
    )

    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 1 WHERE NamedScheduleId = :primaryKey")
    suspend fun setDefaultNamedSchedule(primaryKey: Long)
    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 0 WHERE NamedScheduleId != :primaryKey")
    suspend fun setNonDefaultNamedSchedule(primaryKey: Long)


    @Query("UPDATE Schedules SET isDefaultSchedule = 1 WHERE ScheduleId = :primaryKey")
    suspend fun setDefaultSchedule(primaryKey: Long)
    @Query("UPDATE Schedules SET isDefaultSchedule = 0 WHERE ScheduleId != :primaryKeySchedule AND namedScheduleId = :primaryKeyNamedSchedule")
    suspend fun setNonDefaultSchedule(primaryKeySchedule: Long, primaryKeyNamedSchedule: Long)
}