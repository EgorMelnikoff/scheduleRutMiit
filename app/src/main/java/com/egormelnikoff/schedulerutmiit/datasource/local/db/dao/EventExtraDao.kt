package com.egormelnikoff.schedulerutmiit.datasource.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.app.entity.EventExtraData

@Dao
interface EventExtraDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(eventExtraData: EventExtraData)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(events: List<EventExtraData>)

    @Query("DELETE FROM EventsExtraData WHERE eventExtraScheduleId = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Long)

    @Query("DELETE FROM EventsExtraData WHERE EventExtraId = :eventId")
    suspend fun deleteByEventId(eventId: Long)

    @Query("SELECT * FROM EventsExtraData WHERE EventExtraId = :eventId")
    suspend fun getByEventId(eventId: Long): EventExtraData?

    @Query("UPDATE eventsextradata SET tag = :tag WHERE eventExtraScheduleId = :scheduleId AND EventExtraId = :eventId")
    suspend fun updateTag(
        scheduleId: Long,
        eventId: Long,
        tag: Int
    )

    @Query("UPDATE eventsextradata SET comment = :comment WHERE eventExtraScheduleId = :scheduleId AND EventExtraId = :eventId")
    suspend fun updateComment(
        scheduleId: Long,
        eventId: Long,
        comment: String
    )
}