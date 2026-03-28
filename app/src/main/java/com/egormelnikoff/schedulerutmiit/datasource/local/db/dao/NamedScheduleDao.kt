package com.egormelnikoff.schedulerutmiit.datasource.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleEntity
import com.egormelnikoff.schedulerutmiit.app.entity.NamedScheduleFormatted

@Dao
interface NamedScheduleDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(namedScheduleEntity: NamedScheduleEntity): Long

    @Query("DELETE FROM NamedSchedules WHERE NamedScheduleId = :namedScheduleId")
    suspend fun deleteById(namedScheduleId: Long)

    @Query("SELECT * FROM NamedSchedules")
    suspend fun getAllEntities(): List<NamedScheduleEntity>

    @Transaction
    @Query("SELECT * FROM NamedSchedules WHERE apiId = :apiId")
    suspend fun getByApiId(apiId: Int): NamedScheduleFormatted?

    @Transaction
    @Query("SELECT * FROM NamedSchedules WHERE NamedScheduleId = :namedScheduleId")
    suspend fun getById(namedScheduleId: Long): NamedScheduleFormatted

    @Query("SELECT COUNT(*) FROM NamedSchedules")
    suspend fun getCount(): Int

    @Query("SELECT * FROM NamedSchedules WHERE isDefaultNamedSchedule = 1")
    suspend fun getDefault(): NamedScheduleEntity?

    @Query("UPDATE namedschedules SET lastTimeUpdate = :lastTimeUpdate WHERE NamedScheduleId = :namedScheduleId")
    suspend fun updateLastTimeUpdate(
        namedScheduleId: Long,
        lastTimeUpdate: Long = System.currentTimeMillis()
    )

    @Query("UPDATE namedschedules SET fullName = :fullName, shortName = :shortName WHERE NamedScheduleId = :namedScheduleId")
    suspend fun updateName(
        namedScheduleId: Long,
        fullName: String,
        shortName: String
    )

    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 1 WHERE NamedScheduleId = :namedScheduleId")
    suspend fun setDefault(namedScheduleId: Long)

    @Query("UPDATE NamedSchedules SET isDefaultNamedSchedule = 0 WHERE NamedScheduleId != :namedScheduleId")
    suspend fun setNonDefault(namedScheduleId: Long)

}