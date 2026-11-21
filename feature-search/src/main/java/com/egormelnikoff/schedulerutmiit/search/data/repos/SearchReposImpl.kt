package com.egormelnikoff.schedulerutmiit.search.data.repos

import com.egormelnikoff.schedulerutmiit.search.data.local.SearchLocalDataSource
import com.egormelnikoff.schedulerutmiit.search.data.remote.SearchRemoteDataSource
import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import com.egormelnikoff.schedulerutmiit.search.domain.repos.SearchRepos
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class SearchReposImpl @Inject constructor(
    private val searchLocalDataSource: SearchLocalDataSource,
    private val searchRemoteDataSource: SearchRemoteDataSource
) : SearchRepos {
    override suspend fun saveToHistory(searchQuery: SearchQuery) {
        searchLocalDataSource.insert(searchQuery)
    }

    override fun observeHistory(): Flow<List<SearchQuery>> = searchLocalDataSource.observeAll()

    override suspend fun clearHistory() {
        searchLocalDataSource.deleteAll()
    }

    override suspend fun deleteQueryById(queryId: Long) {
        searchLocalDataSource.deleteById(queryId)
    }

    override suspend fun fetchAllGroups(): List<Group> {
        return searchRemoteDataSource.fetchAllGroups()
    }

    override suspend fun fetchPeopleByQuery(
        query: String
    ): List<Person> {
        return searchRemoteDataSource.fetchPeopleByQuery(query)
    }
}