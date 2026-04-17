package com.egormelnikoff.schedulerutmiit.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.Event

@Dao
interface EventDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(event: Event)

    @Insert(onConflict = REPLACE)
    suspend fun insertAll(events: List<Event>)

    @Query("DELETE FROM Events WHERE eventScheduleId = :scheduleId")
    suspend fun deleteByScheduleId(scheduleId: Long)

    @Query("DELETE FROM Events WHERE EventId = :eventId")
    suspend fun deleteById(eventId: Long)

    @Query("SELECT COUNT(*) FROM Events WHERE eventScheduleId = :scheduleId AND SUBSTRING(startDatetime, 1, 10) = :date")
    suspend fun getCountPerDate(date: String, scheduleId: Long): Int

    @Query("SELECT * FROM Events WHERE name = :name AND typeName = :typeName AND eventScheduleId = :scheduleId")
    suspend fun getByNameAndType(
        name: String,
        typeName: String?,
        scheduleId: Long
    ): List<Event>

    @Query("UPDATE Events SET isHidden = :isHidden WHERE EventId = :eventId")
    suspend fun updateIsHidden(
        eventId: Long,
        isHidden: Boolean
    )
}