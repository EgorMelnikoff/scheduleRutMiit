package com.egormelnikoff.schedulerutmiit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.core.database.entity.EventExtraDataEntity
import java.time.LocalDateTime

@Dao
interface EventExtraDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(eventExtraData: EventExtraDataEntity)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(events: List<EventExtraDataEntity>)

    @Query("DELETE FROM EventsExtraData WHERE eventExtraScheduleId = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Long)

    @Query("DELETE FROM EventsExtraData WHERE eventId = :eventId")
    suspend fun deleteByEventId(eventId: Long)

    @Query("DELETE FROM EventsExtraData WHERE eventId = :eventId AND dateTime =:dateTime")
    suspend fun deleteByEventIdAndDateTime(eventId: Long, dateTime: LocalDateTime)

    @Query("SELECT * FROM EventsExtraData WHERE eventExtraScheduleId = :scheduleId")
    suspend fun getByScheduleId(scheduleId: Long): List<EventExtraDataEntity>

    @Query("SELECT * FROM EventsExtraData WHERE eventId = :eventId")
    suspend fun getByEventId(eventId: Long): EventExtraDataEntity?

    @Query("SELECT * FROM EventsExtraData WHERE eventId = :eventId AND dateTime = :dateTime")
    suspend fun getByEventIdAndDateTime(eventId: Long, dateTime: LocalDateTime): EventExtraDataEntity?


    @Query("UPDATE eventsextradata SET tag = :tag WHERE eventId = :eventId")
    suspend fun updateTagByEventId(
        eventId: Long,
        tag: Int
    )
    @Query("UPDATE eventsextradata SET tag = :tag WHERE eventId = :eventId AND dateTime = :dateTime")
    suspend fun updateTagByEventIdAndDateTime(
        eventId: Long,
        dateTime: LocalDateTime,
        tag: Int
    )

    @Query("UPDATE eventsextradata SET comment = :comment WHERE eventId = :eventId")
    suspend fun updateCommentByEventId(
        eventId: Long,
        comment: String
    )
    @Query("UPDATE eventsextradata SET comment = :comment WHERE eventId = :eventId AND dateTime = :dateTime")
    suspend fun updateCommentByEventIdAndDateTime(
        eventId: Long,
        dateTime: LocalDateTime,
        comment: String
    )
}