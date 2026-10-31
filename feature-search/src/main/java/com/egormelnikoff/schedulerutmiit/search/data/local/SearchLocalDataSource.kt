package com.egormelnikoff.schedulerutmiit.search.data.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.search.data.local.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.search.data.local.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class SearchLocalDataSource @Inject constructor(
    private val db: SearchQueryDatabase,
    private val searchQueryDao: SearchQueryDao
) {
    suspend fun insert(searchQuery: SearchQuery) = db.withTransaction {
        searchQueryDao.getByApiId(searchQuery.apiId)?.let { savedQuery ->
            searchQueryDao.deleteById(savedQuery.id)
        }
        searchQueryDao.insert(searchQuery.toEntity())
    }

    fun observeAll() =
        searchQueryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun deleteAll() = searchQueryDao.deleteAll()

    suspend fun deleteById(queryId: Long) = searchQueryDao.deleteById(queryId)
}