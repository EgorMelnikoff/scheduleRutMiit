package com.egormelnikoff.schedulerutmiit.data.repos.search.local

import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery

interface SearchLocalRepos {
    suspend fun getAllSearchQuery(): List<SearchQuery>
    suspend fun saveSearchQuery(searchQuery: SearchQuery)
    suspend fun deleteSearchQuery(queryPrimaryKey: Long)
    suspend fun deleteAllSearchQuery()
}