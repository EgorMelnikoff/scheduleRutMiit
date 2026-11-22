package com.egormelnikoff.schedulerutmiit.search.data.remote

import com.egormelnikoff.schedulerutmiit.core.network.endpoint.Endpoints
import com.egormelnikoff.schedulerutmiit.core.network.fetcher.HtmlFetcher
import com.egormelnikoff.schedulerutmiit.search.data.remote.mapper.toDomain
import com.egormelnikoff.schedulerutmiit.search.data.remote.model.InstituteDto
import com.egormelnikoff.schedulerutmiit.search.data.utils.parser.SearchParser
import com.egormelnikoff.schedulerutmiit.search.domain.model.Group
import com.egormelnikoff.schedulerutmiit.search.domain.model.Person
import javax.inject.Inject

internal class SearchRemoteDataSource @Inject constructor(
    private val searchApi: SearchApi,
    private val htmlFetcher: HtmlFetcher
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

    @Deprecated("Пользоваться v2")
    suspend fun fetchAllGroups(): List<Group> {
        val institutes = searchApi.getInstitutes()

        return institutes.institutes.getGroups()
    }


    @Deprecated("Пользоваться v2")
    suspend fun fetchPeopleByQuery(query: String): List<Person> {
        val people = htmlFetcher(
            url = Endpoints.peopleUrl(query)
        )

        return SearchParser.parsePeople(people).map { p ->
            p.toDomain()
        }
    }

    private fun List<InstituteDto>.getGroups(): List<Group> = this.flatMap { institute ->
        institute.courses.flatMap { course ->
            course.specialties.flatMap { specialty ->
                specialty.groups.map { it.toDomain() }
            }
        }
    }
}