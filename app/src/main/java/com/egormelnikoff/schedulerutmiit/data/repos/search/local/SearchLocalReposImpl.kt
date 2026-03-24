package com.egormelnikoff.schedulerutmiit.data.repos.search.local

import androidx.room.withTransaction
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.data.datasource.local.db.AppDatabase
import com.egormelnikoff.schedulerutmiit.data.datasource.local.db.Dao
import javax.inject.Inject

class SearchLocalReposImpl @Inject constructor(
    private val db: AppDatabase,
    private val dao: Dao
) : SearchLocalRepos {
    override suspend fun saveSearchQuery(searchQuery: SearchQuery) = db.withTransaction {
        dao.getSearchQueryByApiId(searchQuery.apiId)?.let { savedQuery ->
            dao.deleteSearchQuery(savedQuery.id)
        }
        dao.saveSearchQuery(searchQuery)
    }

    override suspend fun getAllSearchQuery() = dao.getAllSearchQuery()

    override suspend fun deleteAllSearchQuery() = dao.deleteAllSearchQuery()

    override suspend fun deleteSearchQuery(queryPrimaryKey: Long) =
        dao.deleteSearchQuery(queryPrimaryKey)

}