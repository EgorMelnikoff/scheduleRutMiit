package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import com.egormelnikoff.schedulerutmiit.search.domain.model.SearchQuery
import kotlinx.coroutines.flow.Flow

internal interface SearchRepos {
    suspend fun saveToHistory(searchQuery: SearchQuery)
    fun observeHistory(): Flow<List<SearchQuery>>
    suspend fun clearHistory()
    suspend fun deleteQueryById(queryId: Long)

    suspend fun fetchAllGroups(): List<Group>
    suspend fun fetchPeopleByQuery(
        query: String
    ): List<Person>
}