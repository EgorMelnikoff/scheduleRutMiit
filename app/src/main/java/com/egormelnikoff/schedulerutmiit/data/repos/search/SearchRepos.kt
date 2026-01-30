package com.egormelnikoff.schedulerutmiit.data.repos.search

import com.egormelnikoff.schedulerutmiit.app.model.Group
import com.egormelnikoff.schedulerutmiit.app.model.Institute
import com.egormelnikoff.schedulerutmiit.app.model.Institutes
import com.egormelnikoff.schedulerutmiit.app.model.Person
import com.egormelnikoff.schedulerutmiit.app.model.Subject
import com.egormelnikoff.schedulerutmiit.data.Result
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.ApiHelper
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.api.MiitApi
import com.egormelnikoff.schedulerutmiit.data.datasource.remote.parser.Parser
import javax.inject.Inject

interface SearchRepos {
    suspend fun getInstitutes(): Result<Institutes>
    suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>>

    suspend fun getPeopleByQuery(query: String): Result<List<Person>>
    suspend fun getSubjectsByCurriculum(id: String): Result<List<Subject>>
}

class SearchReposImpl @Inject constructor(
    private val miitApi: MiitApi,
    private val apiHelper: ApiHelper,
    private val parser: Parser
) : SearchRepos {
    override suspend fun getPeopleByQuery(query: String) = parser.parsePeople(query)

    override suspend fun getSubjectsByCurriculum(id: String) = parser.parseListSubjects(id)

    override suspend fun getGroupsByQuery(
        institutes: Institutes,
        query: String
    ): Result<List<Group>> {
        val groups = institutes.institutes?.let { getGroups(it) }
        val filteredGroups = groups?.filter {
            compareValues(it.name ?: "", query)
        } ?: listOf()
        return Result.Success(filteredGroups)
    }

    override suspend fun getInstitutes(): Result<Institutes> =
        apiHelper.callApiWithExceptions(
            requestType = "Institutes"
        ) { miitApi.getInstitutes() }

    private fun compareValues(comparableValue: String, query: String): Boolean {
        return comparableValue.lowercase().contains(query.lowercase())
    }

    private fun getGroups(institutes: List<Institute>): List<Group> {
        return institutes.flatMap { institute ->
            institute.courses?.flatMap { course ->
                course.specialties?.flatMap { specialty ->
                    specialty.groups ?: emptyList()
                } ?: emptyList()
            } ?: emptyList()
        }
    }
}
