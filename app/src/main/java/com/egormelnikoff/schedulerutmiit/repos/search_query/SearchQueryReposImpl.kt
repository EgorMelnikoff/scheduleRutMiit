package com.egormelnikoff.schedulerutmiit.repos.search_query

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.datasource.local.db.dao.SearchQueryDao
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