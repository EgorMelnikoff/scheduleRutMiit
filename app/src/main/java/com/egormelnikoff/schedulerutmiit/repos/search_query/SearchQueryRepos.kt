package com.egormelnikoff.schedulerutmiit.repos.search_query

import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery

interface SearchQueryRepos {
    suspend fun insert(searchQuery: SearchQuery)
    suspend fun deleteById(queryId: Long)
    suspend fun deleteAll()
    suspend fun getAll(): List<SearchQuery>
}
