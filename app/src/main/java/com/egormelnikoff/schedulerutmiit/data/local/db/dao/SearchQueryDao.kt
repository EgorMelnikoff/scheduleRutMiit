package com.egormelnikoff.schedulerutmiit.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.data.local.db.entity.SearchQuery

@Dao
interface SearchQueryDao {
    @Insert(onConflict = REPLACE)
    suspend fun insert(searchQuery: SearchQuery)

    @Query("DELETE FROM SearchHistory")
    suspend fun deleteAll()

    @Query("DELETE FROM SearchHistory WHERE id = :queryId")
    suspend fun deleteById(queryId: Long)

    @Query("SELECT * FROM SearchHistory ORDER BY id DESC")
    suspend fun getAll(): List<SearchQuery>

    @Query("SELECT * FROM SearchHistory WHERE apiId = :apiId")
    suspend fun getByApiId(apiId: Int): SearchQuery?
}