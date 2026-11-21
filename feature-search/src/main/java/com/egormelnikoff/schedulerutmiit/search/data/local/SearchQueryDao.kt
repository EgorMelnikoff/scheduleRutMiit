package com.egormelnikoff.schedulerutmiit.search.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.egormelnikoff.schedulerutmiit.search.data.local.entity.SearchQueryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface SearchQueryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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