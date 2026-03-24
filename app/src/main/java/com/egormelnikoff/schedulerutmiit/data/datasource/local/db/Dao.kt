package com.egormelnikoff.schedulerutmiit.data.datasource.local.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.egormelnikoff.schedulerutmiit.app.entity.EventEntity
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery

@Dao
interface Dao {
    @Insert(onConflict = REPLACE)
    suspend fun insertNamedScheduleEntity(namedScheduleEntity: NamedScheduleEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertScheduleEntity(scheduleEntity: ScheduleEntity): Long

    @Insert(onConflict = REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertEventExtraData(eventExtraData: EventExtraData)

    @Insert(onConflict = REPLACE)
    suspend fun saveSearchQuery(searchQuery: SearchQuery)


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

    @Query("DELETE FROM SearchHistory WHERE id = :queryPrimaryKey")
    suspend fun deleteSearchQuery(queryPrimaryKey: Long)

    @Query("DELETE FROM SearchHistory")
    suspend fun deleteAllSearchQuery()



    @Query("SELECT * FROM SearchHistory ORDER BY id DESC")
    suspend fun getAllSearchQuery(): List<SearchQuery>

    @Query("SELECT * FROM SearchHistory WHERE apiId = :apiId")
    suspend fun getSearchQueryByApiId(apiId: Int): SearchQuery?

    @Query("SELECT * FROM NamedSchedules")
    suspend fun getAllNamedScheduleEntities(): List<NamedScheduleEntity>

    @Transaction
    @Query("SELECT * FROM NamedSchedules WHERE apiId = :apiId")
    suspend fun getNamedScheduleByApiId(apiId: Int): NamedScheduleFormatted?

    @Transaction
    @Query("SELECT * FROM NamedSchedules WHERE NamedScheduleId = :id")
    suspend fun getNamedScheduleById(id: Long): NamedScheduleFormatted

    @Query("SELECT * FROM NamedSchedules WHERE isDefaultNamedSchedule = 1")
    suspend fun getDefaultNamedScheduleEntity(): NamedScheduleEntity?

    @Query("SELECT * FROM EventsExtraData WHERE EventExtraId = :primaryKey")
    suspend fun getEventExtraByEventId(primaryKey: Long): EventExtraData?

    @Query("SELECT ScheduleId FROM Schedules WHERE NamedScheduleId = :primaryKey")
    suspend fun getSchedulesId(primaryKey: Long): List<Long>

    @Query("SELECT COUNT(*) FROM NamedSchedules")
    suspend fun getCount(): Int

    @Query("SELECT COUNT(*) FROM Events WHERE eventScheduleId = :scheduleId AND SUBSTRING(startDatetime, 1, 10) = :date")
    suspend fun getCountEventsPerDate(date: String, scheduleId: Long): Int

    @Query("SELECT * FROM Events WHERE name = :name AND typeName = :typeName AND eventScheduleId = :scheduleId")
    suspend fun getEventsByNameAndType(name: String, typeName: String?, scheduleId: Long): List<EventEntity>


    @Query("UPDATE eventsextradata SET tag = :tag WHERE eventExtraScheduleId = :schedulePrimaryKey AND EventExtraId = :eventPrimaryKey")
    suspend fun updateTagEvent(
        schedulePrimaryKey: Long,
        eventPrimaryKey: Long,
        tag: Int
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
        lastTimeUpdate: Long = System.currentTimeMillis()
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