package com.egormelnikoff.schedulerutmiit.search.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.common.domain.SearchQuery
import com.egormelnikoff.schedulerutmiit.core.database.dao.SearchQueryDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.core.database.mapper.toEntity
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchQueryRepos
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class SearchQueryReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val searchQueryDao: SearchQueryDao
) : SearchQueryRepos {
    override suspend fun insert(searchQuery: SearchQuery) = db.withTransaction {
        searchQueryDao.getByApiId(searchQuery.apiId)?.let { savedQuery ->
            searchQueryDao.deleteById(savedQuery.id)
        }
        searchQueryDao.insert(searchQuery.toEntity())
    }

    override suspend fun getAll() = searchQueryDao.getAll().map { it.toDomain() }

    override fun observeAll(): Flow<List<SearchQuery>> =
        searchQueryDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun deleteAll() = searchQueryDao.deleteAll()

    override suspend fun deleteById(queryId: Long) =
        searchQueryDao.deleteById(queryId)
}