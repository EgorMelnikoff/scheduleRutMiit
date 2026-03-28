package com.egormelnikoff.schedulerutmiit.datasource.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.app.entity.ScheduleEntity

@Dao
interface ScheduleDao {
    @Insert(onConflict = REPLACE)
    suspend fun insertAll(schedules: List<ScheduleEntity>): List<Long>

    @Insert(onConflict = REPLACE)
    suspend fun insert(scheduleEntity: ScheduleEntity): Long

    @Query("DELETE FROM Schedules WHERE namedScheduleId = :namedScheduleId")
    suspend fun deleteByNamedScheduleId(namedScheduleId: Long)

    @Query("DELETE FROM Schedules WHERE ScheduleId = :scheduleId")
    suspend fun deleteById(scheduleId: Long)

    @Query("SELECT ScheduleId FROM Schedules WHERE NamedScheduleId = :namedScheduleId")
    suspend fun getIds(namedScheduleId: Long): List<Long>

    @Query("UPDATE Schedules SET isDefaultSchedule = 1 WHERE ScheduleId = :scheduleId")
    suspend fun setDefault(scheduleId: Long)

    @Query("UPDATE Schedules SET isDefaultSchedule = 0 WHERE ScheduleId != :scheduleId AND namedScheduleId = :namedScheduleId")
    suspend fun setNonDefault(scheduleId: Long, namedScheduleId: Long)

}