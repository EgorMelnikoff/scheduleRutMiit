package com.egormelnikoff.schedulerutmiit.search.data.remote

import com.egormelnikoff.schedulerutmiit.search.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import javax.inject.Inject

internal class SearchRemoteDataSource @Inject constructor(
    private val searchApi: SearchApi,
) {
    suspend fun getGroupsV2(): List<Group> {
        val groups = searchApi.getGroups()
        return groups.map { it.toDomain() }
    }

    suspend fun getPeopleV2(query: String): List<Person> {
        val people = searchApi.getPeople(
            name = query
        )
        return people.simplePersons.map { it.toDomain() }
    }
}