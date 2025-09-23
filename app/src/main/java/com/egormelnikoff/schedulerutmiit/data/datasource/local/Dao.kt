package com.egormelnikoff.schedulerutmiit.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.egormelnikoff.schedulerutmiit.data.entity.Event
import com.egormelnikoff.schedulerutmiit.data.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.data.entity.ScheduleFormatted

@Dao
interface NamedScheduleDao {
    @Insert
    suspend fun insertNamedSchedule(namedScheduleFormatted: NamedScheduleFormatted) {
        val namedScheduleId = insertNamedScheduleEntity(namedScheduleFormatted.namedScheduleEntity)
        for (schedule in namedScheduleFormatted.schedules) {
            insertScheduleWithEvents(
                namedScheduleId = namedScheduleId,
                scheduleFormatted = schedule
            )
        }
    }
    @Insert
    suspend fun insertScheduleWithEvents(
        namedScheduleId: Long,
        scheduleFormatted: ScheduleFormatted,
    ) {
        scheduleFormatted.scheduleEntity.namedScheduleId = namedScheduleId
        val scheduleId = insertSchedule(scheduleFormatted.scheduleEntity)

        scheduleFormatted.eventsExtraData.forEach { eventExtraData ->
            eventExtraData.scheduleId = scheduleId
            insertEventExtraData(eventExtraData)
        }

        scheduleFormatted.events.forEach { event ->
            event.scheduleId = scheduleId
            insertEvent(event)
        }
    }
    @Insert
    suspend fun insertNamedScheduleEntity(namedScheduleEntity: NamedScheduleEntity): Long
    @Insert
    suspend fun insertSchedule(scheduleEntity: ScheduleEntity): Long
    @Insert
    suspend fun insertEvent(event: Event)
    @Insert
    suspend fun insertEventExtraData(eventExtraData: EventExtraData)


    @Transaction
    @Query("SELECT * FROM NamedSchedules")
    suspend fun getAll(): List<NamedScheduleEntity>

    @Transaction
    @Query("SELECT * FROM NamedSchedules WHERE apiId = :apiId")
    suspend fun getNamedScheduleByApiId(apiId: Int): NamedScheduleFormatted?
    @Query("SELECT * FROM NamedSchedules WHERE NamedScheduleId = :id")
    suspend fun getNamedScheduleById(id: Long): NamedScheduleFormatted?
    @Query("SELECT * FROM EventsExtraData WHERE EventExtraId = :primaryKey")
    suspend fun getEventExtraByEventId(primaryKey: Long): EventExtraData?
    @Query("SELECT ScheduleId FROM Schedules WHERE NamedScheduleId = :primaryKey")
    suspend fun getScheduleId(primaryKey: Long): List<Long>
    @Query("SELECT COUNT(*) FROM NamedSchedules")
    suspend fun getCount(): Int


    @Transaction
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
    @Query("DELETE FROM EventsExtraData WHERE eventExtraScheduleId = :id")
    suspend fun deleteEventsExtraByScheduleId(id: Long)
    @Query("DELETE FROM EventsExtraData WHERE EventExtraId = :id")
    suspend fun deleteEventsExtraByEventId(id: Long)


    @Query("UPDATE eventsextradata SET tag = :priority WHERE eventExtraScheduleId = :scheduleId AND EventExtraId = :eventId")
    suspend fun updateTagEvent(
        scheduleId: Long,
        eventId: Long,
        priority: Int
    )
    @Query("UPDATE eventsextradata SET comment = :comment WHERE eventExtraScheduleId = :scheduleId AND EventExtraId = :eventId")
    suspend fun updateCommentEvent(
        scheduleId: Long,
        eventId: Long,
        comment: String
    )

    @Query("UPDATE namedschedules SET lastTimeUpdate = :lastTimeUpdate WHERE NamedScheduleId = :namedScheduleId")
    suspend fun updateLastTimeUpdate(
        namedScheduleId: Long,
        lastTimeUpdate: Long
    )

    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 1 WHERE NamedScheduleId = :primaryKey")
    suspend fun setDefaultNamedSchedule(primaryKey: Long)
    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 0 WHERE NamedScheduleId != :primaryKey")
    suspend fun setNonDefaultNamedSchedule(primaryKey: Long)


    @Query("UPDATE Schedules SET isDefaultSchedule = 1 WHERE ScheduleId = :primaryKey")
    suspend fun setDefaultSchedule(primaryKey: Long)
    @Query("UPDATE Schedules SET isDefaultSchedule = 0 WHERE ScheduleId != :primaryKeySchedule AND namedScheduleId = :namedScheduleId")
    suspend fun setNonDefaultSchedule(primaryKeySchedule: Long, namedScheduleId: Long)
}