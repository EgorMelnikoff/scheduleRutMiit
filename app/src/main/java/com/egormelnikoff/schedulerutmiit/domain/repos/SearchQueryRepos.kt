package com.egormelnikoff.schedulerutmiit.domain.repos

import com.egormelnikoff.schedulerutmiit.data.local.db.entity.SearchQuery

interface SearchQueryRepos {
    suspend fun insert(searchQuery: SearchQuery)
    suspend fun deleteById(queryId: Long)
    suspend fun deleteAll()
    suspend fun getAll(): List<SearchQuery>
}