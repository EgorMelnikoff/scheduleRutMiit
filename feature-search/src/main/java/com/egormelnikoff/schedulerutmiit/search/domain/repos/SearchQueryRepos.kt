package com.egormelnikoff.schedulerutmiit.search.domain.repos

import com.egormelnikoff.schedulerutmiit.core.database.entity.SearchQuery

interface SearchQueryRepos {
    suspend fun insert(searchQuery: SearchQuery)
    suspend fun deleteById(queryId: Long)
    suspend fun deleteAll()
    suspend fun getAll(): List<SearchQuery>
}