package com.egormelnikoff.schedulerutmiit.data.repos

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.core.database.dao.SearchQueryDao
import com.egormelnikoff.schedulerutmiit.core.database.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.domain.repos.SearchQueryRepos
import javax.inject.Inject

class SearchQueryReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val searchQueryDao: SearchQueryDao
) : SearchQueryRepos {
    override suspend fun insert(searchQuery: SearchQuery) = db.withTransaction {
        searchQueryDao.getByApiId(searchQuery.apiId)?.let { savedQuery ->
            searchQueryDao.deleteById(savedQuery.id)
        }
        searchQueryDao.insert(searchQuery)
    }

    override suspend fun getAll() = searchQueryDao.getAll()

    override suspend fun deleteAll() = searchQueryDao.deleteAll()

    override suspend fun deleteById(queryId: Long) =
        searchQueryDao.deleteById(queryId)
}