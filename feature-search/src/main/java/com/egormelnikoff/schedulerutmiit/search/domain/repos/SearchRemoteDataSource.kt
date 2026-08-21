package com.egormelnikoff.schedulerutmiit.search.domain.repos


import com.egormelnikoff.schedulerutmiit.core.common.domain.Group
import com.egormelnikoff.schedulerutmiit.core.common.domain.Person
import com.egormelnikoff.schedulerutmiit.core.common.result.Result

interface SearchRemoteDataSource {
    suspend fun fetchAllGroups(): Result<List<Group>>
    suspend fun fetchPeopleByQuery(query: String): Result<List<Person>>
}