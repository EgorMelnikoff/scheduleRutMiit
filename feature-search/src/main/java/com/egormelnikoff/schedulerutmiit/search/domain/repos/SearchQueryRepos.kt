package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.core.common.domain.SearchQuery
import kotlinx.coroutines.flow.Flow

interface SearchQueryRepos {
    suspend fun insert(searchQuery: SearchQuery)
    suspend fun deleteById(queryId: Long)
    suspend fun deleteAll()
    suspend fun getAll(): List<SearchQuery>
    fun observeAll(): Flow<List<SearchQuery>>
}