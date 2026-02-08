package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.entity.Group
import com.egormelnikoff.schedulerutmiit.app.entity.SearchQuery
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result

interface SearchRepos {
    suspend fun fetchInstitutes(): Result<Institutes>
    suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>>

    suspend fun getPeopleByQuery(query: String): Result<List<Person>>
    suspend fun getSubjectsByCurriculum(id: String): Result<List<Subject>>

    suspend fun getAllSearchQuery(): List<SearchQuery>
    suspend fun saveSearchQuery (searchQuery: SearchQuery)
    suspend fun deleteSearchQuery(queryPrimaryKey: Long)
    suspend fun deleteAllSearchQuery()
}