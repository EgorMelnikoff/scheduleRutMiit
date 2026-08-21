package com.egormelnikoff.schedulerutmiit.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchQueryDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(searchQuery: SearchQueryEntity)

    @Query("DELETE FROM SearchHistory")
    suspend fun deleteAll()

    @Query("DELETE FROM SearchHistory WHERE id = :queryId")
    suspend fun deleteById(queryId: Long)

    @Query("SELECT * FROM SearchHistory")
    fun observeAll(): Flow<List<SearchQueryEntity>>

    @Query("SELECT * FROM SearchHistory WHERE apiId = :apiId")
    suspend fun getByApiId(apiId: Int): SearchQueryEntity?
}